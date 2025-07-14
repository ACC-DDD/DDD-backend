package acc.firewatch.cctv.dto;

import acc.firewatch.cctv.entity.CctvItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CctvRequestDto{
    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private String cctvUrl;
    private String city;
    private String district;
    private String town;

    public CctvItem toEntity() {
        return CctvItem.builder()
                .id(this.id)
                .name(this.name)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .cctvUrl(this.cctvUrl)
                .city(this.city)
                .district(this.district)
                .town(this.town)
                .status(false) // 저장 시 기본은 false로 설정
                .build();
    }

}
