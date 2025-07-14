package acc.firewatch.member.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    private String city; // 시/도
    private String district; // 시/군/구
    private String detail; // 상세 주소
}
