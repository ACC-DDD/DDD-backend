package acc.firewatch.cctv.repository;

import acc.firewatch.cctv.entity.CctvItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.List;
import java.util.stream.StreamSupport;

@Repository
@RequiredArgsConstructor
public class CctvRepository {

    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbEnhancedClient enhancedClient;
    private static final String TABLE_NAME = "Cctv";
    private static final String GSI_NAME = "district-index";

    public DynamoDbTable<CctvItem> getTable() {
        return enhancedClient.table(TABLE_NAME, TableSchema.fromBean(CctvItem.class));
    }

    public void createTable() {
        CreateTableRequest request = CreateTableRequest.builder()
                .tableName(TABLE_NAME)
                .keySchema(KeySchemaElement.builder()
                        .attributeName("id")
                        .keyType(KeyType.HASH)
                        .build())
                .attributeDefinitions(
                        AttributeDefinition.builder()
                                .attributeName("id")
                                .attributeType(ScalarAttributeType.S)
                                .build(),
                        AttributeDefinition.builder()
                                .attributeName("district")
                                .attributeType(ScalarAttributeType.S)
                                .build()
                )
                .billingMode(BillingMode.PAY_PER_REQUEST)
                .globalSecondaryIndexes(GlobalSecondaryIndex.builder()
                        .indexName(GSI_NAME)
                        .keySchema(KeySchemaElement.builder()
                                .attributeName("district")
                                .keyType(KeyType.HASH)
                                .build())
                        .projection(Projection.builder()
                                .projectionType(ProjectionType.ALL)
                                .build())
                        .build())
                .build();

        dynamoDbClient.createTable(request);
    }

    public void deleteTable() {
        DeleteTableRequest request = DeleteTableRequest.builder()
                .tableName(TABLE_NAME)
                .build();
        dynamoDbClient.deleteTable(request);
    }

    public void save(CctvItem item) {
        getTable().putItem(item);
    }

    public CctvItem getById(String id) {
        return getTable().getItem(r -> r.key(k -> k.partitionValue(id)));
    }

    public void deleteById(String id) {
        getTable().deleteItem(r -> r.key(k -> k.partitionValue(id)));
    }

    public List<CctvItem> getAll() {
        return StreamSupport.stream(getTable().scan().items().spliterator(), false)
                .toList();
    }

    public List<CctvItem> findByDistrict(String district) {
        QueryConditional query = QueryConditional.keyEqualTo(Key.builder().partitionValue(district).build());
        SdkIterable<Page<CctvItem>> results = getTable().index(GSI_NAME).query(query);
        return StreamSupport.stream(results.spliterator(), false)
                .flatMap(page -> page.items().stream())
                .toList();
    }
}
