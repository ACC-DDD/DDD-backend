package acc.firewatch.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequestDto {
    private String name;
    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    @Pattern(
            regexp = "^(01[016789])(-?\\d{3,4})(-?\\d{4})$",
            message = "전화번호 형식이 올바르지 않습니다. 예) 010-1234-5678 또는 01012345678"
    )
    private String phoneNum;

    private String password;
    private String confirmPassword;

    private String city;
    private String district;
    private String detail;
}