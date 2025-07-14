package acc.firewatch.cctv.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cctv {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    private String name;       // city+district+town
    private double latitude;   // 위도
    private double longitude;  // 경도

    @Column(length = 1000, nullable = false)
    private String cctvUrl;

    private String city;      // 시/도
    private String district;  // 군/구
    private String town;      // 동/면/읍

    private boolean status;   // 재난 상태
}
