package acc.firewatch.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    TEST_ERROR_CODE(400, "응답 테스트 실패입니다."),

    // auth
    ALREADY_EXIST_MEMBER(400,"이미 가입된 전화번호(멤버)입니다."),
    PASSWORD_CONFIRM_MISMATCH(400,"비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    LOGIN_FAILED(400,"전화번호 또는 비밀번호가 일치하지 않습니다."),

    // member
    MEMBER_NOT_FOUND(404,"멤버 정보를 찾을 수 없습니다."),
    PASSWORD_MISMATCH(400,"현재 비밀번호가 일치하지 않습니다."),

    // jwt
    NOT_FOUND_JWT_SECRET(404, "Jwt Secret 키를 찾을 수 없습니다. yml 파일에 설정해주세요."),
    TOKEN_VALIDATE_FAILED(400,"Jwt Token이 유효하지 않습니다."),
    NOT_FOUND_AUTHORIZATION_HEADER(401, "Authorization 헤더가 없습니다."),
    NULL_POINT_HEADER_REQUEST(403,"헤더에 값이 없습니다."),
    JWT_PARSING_FAILED(403,"토큰 파싱 중 예외가 발생했습니다."),
    JWT_EXPIRED(403,"토큰이 만료되었습니다."),
    INVALID_JWT_TOKEN(401,"유효하지 않은 JWT 토큰입니다."),
    INVALID_REFRESH_TOKEN(400,"유효하지 않은 refresh 토큰입니다."),
    REFRESH_TOKEN_MISMATCH(400,"멤버에 저장된 refresh 토큰과 일치하지 않습니다."),

    // cctv
    CCTV_CSV_SAVE_ERROR(500,"CCTV CSV 저장 중 오류 발생"),
    DYNAMO_CCTV_NOT_FOUND(400, "DynamoDB에서 해당 cctvItem을 찾을 수 없습니다."),
    CSV2DYNAMO_SAVE_FAIL( 500,"csv 업로드 중 서버에서 에러가 발생했습니다."),
    INVALID_DISTRICT(400,"존재하지 않는 행정구역입니다."),
    ;

    private final int code;
    private final String message;
}