package acc.firewatch.member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponseDto {
    private Long id;
    private String name;
    private String phoneNum;
    private String city;
    private String district;
    private String detail;
    private boolean verified;
}