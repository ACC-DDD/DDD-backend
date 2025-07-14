package acc.firewatch.cctv.service;

import acc.firewatch.cctv.entity.Cctv;
import acc.firewatch.common.exception.CustomException;
import acc.firewatch.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class CctvExportService {

    public void exportToCsv(List<Cctv> cctvs, String filePath) {

        // CSV 출력용 순번 카운터
        final AtomicInteger sequence = new AtomicInteger(1);

        List<Cctv> validCctvs = cctvs.stream()
                .filter(c -> c.getDistrict() != null && !c.getDistrict().trim().isEmpty())
                .toList();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("id,name,latitude,longitude,cctvUrl,city,district,town,status\n");

            for (Cctv cctv : validCctvs) {
                writer.write(String.format("%d,%s,%.6f,%.6f,%s,%s,%s,%s,%b\n",
                        sequence.getAndIncrement(),
                        sanitize(cctv.getName()),
                        cctv.getLatitude(),
                        cctv.getLongitude(),
                        sanitize(cctv.getCctvUrl()),
                        sanitize(cctv.getCity()),
                        sanitize(cctv.getDistrict()),
                        sanitize(cctv.getTown()),
                        cctv.isStatus()
                ));
            }

            log.info("✅ CSV 저장 완료: {}", filePath);
        } catch (IOException e) {
            log.error("❌ CSV 저장 중 오류 발생", e);
            throw new CustomException(ErrorCode.CCTV_CSV_SAVE_ERROR);
        }
    }

    private String sanitize(String input) {
        return input == null ? "" : input.replace(",", " ").replace("\n", " ").trim();
    }
}
