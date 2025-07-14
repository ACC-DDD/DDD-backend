package acc.firewatch.member.service;

import acc.firewatch.cctv.dto.CctvRequestDto;
import acc.firewatch.cctv.dto.CctvResponseDto;
import acc.firewatch.cctv.entity.CctvItem;
import acc.firewatch.member.dto.MemberDynamoDto;
import acc.firewatch.member.entity.MemberItem;
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

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberDynamoService {
    private final DynamoDbEnhancedClient enhancedClient;
    private static final String TABLE_NAME = "Member";

    private DynamoDbTable<MemberItem> getTable() {
        return enhancedClient
                .table(TABLE_NAME, TableSchema.fromBean(MemberItem.class));
    }

    // 단건 새로 저장 또는 덮어쓰기
    public void save(MemberDynamoDto requestDto) {
        getTable().putItem(requestDto.toEntity());
    }

    // ID로 단건 조회
    public MemberItem getById(Long id) {
        return getTable().getItem(r -> r.key(k -> k.partitionValue(id)));
    }

    // id에 해당하는 레코드 삭제
    public void deleteById(Long id) {
        getTable().deleteItem(r -> r.key(k -> k.partitionValue(id)));
        log.info("✅ 삭제 완료: id = {}", id);
    }

    // 파라미터 address(city+district)에 해당하는 전체 member 아이템 조회
    public List<MemberDynamoDto> findByAddress(String address) {
        QueryConditional query = QueryConditional.keyEqualTo(Key.builder().partitionValue(address).build());

        SdkIterable<Page<MemberItem>> results = getTable()
                .index("address-index")
                .query(query);

        return StreamSupport.stream(results.spliterator(), false)
                .flatMap(page -> page.items().stream())
                .map(MemberDynamoDto::fromEntity)
                .toList();
    }

}
