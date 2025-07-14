package acc.firewatch.member.dto;

import acc.firewatch.member.entity.MemberItem;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberDynamoDto {
    private Long id;
    private String name;
    private String phoneNum;
    private String address; // city+district
    private String refreshToken;

    public MemberItem toEntity() {
        return MemberItem.builder()
                .id(this.id)
                .name(this.name)
                .phoneNum(this.phoneNum)
                .address(this.address)
                .refreshToken(this.refreshToken)
                .build();
    }

    public static MemberDynamoDto fromEntity(MemberItem item) {
        return MemberDynamoDto.builder()
                .id(item.getId())
                .name(item.getName())
                .phoneNum(item.getPhoneNum())
                .address(item.getAddress())
                .refreshToken(item.getRefreshToken())
                .build();
    }
}
