package acc.firewatch.common.response.dto;

import acc.firewatch.common.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CustomResponseUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void writeErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getCode());
        response.setContentType("application/json;charset=UTF-8");

        CustomResponse<Object> errorResponse = CustomResponse.failure(errorCode);
        String json = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(json);
    }
}