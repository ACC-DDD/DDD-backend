package acc.firewatch.fcm.producer;

import acc.firewatch.common.exception.CustomException;
import acc.firewatch.common.exception.ErrorCode;
import acc.firewatch.fcm.dto.FcmSendRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FcmMessageProducer {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.queues.fcm-token-queue}")
    private String sqsQueueName;

    public void send(FcmSendRequestDto request) {
        try {
            String json = objectMapper.writeValueAsString(request);
            sqsTemplate.send(sqsQueueName, json);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.SQS_SEND_ERROR);
        }
    }
}

