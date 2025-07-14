package acc.firewatch.member.service;

import acc.firewatch.common.exception.CustomException;
import acc.firewatch.common.exception.ErrorCode;
import acc.firewatch.config.jwt.JwtTokenProvider;
import acc.firewatch.member.dto.*;
import acc.firewatch.member.entity.MemberItem;
import acc.firewatch.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberDynamoService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private Long memberSeq=1L;

    // 회원가입
    public MemberResponseDto signUp(MemberRequestDto dto) {
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        if (memberRepository.existsByPhoneNum(dto.getPhoneNum())) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_MEMBER);
        }

        MemberItem member = MemberItem.builder()
                .id(memberSeq++)
                .name(dto.getName())
                .phoneNum(dto.getPhoneNum())
                .password(passwordEncoder.encode(dto.getPassword()))
                .city(dto.getCity())
                .district(dto.getDistrict())
                .detail(dto.getDetail())
                .address(dto.getCity()+ " " +dto.getDistrict()) // city+district. ex) 서울시 강남구
                .verified(false)
                .refreshToken(null)
                .build();

        memberRepository.save(member);

        return MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .phoneNum(member.getPhoneNum())
                .verified(member.isVerified())
                .city(member.getCity())
                .district(member.getDistrict())
                .detail(member.getDetail())
                .build();
    }

    // 로그인
    public LoginResponseDto login(LoginRequestDto dto) {
        MemberItem member = memberRepository.findByPhoneNum(dto.getPhoneNum())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_FAILED));

        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

        String accessToken = jwtTokenProvider.generateToken(member.getPhoneNum(), member.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getPhoneNum(), member.getId());

        member.setRefreshToken(refreshToken);
        memberRepository.update(member);

        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .name(member.getName())
                .memberId(member.getId())
                .build();
    }

    // 로그아웃
    public void logout(String phoneNum) {
        MemberItem member = memberRepository.findByPhoneNum(phoneNum)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.setRefreshToken(null);
        memberRepository.update(member);
    }

    // 내 정보 조회
    public MemberResponseDto getMyInfo(String phoneNum) {
        MemberItem member = memberRepository.findByPhoneNum(phoneNum)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        return MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .phoneNum(member.getPhoneNum())
                .city(member.getCity())
                .district(member.getDistrict())
                .detail(member.getDetail())
                .build();
    }

    // 회원 정보 수정 -> 주소만 가능
    public MemberResponseDto updateMyInfo(String phoneNum, MemberUpdateRequestDto dto) {
        MemberItem member = memberRepository.findByPhoneNum(phoneNum)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        String updatedAddress = dto.getCity() + " " + dto.getDistrict();
        member.setCity(dto.getCity());
        member.setDistrict(dto.getDistrict());
        member.setDetail(dto.getDetail());
        member.setAddress(updatedAddress);

        memberRepository.update(member);

        return MemberResponseDto.builder()
                .id(member.getId())
                .name(member.getName())
                .phoneNum(member.getPhoneNum())
                .city(member.getCity())
                .district(member.getDistrict())
                .detail(member.getDetail())
                .build();
    }

    // 비밀번호 변경
    public void changePassword(String phoneNum, PasswordChangeRequestDto dto) {
        MemberItem member = memberRepository.findByPhoneNum(phoneNum)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        if (!passwordEncoder.matches(dto.getCurrentPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
        }

        if (!dto.getNewPassword().equals(dto.getConfirmNewPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_CONFIRM_MISMATCH);
        }

        member.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        memberRepository.update(member);
    }

    // 파라미터 address(city+district)에 해당하는 전체 member 아이템 조회
    public MemberAddressSearchResponseDto findByAddress(String address) {
        List<MemberResponseDto> members = memberRepository.findMembersByAddress(address).stream()
                .map(item -> MemberResponseDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .phoneNum(item.getPhoneNum())
                        .verified(item.isVerified())
                        .city(item.getCity())
                        .district(item.getDistrict())
                        .detail(item.getDetail())
                        .build())
                .toList();

        return MemberAddressSearchResponseDto.builder()
                .address(address)
                .count(members.size())
                .members(members)
                .build();
    }

    // refreshToken 갱신: null이 아닌 필드들은 모두 갱신되고, null인 필드는 기존 값 유지되는 구조를 활용
    public void updateRefreshToken(Long memberId, String newRefreshToken) {
        MemberItem member = getById(memberId);
        if (member == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        member.setRefreshToken(newRefreshToken);
        memberRepository.update(member);
    }

    // ID로 단건 조회
    public MemberItem getById(Long id) {
        MemberItem member = memberRepository.getById(id);
        if (member == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return member;
    }

    // id에 해당하는 레코드 삭제
    public void deleteById(Long id) {
        memberRepository.deleteById(id);
        log.info("삭제 완료: id = {}", id);
    }

}
