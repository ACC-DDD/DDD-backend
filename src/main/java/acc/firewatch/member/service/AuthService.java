package acc.firewatch.member.service;

import acc.firewatch.common.exception.CustomException;
import acc.firewatch.common.exception.ErrorCode;
import acc.firewatch.config.jwt.JwtTokenProvider;
import acc.firewatch.member.dto.TokenResponse;
import acc.firewatch.member.entity.MemberItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    public TokenResponse reissueToken(String refreshToken) {

        // refresh 토큰 유효성 확인
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // claims에서 memberId 추출
        Long memberId = jwtTokenProvider.parseClaims(refreshToken).get("memberId", Long.class);

        // DynamoDB에서 사용자 정보 조회
        MemberItem memberItem = memberService.getById(memberId);
        if (memberItem == null || !refreshToken.equals(memberItem.getRefreshToken())) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        // 새 토큰 발급 및 저장
        String newAccessToken = jwtTokenProvider.generateToken(memberItem.getPhoneNum(), memberItem.getId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(memberItem.getPhoneNum(), memberItem.getId());

        // DynamoDB에 새로운 refreshToken 갱신
        memberService.updateRefreshToken(memberId, newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}