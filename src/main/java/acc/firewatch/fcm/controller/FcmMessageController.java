package acc.firewatch.fcm.controller;

import acc.firewatch.common.response.dto.CustomResponse;
import acc.firewatch.common.response.dto.SuccessStatus;
import acc.firewatch.fcm.dto.FcmSendRequestDto;
import acc.firewatch.fcm.service.FcmMessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fcm")
@RequiredArgsConstructor
public class FcmMessageController {
    private final FcmMessageService fcmMessageService;

    @PostMapping("/publish")
    public CustomResponse<?> sendFcmTokenToSqs(@Valid @RequestBody FcmSendRequestDto request) {
        fcmMessageService.publishFcmToken(request);
        return CustomResponse.success(SuccessStatus.SEND_FCM_MESSAGE_OK);
    }
}