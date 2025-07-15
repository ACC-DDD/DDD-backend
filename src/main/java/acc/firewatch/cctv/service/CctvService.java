package acc.firewatch.cctv.service;

import acc.firewatch.cctv.repository.CctvRepository;
import acc.firewatch.cctv.dto.CctvRequestDto;
import acc.firewatch.cctv.dto.CctvResponseDto;
import acc.firewatch.cctv.entity.CctvItem;
import acc.firewatch.common.exception.CustomException;
import acc.firewatch.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class CctvService {

    private final CctvRepository cctvRepository;

    // Cctv 테이블 삭제
    public void deleteTable() {
        try {
            cctvRepository.deleteTable();
            System.out.println("✅ 테이블 삭제 완료");
        } catch (ResourceNotFoundException e) {
            System.out.println("⚠️ 테이블이 이미 존재하지 않음");
        }
    }

    // Cctv 테이블 생성
    public void createTable() {
        cctvRepository.createTable();
    }

    // 단건 새로 저장 또는 덮어쓰기
    public void save(CctvRequestDto requestDto) {
        cctvRepository.save(requestDto.toEntity());
    }

    // ID로 단건 조회
    public CctvItem getById(String id) {
        return cctvRepository.getById(id);
    }

    // 전체 CCTV 아이템 조회
    public List<CctvResponseDto> getAll() {
        return StreamSupport.stream(cctvRepository.getAll().spliterator(), false)
                .map(CctvResponseDto::fromEntity)
                .toList();
    }

    // 파라미터 district(군/구)에 해당하는 전체 cctv 아이템 조회
    public List<CctvResponseDto> findByDistrict(String district) {
        return StreamSupport.stream(cctvRepository.findByDistrict(district).spliterator(), false)
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

                cctvRepository.save(item);
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
        return StreamSupport.stream(cctvRepository.getAll().spliterator(), false)
                .map(CctvItem::getDistrict)
                .filter(d -> d != null && !d.trim().isEmpty())
                .distinct()
                .sorted()
                .toList();
    }

    // id에 해당하는 레코드 삭제
    public void deleteById(String id) {
        cctvRepository.deleteById(id);
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