package acc.firewatch.member.entity;

import lombok.*;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

@DynamoDbBean
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberItem {
    private Long id;
    private String name;
    private String phoneNum;
    private String address; // city+district
    private String refreshToken;

    @DynamoDbPartitionKey
    public Long getId(){
        return id;
    }

    @DynamoDbSecondaryPartitionKey(indexNames = "address-index")
    public String getAddress(){
        return address;
    }
}
