package acc.firewatch.common.response.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessStatus {
    SUCCESS(200,"성공"),

    // auth
    SIGNUP_MEMBER_OK(200,"회원가입 성공"),
    LOGIN_MEMBER_OK(200,"로그인 성공"),
    TOKEN_REISSUE_OK(200,"토큰 재발급 성공"),
    LOGOUT_MEMBER_OK(200,"로그아웃 성공"),

    // member
    GET_MY_INFO_OK(200,"멤버 정보 조회 성공"),
    UPDATE_MY_INFO_OK(200,"멤버 정보 수정 성공"),
    UPDATE_PASSWORD(200,"비밀번호가 성공적으로 변경되었습니다."),
    DYNAMO_MEMBER_SAVE(201,"memberItem 저장 성공"),
    DYNAMO_MEMBER_GET(200,"memberItem 조회 성공"),
    DYNAMO_MEMBER_DELETE(200,"memberItem 삭제 성공"),

    // cctv
    CREATE_DYNAMO_CCTV_TABLE(200,"DynamoDB cctv 테이블 생성 성공"),
    DELETE_DYNAMO_CCTV_TABLE(200,"DynamoDB cctv 테이블 삭제 성공"),
    CCTV_CSV_EXPORT(200,"전체 cctv csv 추출 성공"),
    DYNAMO_CCTV_SAVE(201,"cctvItem 저장 성공"),
    DYNAMO_CCTV_GET(200,"cctvItem 조회 성공"),
    DYNAMO_CCTVURL_GET(200,"cctvUrl 조회 성공"),
    CSV2DYNAMO_SAVE(201,"CSV -> DynamoDB 업로드 성공"),
    DYNAMO_CCTV_DELETE(200, "cctvItem 삭제 성공"),
    ;

    private final int code;
    private final String message;
}
