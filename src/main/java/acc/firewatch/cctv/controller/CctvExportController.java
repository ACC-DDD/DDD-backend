package acc.firewatch.cctv.controller;

import acc.firewatch.cctv.entity.Cctv;
import acc.firewatch.cctv.service.CctvExportService;
import acc.firewatch.external.its.TrafficCctvFetcher;
import acc.firewatch.common.exception.CustomException;
import acc.firewatch.common.exception.ErrorCode;
import acc.firewatch.common.response.dto.CustomResponse;
import acc.firewatch.common.response.dto.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cctvs/export")
@RequiredArgsConstructor
public class CctvExportController {

    private final TrafficCctvFetcher fetcher;
    private final CctvExportService exportService;

    @Operation(summary = "CCTV 데이터 CSV 파일 추출 API", description = "외부 API로부터 CCTV 정보를 모으고 CSV 파일로 만듭니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 cctv csv 추출 성공"),
            @ApiResponse(
                    responseCode = "500",
                    description = "CCTV CSV 저장 중 오류 발생",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
    {
      "code": 500,
      "message": "CCTV CSV 저장 중 오류 발생"
    }"""))
            )
    })
    @PostMapping
    public CustomResponse<?> exportCctvToCsv() {
        try {
            System.out.println("🚀 CCTV 데이터 수집 시작...");
            List<Cctv> cctvs = fetcher.fetchAllCctvsWithAddress();

            String filePath = "src/main/resources/cctv-merged.csv";
            exportService.exportToCsv(cctvs, filePath);

            return CustomResponse.success("✅ CCTV 데이터를 CSV로 저장했습니다. (파일 경로: " + filePath + ")", SuccessStatus.CCTV_CSV_EXPORT);
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException(ErrorCode.CCTV_CSV_SAVE_ERROR);
        }
    }
}
