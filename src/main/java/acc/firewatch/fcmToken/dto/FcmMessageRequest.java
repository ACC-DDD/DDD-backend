package acc.firewatch.fcmToken.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class FcmMessageRequest {
  private String token;
  private String topic;
}