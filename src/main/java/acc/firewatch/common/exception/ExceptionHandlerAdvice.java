package acc.firewatch.common.exception;

import acc.firewatch.common.response.dto.CustomResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<CustomResponse<Object>> handleCustomException(CustomException e) {
        log.error("[CustomException] code: {}, message: {}", e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        return ResponseEntity
                .status(e.getErrorCode().getCode())
                .body(CustomResponse.failure(e.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomResponse<Object>> handleGeneralException(Exception e) {
        log.error("[Exception] message: {}", e.getMessage(), e);
        return ResponseEntity
                .status(500)
                .body(CustomResponse.failure(500, "서버 내부 오류가 발생했습니다."));
    }
}
