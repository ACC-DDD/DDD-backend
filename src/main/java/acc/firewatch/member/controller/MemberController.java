package acc.firewatch.member.controller;

import acc.firewatch.common.response.dto.CustomResponse;
import acc.firewatch.common.response.dto.SuccessStatus;
import acc.firewatch.config.security.SecurityUtil;
import acc.firewatch.member.dto.*;
import acc.firewatch.member.service.AuthService;
import acc.firewatch.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
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
    public CustomResponse<MemberResponseDto> signUp(@Valid @RequestBody MemberRequestDto requestDto) {
        MemberResponseDto responseDto = memberService.signUp(requestDto);
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
        LoginResponseDto responseDto = memberService.login(requestDto);
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
    public CustomResponse<TokenReissueResponseDto> reissue(@RequestBody TokenReissueRequestDto request) {
        TokenReissueResponseDto response = authService.reissueToken(request.getRefreshToken());
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
        Long memberId = SecurityUtil.getCurrentMemberId();
        authService.logout(memberId);
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
        String phoneNum = SecurityUtil.getCurrentPhoneNum();
        return CustomResponse.success(memberService.getMyInfo(phoneNum), SuccessStatus.GET_MY_INFO_OK);
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
        String phoneNum = SecurityUtil.getCurrentPhoneNum();
        MemberResponseDto responseDto = memberService.updateMyInfo(phoneNum, requestDto);
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
        String phoneNum = SecurityUtil.getCurrentPhoneNum();
        memberService.changePassword(phoneNum, dto);
        return CustomResponse.success(SuccessStatus.UPDATE_PASSWORD);
    }

    @Operation(summary = "주소 기준 전체 MEMBER 조회 API", description = "주소(city + district)에 해당하는 전체 MEMBER를 조회합니다.")
    @GetMapping("/search")
    public CustomResponse<MemberAddressSearchResponseDto> findByAddress(@RequestParam String address) {
        return CustomResponse.success(
                memberService.findByAddress(address),
                SuccessStatus.DYNAMO_MEMBER_GET
        );
    }

    @Operation(summary = "ID로 멤버 레코드 삭제 API", description = "멤버 테이블에서 파티션 키에 해당하는 레코드를 삭제합니다.")
    @DeleteMapping("/members/{id}")
    public CustomResponse<String> deleteById(@PathVariable Long id) {
        memberService.deleteById(id);
        return CustomResponse.success("memberItem id = " + id + "삭제 완료", SuccessStatus.DYNAMO_MEMBER_DELETE);
    }
}
