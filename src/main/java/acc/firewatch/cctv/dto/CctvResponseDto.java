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
public class CctvResponseDto{
    private String id;
    private String name;
    private String cctvUrl;
    private String city;
    private String district;
    private String town;
    private boolean status;

    public static CctvResponseDto fromEntity(CctvItem item) {
        return CctvResponseDto.builder()
                .id(item.getId())
                .name(item.getName())
                .cctvUrl(item.getCctvUrl())
                .city(item.getCity())
                .district(item.getDistrict())
                .town(item.getTown())
                .status(item.isStatus())
                .build();
    }


}
