package acc.firewatch.member.service;

import acc.firewatch.common.exception.CustomException;
import acc.firewatch.common.exception.ErrorCode;
import acc.firewatch.config.jwt.JwtTokenProvider;
import acc.firewatch.member.dto.MemberDynamoDto;
import acc.firewatch.member.dto.TokenResponse;
import acc.firewatch.member.entity.Member;
import acc.firewatch.member.entity.MemberItem;
import acc.firewatch.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final MemberDynamoService memberDynamoService;

    public TokenResponse reissueToken(String refreshToken) {

        // refresh 토큰 유효성 확인
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // claims에서 memberId 추출
        Long memberId = jwtTokenProvider.parseClaims(refreshToken).get("memberId", Long.class);

        // DynamoDB에 저장된 refresh 토큰과 비교
        MemberItem memberItem = memberDynamoService.getById(memberId);
        if (memberItem == null || !refreshToken.equals(memberItem.getRefreshToken())) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_MISMATCH);
        }

        // MySQL에서 사용자 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 새 토큰 발급 및 저장
        String newAccessToken = jwtTokenProvider.generateToken(member.getPhoneNum(), member.getId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(member.getPhoneNum(), member.getId());

        // DynamoDB에 새로운 refreshToken 갱신
        MemberDynamoDto updated = MemberDynamoDto.builder()
                .id(member.getId())
                .name(member.getName())
                .phoneNum(member.getPhoneNum())
                .address(memberItem.getAddress()) // 기존 address 유지
                .refreshToken(newRefreshToken)
                .build();
        memberDynamoService.save(updated);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }
}