package acc.firewatch.cctv.service;

import acc.firewatch.cctv.dto.CctvRequestDto;
import acc.firewatch.cctv.dto.CctvResponseDto;
import acc.firewatch.cctv.entity.CctvItem;
import acc.firewatch.common.exception.CustomException;
import acc.firewatch.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class CctvDynamoService {

    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbEnhancedClient enhancedClient;
    private static final String TABLE_NAME = "Cctv";
    private static final String GSI_NAME = "district-index";

    private DynamoDbTable<CctvItem> getTable() {
        return enhancedClient
                .table(TABLE_NAME, TableSchema.fromBean(CctvItem.class));
    }

    // Cctv 테이블 삭제
    public void deleteTable() {
        try {
            DeleteTableRequest request = DeleteTableRequest.builder()
                    .tableName(TABLE_NAME)
                    .build();
            dynamoDbClient.deleteTable(request);
            System.out.println("✅ 테이블 삭제 완료");
        } catch (ResourceNotFoundException e) {
            System.out.println("⚠️ 테이블이 이미 존재하지 않음");
        }
    }

    // Cctv 테이블 생성
    public void createTable() {
        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(TABLE_NAME)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("id")
                        .keyType(KeyType.HASH)
                        .build())
                .attributeDefinitions(
                        AttributeDefinition.builder()
                                .attributeName("id")
                                .attributeType(ScalarAttributeType.S) // 기본 키(String)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName("district")
                                .attributeType(ScalarAttributeType.S) // GSI 키(String)
                                .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST) // 온디맨드 모드
                .globalSecondaryIndexes(GlobalSecondaryIndex.builder()
                        .indexName(GSI_NAME)
                        .keySchema(KeySchemaElement.builder()
                                .attributeName("district")
                                .keyType(KeyType.HASH)
                                .build())
                        .projection(Projection.builder()
                                .projectionType(ProjectionType.ALL)
                                .build())
                        .build())
                .build();

        dynamoDbClient.createTable(request);
        System.out.println("✅ 테이블 생성 완료");
    }

    // 단건 새로 저장 또는 덮어쓰기
    public void save(CctvRequestDto requestDto) {
        getTable().putItem(requestDto.toEntity());
    }

    // ID로 단건 조회
    public CctvItem getById(String id) {
        return getTable().getItem(r -> r.key(k -> k.partitionValue(id)));
    }

    // 전체 CCTV 아이템 조회
    public List<CctvResponseDto> getAll() {
        return StreamSupport.stream(getTable().scan().items().spliterator(), false)
                .map(CctvResponseDto::fromEntity)
                .toList();
    }

    // 파라미터 district(군/구)에 해당하는 전체 cctv 아이템 조회
    public List<CctvResponseDto> getByDistrict(String district) {
        QueryConditional query = QueryConditional.keyEqualTo(Key.builder().partitionValue(district).build());

        SdkIterable<Page<CctvItem>> results = getTable()
                .index("DistrictIndex")
                .query(query);

        return StreamSupport.stream(results.spliterator(), false)
                .flatMap(page -> page.items().stream())
                .map(CctvResponseDto::fromEntity)
                .toList();
    }

    // cctv csv -> dynamo 일괄 업로드
    public boolean uploadCsvToDynamo(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            reader.readLine(); // skip header

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length < 9) continue;

                CctvItem item = CctvItem.builder()
                        .id(parts[0])
                        .name(parts[1])
                        .latitude(Double.parseDouble(parts[2]))
                        .longitude(Double.parseDouble(parts[3]))
                        .cctvUrl(parts[4])
                        .city(parts[5])
                        .district(parts[6])
                        .town(parts[7])
                        .status(Boolean.parseBoolean(parts[8]))
                        .build();

                getTable().putItem(item);
            }

            log.info("✅ CSV 데이터를 DynamoDB에 성공적으로 업로드했습니다.");
            return true;
        } catch (Exception e) {
            log.error("❌ CSV → Dynamo 업로드 중 오류 발생: {}", e.getMessage(), e);
            return false;
        }
    }

    // 모든 district 값 조회
    public List<String> getAllDistricts() {
        return getTable().scan().items().stream()
                .map(CctvItem::getDistrict)                     // district 필드만 추출
                .filter(d -> d != null && !d.trim().isEmpty())  // null/빈 문자열 제외
                .distinct()                                     // 중복 제거
                .sorted()                                       // 알파벳 순 정렬
                .toList();
    }

    // id에 해당하는 레코드 삭제
    public void deleteById(String id) {
        getTable().deleteItem(r -> r.key(k -> k.partitionValue(id)));
        log.info("✅ 삭제 완료: id = {}", id);
    }

    // id에 해당하는 cctvUrl 조회
    public String getStreamUrlById(String id) {
        CctvItem item = getById(id); // 기존 단건 조회 재사용
        if (item == null || item.getCctvUrl() == null || item.getCctvUrl().isBlank()) {
            throw new CustomException(ErrorCode.DYNAMO_CCTV_NOT_FOUND);
        }
        return item.getCctvUrl();
    }

}