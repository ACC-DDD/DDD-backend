package acc.firewatch.external.its;

import acc.firewatch.cctv.entity.Cctv;
import acc.firewatch.external.vworld.ReverseGeocodingService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class TrafficCctvFetcher {
    private final ReverseGeocodingService reverseGeocodingService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://openapi.its.go.kr:9443")
            .codecs(config -> config.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
            .build();

    @Value("${its.api.key}")
    private String apiKey;

    public List<Cctv> fetchAllCctvsWithAddress() throws Exception {
        List<Cctv> result = new ArrayList<>();

        for (double minX = 126.0; minX < 130.0; minX += 1.0) {
            for (double minY = 34.0; minY < 38.0; minY += 1.0) {
                double maxX = minX + 1.0;
                double maxY = minY + 1.0;

                String url = "/cctvInfo?apiKey=" + apiKey +
                        "&type=ex" +
                        "&cctvType=1" +
                        "&minX=" + minX +
                        "&maxX=" + maxX +
                        "&minY=" + minY +
                        "&maxY=" + maxY +
                        "&getType=json";

                String response = webClient.get().uri(url).retrieve().bodyToMono(String.class).block();
                JsonNode items = objectMapper.readTree(response).path("response").path("data");

                if (!items.isArray()) continue;

                for (JsonNode item : items) {
                    String cctvUrl = item.path("cctvurl").asText();
                    double lat = item.path("coordy").asDouble();
                    double lng = item.path("coordx").asDouble();

                    Map<String, String> address = reverseGeocodingService.getAddressFields(lng, lat);
                    String city = address.getOrDefault("city", "");
                    String district = address.getOrDefault("district", "");
                    String town = address.getOrDefault("town", "");
                    String name = city + " " + district;

                    Cctv cctv = Cctv.builder()
                            .name(name)
                            .latitude(lat)
                            .longitude(lng)
                            .cctvUrl(cctvUrl)
                            .city(city)
                            .district(district)
                            .town(town)
                            .status(false) // 디폴트 설정
                            .build();

                    result.add(cctv);
                }
            }
        }
        return result;
    }
}
