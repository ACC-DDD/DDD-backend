package acc.firewatch.config.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class CustomUserPrincipal {
    private final Long memberId;
    private final String phoneNum;
}
