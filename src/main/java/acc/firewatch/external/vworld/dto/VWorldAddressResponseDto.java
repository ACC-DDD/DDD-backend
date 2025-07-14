package acc.firewatch.external.vworld.dto;

import lombok.Data;

import java.util.List;

@Data
public class VWorldAddressResponseDto {
    private VWorldResponse response;

    @Data
    public static class VWorldResponse {
        private List<Result> result;
    }

    @Data
    public static class Result {
        private Structure structure;
    }

    @Data
    public static class Structure {
        private String level1;   // 시/도
        private String level2;   // 시/군/구
        private String level4L;  // 읍/면/동
    }
}