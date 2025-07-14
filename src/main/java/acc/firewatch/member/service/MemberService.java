package acc.firewatch.member.service;

import acc.firewatch.common.exception.CustomException;
import acc.firewatch.common.exception.ErrorCode;
import acc.firewatch.config.jwt.JwtTokenProvider;
import acc.firewatch.member.dto.*;
import acc.firewatch.member.entity.Address;
import acc.firewatch.member.entity.Member;
import acc.firewatch.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberDynamoService memberDynamoService;

    // 회원가입
    public MemberResponseDto signUp(MemberRequestDto dto) {

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        if (memberRepository.existsByPhoneNum(dto.getPhoneNum())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_MEMBER);
        }

        Member member = Member.builder()
                .name(dto.getName())
                .phoneNum(dto.getPhoneNum())
                .password(passwordEncoder.encode(dto.getPassword()))
                .address(Address.builder()
                        .city(dto.getCity())
                        .district(dto.getDistrict())
                        .detail(dto.getDetail())
                        .build())
                .verified(false)
                .build();

        Member saved = memberRepository.save(member);

        String addressForDynamo = dto.getCity() + " " + dto.getDistrict();
        MemberDynamoDto dynamoDto = MemberDynamoDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .phoneNum(saved.getPhoneNum())
                .address(addressForDynamo)
                .refreshToken(null)
                .build();
        memberDynamoService.save(dynamoDto);

        return MemberResponseDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .phoneNum(saved.getPhoneNum())
                .verified(saved.isVerified())
                .city(saved.getAddress().getCity())
                .district(saved.getAddress().getDistrict())
                .detail(saved.getAddress().getDetail())
                .build();

    }

    // 로그인
    public LoginResponseDto login(LoginRequestDto dto) {
        Optional<Member> optional = memberRepository.findByPhoneNum(dto.getPhoneNum());

        if (optional.isEmpty() || !passwordEncoder.matches(dto.getPassword(), optional.get().getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        Member member = optional.get();

        String accessToken = jwtTokenProvider.generateToken(member.getPhoneNum(), member.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getPhoneNum(), member.getId());

        String address = member.getAddress().getCity() + " " + member.getAddress().getDistrict();
        MemberDynamoDto dynamoDto = MemberDynamoDto.builder()
                .id(member.getId())
                .name(member.getName())
                .phoneNum(member.getPhoneNum())
                .address(address)
                .refreshToken(refreshToken)  // 토큰 갱신
                .build();
        memberDynamoService.save(dynamoDto);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .name(member.getName())
                .memberId(member.getId())
                .build();
    }

    // 로그아웃
    public void logout(String phoneNum) {
        Member member = memberRepository.findByPhoneNum(phoneNum)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String address = member.getAddress().getCity() + " " + member.getAddress().getDistrict();
        MemberDynamoDto dynamoDto = MemberDynamoDto.builder()
                .id(member.getId())
                .name(member.getName())
                .phoneNum(member.getPhoneNum())
                .address(address)
                .refreshToken(null)  // 토큰 무효화
                .build();
        memberDynamoService.save(dynamoDto);
    }

    // 회원 정보 조회
    public MemberResponseDto getMyInfo(String phoneNum) {
        Member member = memberRepository.findByPhoneNum(phoneNum)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .phoneNum(member.getPhoneNum())
                .city(member.getAddress().getCity())
                .district(member.getAddress().getDistrict())
                .detail(member.getAddress().getDetail())
                .build();
    }

    // 회원 정보 수정 -> 주소만 가능
    public MemberResponseDto updateMyInfo(String phoneNum, MemberUpdateRequestDto dto) {
        Member member = memberRepository.findByPhoneNum(phoneNum)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.getAddress().setCity(dto.getCity());
        member.getAddress().setDistrict(dto.getDistrict());
        member.getAddress().setDetail(dto.getDetail());

        Member saved = memberRepository.save(member);

        return MemberResponseDto.builder()
                .id(saved.getId())
                .name(saved.getName())
                .phoneNum(saved.getPhoneNum())
                .city(saved.getAddress().getCity())
                .district(saved.getAddress().getDistrict())
                .detail(saved.getAddress().getDetail())
                .build();
    }

    // 비밀번호 변경
    public void changePassword(String phoneNum, PasswordChangeRequestDto dto) {
        Member member = memberRepository.findByPhoneNum(phoneNum)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        member.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        memberRepository.save(member);
    }

}