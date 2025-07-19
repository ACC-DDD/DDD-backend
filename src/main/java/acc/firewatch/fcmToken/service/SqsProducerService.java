package acc.firewatch.fcmToken.service;

import io.awspring.cloud.sqs.operations.SqsTemplate;

import acc.firewatch.fcmToken.dto.FcmMessageRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value; // import 확인
import org.springframework.stereotype.Service;

@Service
public class SqsProducerService {

  private static final Logger logger = LoggerFactory.getLogger(SqsProducerService.class);

  private final SqsTemplate sqsTemplate;
  private final ObjectMapper objectMapper;

  @Value("${aws.sqs.queues.fcm-token-queue}")
  private String sqsQueueName;

  public SqsProducerService(SqsTemplate sqsTemplate, ObjectMapper objectMapper) {
    this.sqsTemplate = sqsTemplate;
    this.objectMapper = objectMapper;
  }

  public void sendMessage(FcmMessageRequest fcmMessageRequest) {
    try {
      String messageBody = objectMapper.writeValueAsString(fcmMessageRequest);

      sqsTemplate.send(sqsQueueName, messageBody);

      logger.info("SQS 메시지 전송 성공 (SqsTemplate): {}", messageBody);

    } catch (JsonProcessingException e) {
      logger.error("메시지 직렬화 실패: {}", fcmMessageRequest, e);
      throw new RuntimeException("메시지 직렬화 실패", e);
    } catch (Exception e) {
      logger.error("SQS 메시지 전송 실패: {}", e.getMessage(), e);
      throw new RuntimeException("SQS 메시지 전송 중 오류 발생", e);
    }
  }
}