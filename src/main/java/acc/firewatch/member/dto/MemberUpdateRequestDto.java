package acc.firewatch.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberUpdateRequestDto {
    // 주소만 수정하게 함
    private String city;
    private String district;
    private String detail;
}
