package acc.firewatch.fcmToken.controller;

import acc.firewatch.fcmToken.dto.FcmMessageRequest;
import acc.firewatch.fcmToken.service.SqsProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/fcm")
public class MessageController {

  private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
  private final SqsProducerService sqsProducerService;

  public MessageController(SqsProducerService sqsProducerService) {
    this.sqsProducerService = sqsProducerService;
  }

  @PostMapping("/send-token-to-sqs")
  public ResponseEntity<Map<String, Object>> sendFcmTokenToSqs(@RequestBody FcmMessageRequest request) {
    Map<String, Object> responseBody = new HashMap<>();

    // 필수 파라미터 유효성 검사
    if (request.getToken() == null || request.getToken().isEmpty() ||
        request.getTopic() == null || request.getTopic().isEmpty()) {
      logger.warn("토큰 또는 토픽이 누락되었습니다. 요청: {}", request);
      responseBody.put("code", 400);
      responseBody.put("message", "토큰과 토픽은 필수 값입니다.");
      return ResponseEntity.badRequest().body(responseBody);
    }

    try {
      sqsProducerService.sendMessage(request);
      logger.info("FCM 토큰 메시지가 SQS로 성공적으로 전송되었습니다. 토큰: {}..., 토픽: {}",
          request.getToken().substring(0, Math.min(request.getToken().length(), 10)),
          request.getTopic());
      responseBody.put("code", 200);
      responseBody.put("message", "FCM 토큰 메시지가 SQS로 성공적으로 전송되었습니다.");
      return ResponseEntity.ok(responseBody);
    } catch (Exception e) {
      logger.error("FCM 토큰 SQS 전송 중 오류 발생: {}", e.getMessage(), e);
      responseBody.put("code", 500);
      responseBody.put("message", "FCM 토큰 SQS 전송 중 오류가 발생했습니다.");
      return ResponseEntity.status(500).body(responseBody);
    }
  }
}