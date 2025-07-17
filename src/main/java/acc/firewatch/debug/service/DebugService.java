package acc.firewatch.debug.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Profile("local")
@RequiredArgsConstructor
public class DebugService {

    private final RedisTemplate<String, String> redisTemplate;

    public String getRefreshTokenFromRedis(Long memberId) {
        return redisTemplate.opsForValue().get("refresh_token:" + memberId);
    }

    public Set<String> listRefreshTokenKeys() {
        return redisTemplate.keys("refresh_token:*");
    }

}
