package acc.firewatch.member.controller;

import acc.firewatch.common.response.dto.CustomResponse;
import acc.firewatch.common.response.dto.SuccessStatus;
import acc.firewatch.member.dto.*;
import acc.firewatch.member.entity.MemberItem;
import acc.firewatch.member.service.AuthService;
import acc.firewatch.member.service.MemberDynamoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberDynamoController {

    private final MemberDynamoService memberDynamoService;
    private final AuthService authService;

    @Operation(summary = "회원가입 API", description = "회원가입 API 입니다 ㅎㅎㅎ")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "비밀번호와 비밀번호 확인이 일치하지 않습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "code": 400,
          "message": "비밀번호와 비밀번호 확인이 일치하지 않습니다."
        }"""))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "이미 가입된 전화번호(멤버)입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "code": 400,
          "message": "이미 가입된 전화번호(멤버)입니다."
        }"""))
            )
    })
    @PostMapping("/auth/signup")
    public CustomResponse<MemberResponseDto> signUp(@RequestBody MemberRequestDto requestDto) {
        MemberResponseDto responseDto = memberDynamoService.signUp(requestDto);
        return CustomResponse.success(responseDto, SuccessStatus.SIGNUP_MEMBER_OK);
    }

    @Operation(summary = "로그인 API", description = "로그인 API 입니다!!!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "전화번호 또는 비밀번호가 일치하지 않습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "code": 400,
          "message": "전화번호 또는 비밀번호가 일치하지 않습니다."
        }"""))
            )
    })
    @PostMapping("/auth/login")
    public CustomResponse<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        LoginResponseDto responseDto = memberDynamoService.login(requestDto);
        return CustomResponse.success(responseDto, SuccessStatus.LOGIN_MEMBER_OK);
    }

    @Operation(summary = "토큰 재발급 API", description = "유효한 refreshToken을 통해 accessToken과 refreshToken을 재발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(
                    responseCode = "400",
                    description = "유효하지 않은 refresh 토큰입니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
              "code": 400,
              "message": "유효하지 않은 refresh 토큰입니다."
            }"""))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "멤버에 저장된 refresh 토큰과 일치하지 않습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
              "code": 400,
              "message": "멤버에 저장된 refresh 토큰과 일치하지 않습니다."
            }"""))
            )
    })
    @PostMapping("/auth/reissue")
    public CustomResponse<TokenResponse> reissue(@RequestBody TokenReissueRequest request) {
        TokenResponse response = authService.reissueToken(request.getRefreshToken());
        return CustomResponse.success(response, SuccessStatus.TOKEN_REISSUE_OK);
    }

    @Operation(summary = "로그아웃 API", description = "로그아웃 API 입니다~")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "멤버 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
              "code": 404,
              "message": "멤버 정보를 찾을 수 없습니다."
            }"""))
            )
    })
    @PostMapping("/me/logout")
    public CustomResponse<?> logout() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String phoneNum = (String) auth.getPrincipal();
        memberDynamoService.logout(phoneNum);
        return CustomResponse.success(SuccessStatus.LOGOUT_MEMBER_OK);
    }

    @Operation(summary = "멤버 정보 조회 API", description = "멤버 정보를 조회하는 API입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 정보 조회 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "멤버 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "code": 404,
          "message": "멤버 정보를 찾을 수 없습니다."
        }"""))
            )
    })
    @GetMapping("/me")
    public CustomResponse<MemberResponseDto> getMyInfo() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String phoneNum = (String) auth.getPrincipal(); // Jwt에서 설정한 값

        return CustomResponse.success(memberDynamoService.getMyInfo(phoneNum), SuccessStatus.GET_MY_INFO_OK);
    }

    @Operation(summary = "멤버 정보 수정 API", description = "멤버 정보를 수정하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "멤버 정보 수정 성공"),
            @ApiResponse(
                    responseCode = "404",
                    description = "멤버 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
        {
          "code": 404,
          "message": "멤버 정보를 찾을 수 없습니다."
        }"""))
            )
    })
    @PatchMapping("/me")
    public CustomResponse<MemberResponseDto> updateMyInfo(@RequestBody MemberUpdateRequestDto requestDto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String phoneNum = (String) auth.getPrincipal();
        MemberResponseDto responseDto = memberDynamoService.updateMyInfo(phoneNum, requestDto);
        return CustomResponse.success(responseDto, SuccessStatus.UPDATE_MY_INFO_OK);
    }

    @Operation(summary = "비밀번호 변경 API", description = "비밀번호를 변경하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호가 성공적으로 변경되었습니다."),
            @ApiResponse(
                    responseCode = "404",
                    description = "멤버 정보를 찾을 수 없습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
              "code": 404,
              "message": "멤버 정보를 찾을 수 없습니다."
            }"""))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "비밀번호와 비밀번호 확인이 일치하지 않습니다.",
                    content = @Content(mediaType = "application/json", examples = @ExampleObject(value = """
            {
              "code": 400,
              "message": "비밀번호와 비밀번호 확인이 일치하지 않습니다."
            }"""))
            )
    })
    @PatchMapping("/me/password")
    public CustomResponse<?> changePassword(@RequestBody PasswordChangeRequestDto dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String phoneNum = (String) auth.getPrincipal();

        memberDynamoService.changePassword(phoneNum, dto);
        return CustomResponse.success(SuccessStatus.UPDATE_PASSWORD);
    }

    @Operation(summary = "단건 새로 저장 또는 덮어쓰는 API", description = "dynamo에 한 건을 새로 저장하거나 덮어씁니다.")
    @PostMapping
    public CustomResponse<MemberResponseDto> save(@RequestBody MemberRequestDto dto) {
        MemberResponseDto saved = memberDynamoService.signUp(dto);
        return CustomResponse.success(saved, SuccessStatus.DYNAMO_MEMBER_SAVE);
    }

    @Operation(summary = "ID로 단건 조회 API", description = "파티션 키로 한 건을 조회합니다.")
    @GetMapping("/{id}")
    public CustomResponse<MemberResponseDto> getById(@PathVariable Long id) {
        MemberItem member = memberDynamoService.getById(id);

        return CustomResponse.success(
                MemberResponseDto.builder()
                        .id(member.getId())
                        .name(member.getName())
                        .phoneNum(member.getPhoneNum())
                        .verified(member.isVerified())
                        .city(member.getCity())
                        .district(member.getDistrict())
                        .detail(member.getDetail())
                        .build(),
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
    public CustomResponse<MemberAddressSearchResponseDto> findByAddress(@RequestParam String address) {
        return CustomResponse.success(
                memberDynamoService.findByAddress(address),
                SuccessStatus.DYNAMO_MEMBER_GET
        );
    }
}
