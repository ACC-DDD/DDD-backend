package acc.firewatch.config.jwt;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CustomUserPrincipal {
    private final Long memberId;
    private final String phoneNum;
}
