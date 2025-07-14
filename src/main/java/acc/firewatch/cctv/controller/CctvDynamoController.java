package acc.firewatch.cctv.controller;

import acc.firewatch.cctv.dto.CctvRequestDto;
import acc.firewatch.cctv.dto.CctvResponseDto;
import acc.firewatch.cctv.entity.CctvItem;
import acc.firewatch.cctv.service.CctvDynamoService;
import acc.firewatch.common.exception.CustomException;
import acc.firewatch.common.exception.ErrorCode;
import acc.firewatch.common.response.dto.CustomResponse;
import acc.firewatch.common.response.dto.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cctvs")
@RequiredArgsConstructor
public class CctvDynamoController {

    private final CctvDynamoService cctvDynamoService;

    @Operation(summary = "dynamo Cctv 테이블 생성 API", description = "dynamo에 Cctv 테이블을 생성합니다.")
    @PostMapping("/table")
    public CustomResponse<Void> createTable() {
        cctvDynamoService.createTable();
        return CustomResponse.success(SuccessStatus.CREATE_DYNAMO_CCTV_TABLE);
    }

    @Operation(summary = "dynamo Cctv 테이블 삭제 API", description = "dynamo에 Cctv 테이블을 삭제합니다.")
    @DeleteMapping("/table")
    public CustomResponse<Void> deleteTable() {
        cctvDynamoService.deleteTable();
        return CustomResponse.success(null, SuccessStatus.DELETE_DYNAMO_CCTV_TABLE);
    }

    @Operation(summary = "단건 새로 저장 또는 덮어쓰는 API", description = "dynamo에 한 건을 새로 저장하거나 덮어씁니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "cctvItem 저장 성공"),
            @ApiResponse(
                    responseCode = "500",
                    description = "DynamoDB 또는 서버 오류입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "code": 500,
          "message": "DynamoDB 또는 서버 오류입니다."
        }"""))
            )
    })
    @PostMapping
    public CustomResponse<String> save(@RequestBody CctvRequestDto requestDto) {
        cctvDynamoService.save(requestDto);
        return CustomResponse.success("cctvItem ID: " + requestDto.getId(), SuccessStatus.DYNAMO_CCTV_SAVE);
    }


    @Operation(summary = "ID로 단건 조회 API", description = "파티션 키로 한 건을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "cctvItem 조회 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "DynamoDB에서 해당 cctvItem을 찾을 수 없습니다",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "code": 400,
          "message": "DynamoDB에서 해당 cctvItem을 찾을 수 없습니다"
        }"""))
            )
    })
    @GetMapping("/{id}")
    public CustomResponse<CctvItem> getById(@PathVariable String id) {
        CctvItem item = cctvDynamoService.getById(id);
        if (item == null) {
            throw new CustomException(ErrorCode.DYNAMO_CCTV_NOT_FOUND);
        }
        return CustomResponse.success(item,SuccessStatus.DYNAMO_CCTV_GET);
    }


    @Operation(summary = "전체 CCTV 조회 API", description = "전체 CCTV를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "cctvItem 조회 성공"),
            @ApiResponse(
                    responseCode = "500",
                    description = "DynamoDB 또는 서버 오류입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "code": 500,
          "message": "DynamoDB 또는 서버 오류입니다."
        }"""))
            )
    })
    @Parameters({
            @Parameter(name = "district", description = "시/군/구, path variable 입니다!")
    })
    @GetMapping
    public CustomResponse<List<CctvResponseDto>> getAll(@RequestParam(required = false) String district) {

        // 검증 안함 → 있는 값이면 조회, 없으면 빈 리스트 반환
        if (district != null && !district.isBlank()) {
            return CustomResponse.success(
                    cctvDynamoService.getByDistrict(district),
                    SuccessStatus.DYNAMO_CCTV_GET
            );
        }

        return CustomResponse.success(
                cctvDynamoService.getAll(),
                SuccessStatus.DYNAMO_CCTV_GET
        );
    }


    @Operation(summary = "DynamoDB에 CCTV 데이터 업로드 API", description = "cctv 데이터 파일을 dynamoDB에 일괄 업로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "csv -> dynamo 업로드 성공"),
            @ApiResponse(
                    responseCode = "500",
                    description = "csv 업로드 중 서버에서 에러가 발생했습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "code": 500,
          "message": "csv 업로드 중 서버에서 에러가 발생했습니다."
        }"""))
            )
    })
    @PostMapping("/import-csv")
    public CustomResponse<?> uploadCsv(@RequestParam(defaultValue = "src/main/resources/cctv-merged.csv") String path) {
        if(cctvDynamoService.uploadCsvToDynamo(path)){
            return CustomResponse.success(SuccessStatus.CSV2DYNAMO_SAVE);
        }
        throw new CustomException(ErrorCode.CSV2DYNAMO_SAVE_FAIL);
    }


    @Operation(summary = "전체 district 조회 API", description = "중복 없이 전체 GSI 값을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "cctvItem 조회 성공"),
            @ApiResponse(
                    responseCode = "500",
                    description = "DynamoDB 또는 서버 오류입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "code": 500,
          "message": "DynamoDB 또는 서버 오류입니다."
        }"""))
            )
    })
    @GetMapping("/districts")
    public CustomResponse<List<String>> getAllDistricts() {
        List<String> districts = cctvDynamoService.getAllDistricts();
        return CustomResponse.success(districts, SuccessStatus.DYNAMO_CCTV_GET);
    }


    @Operation(summary = "ID로 단건 레코드 삭제 API", description = "파티션 키에 해당하는 레코드를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "cctvItem 삭제 성공"),
            @ApiResponse(
                    responseCode = "500",
                    description = "DynamoDB 또는 서버 오류입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "code": 500,
          "message": "DynamoDB 또는 서버 오류입니다."
        }"""))
            )
    })
    @DeleteMapping("/{id}")
    public CustomResponse<Void> deleteCctv(@PathVariable String id) {
        cctvDynamoService.deleteById(id);
        return CustomResponse.success(null, SuccessStatus.DYNAMO_CCTV_DELETE);
    }


    @Operation(summary = "ID로 cctvUrl 조회 API", description = "파티션 키에 해당하는 cctvUrl을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "cctvUrl 조회 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "dynamo에서 해당 cctvItem을 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "code": 400,
          "message": "dynamo에서 해당 cctvItem을 찾을 수 없습니다."
        }"""))
            )
    })
    @GetMapping("/{id}/stream")
    public CustomResponse<String> getCctvStreamUrl(@PathVariable String id) {
        String streamUrl = cctvDynamoService.getStreamUrlById(id);
        return CustomResponse.success(streamUrl, SuccessStatus.DYNAMO_CCTVURL_GET);
    }

}
