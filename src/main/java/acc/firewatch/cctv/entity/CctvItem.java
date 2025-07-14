package acc.firewatch.cctv.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter @Setter
public class CctvItem {

    private String id;
    private String name;
    private double latitude;
    private double longitude;
    private String cctvUrl;
    private String city;
    private String district;
    private String town;
    private boolean status;

    // Partition Key
    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }

    // GSI Secondary Partition Key
    @DynamoDbSecondaryPartitionKey(indexNames = "DistrictIndex")
    public String getDistrict() {
        return district;
    }
}