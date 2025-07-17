package acc.firewatch.member.dto;

import lombok.Getter;

@Getter
public class TokenReissueRequestDto {
    private String refreshToken;
}