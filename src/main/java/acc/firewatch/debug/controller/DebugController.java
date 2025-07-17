package acc.firewatch.debug.controller;

import acc.firewatch.common.response.dto.CustomResponse;
import acc.firewatch.common.response.dto.SuccessStatus;
import acc.firewatch.debug.service.DebugService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@Profile("local")
@RestController
@RequestMapping("/debug")
@RequiredArgsConstructor
public class DebugController {

    private final DebugService debugService;

    @Operation(summary = "멤버 ID로 refresh 토큰 조회 API", description = "redis에 저장된 멤버 ID에 해당하는 refresh 토큰을 조회합니다.")
    @GetMapping("/redis/refresh-token/{id}")
    public CustomResponse<String> getRefreshToken(@PathVariable Long id) {
        return CustomResponse.success(debugService.getRefreshTokenFromRedis(id), SuccessStatus.SUCCESS);
    }

    @Operation(summary = "모든 Key 조회 API", description = "redis에 저장된 모든 Key를 조회합니다.")
    @GetMapping("/redis/refresh-token/keys")
    public CustomResponse<Set<String>> listAllRefreshTokenKeys() {
        return CustomResponse.success(debugService.listRefreshTokenKeys(),SuccessStatus.SUCCESS);
    }

    @Operation(summary = "refresh 토큰의 TTL 조회 API", description = "redis에 저장된 멤버 ID에 해당하는 refresh 토큰의 TTL을 조회합니다.")
    @GetMapping("/redis/refresh-token/{id}/ttl")
    public CustomResponse<Long> getRefreshTokenTTL(@PathVariable Long id) {
        return CustomResponse.success(debugService.getRefreshTokenTTL(id), SuccessStatus.SUCCESS);
    }

}
