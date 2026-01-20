package acc.firewatch.config.security;

import acc.firewatch.common.exception.CustomException;
import acc.firewatch.common.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static Long getCurrentMemberId() {
        Authentication authentication = getAuthenticationOrThrow();

        if (!(authentication.getPrincipal() instanceof CustomUserPrincipal principal)) {
            throw new CustomException(ErrorCode.INVALID_AUTH_PRINCIPAL);
        }

        return principal.getMemberId();
    }

    public static String getCurrentPhoneNum() {
        Authentication authentication = getAuthenticationOrThrow();

        if (!(authentication.getPrincipal() instanceof CustomUserPrincipal principal)) {
            throw new CustomException(ErrorCode.INVALID_AUTH_PRINCIPAL);
        }

        return principal.getPhoneNum();
    }

    private static Authentication getAuthenticationOrThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.AUTHENTICATION_INFO_NOT_FOUND);
        }

        return authentication;
    }
}

