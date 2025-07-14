package acc.firewatch.member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private Long memberId;
    private String name;
    private String accessToken;
    private String refreshToken;
}