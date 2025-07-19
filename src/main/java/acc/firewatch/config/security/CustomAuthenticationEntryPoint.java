package acc.firewatch.config.security;

import acc.firewatch.common.exception.CustomException;
import acc.firewatch.common.exception.ErrorCode;
import acc.firewatch.common.response.dto.CustomResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ErrorCode errorCode = ErrorCode.AUTH_UNKNOWN_ERROR; // 기본값

        // JwtAuthenticationFilter에서 감싼 CustomException의 ErrorCode를 추출
        if (authException.getCause() instanceof CustomException) {
            errorCode = ((CustomException) authException.getCause()).getErrorCode();
        }

        CustomResponse<Object> errorResponse = CustomResponse.failure(errorCode);
        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }

}
