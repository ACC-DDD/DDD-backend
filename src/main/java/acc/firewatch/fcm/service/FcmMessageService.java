package acc.firewatch.fcm.service;

import acc.firewatch.fcm.producer.FcmMessageProducer;
import io.awspring.cloud.sqs.operations.SqsTemplate;

import acc.firewatch.fcm.dto.FcmSendRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value; // import 확인
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmMessageService {

  private final FcmMessageProducer producer;

  public void publishFcmToken(FcmSendRequestDto request) {
    producer.send(request);
  }
}