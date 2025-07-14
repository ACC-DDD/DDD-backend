package acc.firewatch.external.vworld;

import acc.firewatch.external.vworld.dto.VWorldAddressResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReverseGeocodingService {

    private final WebClient webClient;

    @Value("${vworld.api.key}")
    private String apiKey;

    public Map<String, String> getAddressFields(double longitude, double latitude) {
        String url = String.format(
                "https://api.vworld.kr/req/address?service=address&request=getAddress&format=json&type=PARCEL&point=%f,%f&key=%s",
                longitude, latitude, apiKey
        );

        VWorldAddressResponseDto response = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(VWorldAddressResponseDto.class)
                .block();

        if (response == null || response.getResponse() == null ||
                response.getResponse().getResult() == null || response.getResponse().getResult().isEmpty()) {
            System.out.printf("⚠️ 주소 없음 → 위도: %f, 경도: %f%n", latitude, longitude);
            Map<String, String> unknown = new HashMap<>();
            unknown.put("city", "미상");
            unknown.put("district", "미상");
            unknown.put("town", "미상");
            return unknown;
        }

        var structure = response.getResponse().getResult().get(0).getStructure();

        Map<String, String> result = new HashMap<>();
        result.put("city", structure.getLevel1());     // 전라남도
        result.put("district", structure.getLevel2()); // 무안군
        result.put("town", structure.getLevel4L());    // 몽탄면
        return result;
    }
}