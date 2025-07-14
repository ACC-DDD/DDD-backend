package acc.firewatch.member.repository;

import acc.firewatch.member.entity.MemberItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.*;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.UpdateItemEnhancedRequest;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final DynamoDbEnhancedClient enhancedClient;
    private static final String TABLE_NAME = "Member";
    private static final String GSI_PHONE_NUM = "phoneNum-index";
    private static final String GSI_ADDRESS = "address-index";

    public DynamoDbTable<MemberItem> getTable() {
        return enhancedClient.table(TABLE_NAME, TableSchema.fromBean(MemberItem.class));
    }

    public Optional<MemberItem> findByPhoneNum(String phoneNum) {
        QueryConditional query = QueryConditional.keyEqualTo(Key.builder().partitionValue(phoneNum).build());

        SdkIterable<Page<MemberItem>> results = getTable()
                .index(GSI_PHONE_NUM)
                .query(query);

        return StreamSupport.stream(results.spliterator(), false)
                .flatMap(page -> page.items().stream())
                .findFirst();
    }

    public List<MemberItem> findMembersByAddress(String address) {
        QueryConditional query = QueryConditional.keyEqualTo(Key.builder().partitionValue(address).build());

        SdkIterable<Page<MemberItem>> results = getTable()
                .index(GSI_ADDRESS)
                .query(query);

        return StreamSupport.stream(results.spliterator(), false)
                .flatMap(page -> page.items().stream())
                .toList();
    }

    public boolean existsByPhoneNum(String phoneNum) {
        return findByPhoneNum(phoneNum).isPresent();
    }

    public void save(MemberItem member) {
        getTable().putItem(member);
    }

    public void update(MemberItem member) {
        getTable().updateItem(UpdateItemEnhancedRequest.builder(MemberItem.class)
                .item(member)
                .ignoreNulls(true)
                .build());
    }

    public MemberItem getById(Long id) {
        return getTable().getItem(r -> r.key(k -> k.partitionValue(id)));
    }

    public void deleteById(Long id) {
        getTable().deleteItem(r -> r.key(k -> k.partitionValue(id)));
    }
}
