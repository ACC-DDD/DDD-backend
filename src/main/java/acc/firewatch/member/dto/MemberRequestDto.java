package acc.firewatch.member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberRequestDto {
    private String name;
    private String phoneNum;

    private String password;
    private String confirmPassword;

    private String city;
    private String district;
    private String detail;
}