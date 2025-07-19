package acc.firewatch.fcm.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class FcmSendRequestDto {

  @NotBlank(message = "토큰은 필수입니다.")
  private String token;

  @NotBlank(message = "토픽은 필수입니다.")
  private String topic;
}