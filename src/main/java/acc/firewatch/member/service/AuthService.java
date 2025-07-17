package acc.firewatch.member.service;

import acc.firewatch.common.exception.CustomException;
import acc.firewatch.common.exception.ErrorCode;
import acc.firewatch.config.jwt.JwtTokenProvider;
import acc.firewatch.member.dto.TokenReissueResponseDto;
import acc.firewatch.member.entity.MemberItem;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    public TokenReissueResponseDto reissueToken(String refreshToken) {

        // refresh 토큰 유효성 확인
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // claims에서 memberId 추출
        Long memberId = jwtTokenProvider.parseClaims(refreshToken).get("memberId", Long.class);

        // Redis에서 refresh token 조회
        String redisKey = "refresh_token:" + memberId;
        String storedRefreshToken = redisTemplate.opsForValue().get(redisKey);

        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        // 사용자 정보 조회
        MemberItem memberItem = memberService.getById(memberId);
        if (memberItem == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // 새 토큰 발급 및 저장
        String newAccessToken = jwtTokenProvider.generateToken(memberItem.getPhoneNum(), memberItem.getId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(memberItem.getPhoneNum(), memberItem.getId());

        // Redis에 새 Refresh Token 저장 (TTL 7일)
        redisTemplate.delete(redisKey);
        redisTemplate.opsForValue().set(redisKey, newRefreshToken, Duration.ofDays(7));

        return new TokenReissueResponseDto(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(Long memberId) {
        String redisKey = "refresh_token:" + memberId;
        redisTemplate.delete(redisKey);
    }

}