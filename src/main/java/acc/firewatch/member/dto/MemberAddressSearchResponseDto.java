package acc.firewatch.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberAddressSearchResponseDto {
    private String address; // 예: "서울 강남구"
    private int count;
    private List<MemberResponseDto> members;
}