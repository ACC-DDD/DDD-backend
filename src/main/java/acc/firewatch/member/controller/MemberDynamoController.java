package acc.firewatch.member.controller;

import acc.firewatch.common.response.dto.CustomResponse;
import acc.firewatch.common.response.dto.SuccessStatus;
import acc.firewatch.member.dto.MemberDynamoDto;
import acc.firewatch.member.service.MemberDynamoService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberDynamoController {

    private final MemberDynamoService memberDynamoService;

    @Operation(summary = "단건 새로 저장 또는 덮어쓰는 API", description = "dynamo에 한 건을 새로 저장하거나 덮어씁니다.")
    @PostMapping
    public CustomResponse<String> save(@RequestBody MemberDynamoDto dto) {
        memberDynamoService.save(dto);
        return CustomResponse.success("MemberItem ID: " + dto.getId(), SuccessStatus.DYNAMO_MEMBER_SAVE);
    }

    @Operation(summary = "ID로 단건 조회 API", description = "파티션 키로 한 건을 조회합니다.")
    @GetMapping("/{id}")
    public CustomResponse<MemberDynamoDto> getById(@PathVariable Long id) {
        return CustomResponse.success(
                MemberDynamoDto.fromEntity(memberDynamoService.getById(id)),
                SuccessStatus.DYNAMO_MEMBER_GET
        );
    }

    @Operation(summary = "ID로 단건 레코드 삭제 API", description = "파티션 키에 해당하는 레코드를 삭제합니다.")
    @DeleteMapping("/{id}")
    public CustomResponse<String> deleteById(@PathVariable Long id) {
        memberDynamoService.deleteById(id);
        return CustomResponse.success("memberItem id = " + id + "삭제 완료", SuccessStatus.DYNAMO_MEMBER_DELETE);
    }

    @Operation(summary = "주소 기준 전체 MEMBER 조회 API", description = "주소(city + district)에 해당하는 전체 MEMBER를 조회합니다.")
    @GetMapping("/search")
    public CustomResponse<List<MemberDynamoDto>> findByAddress(@RequestParam String address) {
        return CustomResponse.success(
                memberDynamoService.findByAddress(address),
                SuccessStatus.DYNAMO_MEMBER_GET
        );
    }
}
