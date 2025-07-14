package acc.firewatch.member.dto;

import lombok.Getter;

@Getter
public class TokenReissueRequest {
    private String refreshToken;
}