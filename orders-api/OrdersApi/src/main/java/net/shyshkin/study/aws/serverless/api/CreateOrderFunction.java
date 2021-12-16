package net.shyshkin.study.aws.serverless.api;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.shyshkin.study.aws.serverless.api.model.Order;

import java.util.Map;

public class CreateOrderFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        ObjectMapper objectMapper = new ObjectMapper();
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(Map.of("Content-Type", "application/json"));

        try {
            Order order = objectMapper.readValue(input.getBody(), Order.class);

            DynamoDB dynamoDB = new DynamoDB(AmazonDynamoDBClientBuilder.defaultClient());
            String tableName = System.getenv("ORDERS_TABLE");
            Table table = dynamoDB.getTable(tableName);
            Item item = new Item()
                    .withPrimaryKey("id", order.id)
                    .withString("itemName", order.itemName)
                    .withInt("quantity", order.quantity);
            PutItemOutcome putItemOutcome = table.putItem(item);
            var sdkHttpMetadata = putItemOutcome.getPutItemResult().getSdkHttpMetadata();
            int statusCode = sdkHttpMetadata.getHttpStatusCode();

            response
                    .withBody("{\"orderId\": \"" + order.id + "\"}")
                    .withStatusCode(statusCode);

        } catch (JsonProcessingException e) {
            context.getLogger().log("Exception happens: " + e.getMessage());
            response.withBody("{\"message\": \"" + e.getMessage().replace("\"", "'") + "\"}")
                    .withStatusCode(500);
        }

        return response;
    }

}
