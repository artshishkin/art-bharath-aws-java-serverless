package net.shyshkin.study.aws.serverless.api;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.shyshkin.study.aws.serverless.api.model.Order;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReadOrdersFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ObjectMapper objectMapper = Utils.objectMapper();
    private final AmazonDynamoDB dynamoDB = Utils.amazonDynamoDB();
    private final String tableName = Utils.TABLE_NAME;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(Map.of("Content-Type", "application/json"));

        try {

            ScanResult scanResult = dynamoDB.scan(new ScanRequest().withTableName(tableName));

            int statusCode = scanResult.getSdkHttpMetadata().getHttpStatusCode();

            List<Order> orders = scanResult.getItems().stream()
                    .map(item -> new Order(
                            Integer.parseInt(item.get("id").getN()),
                            item.get("itemName").getS(),
                            Integer.parseInt(item.get("quantity").getN()))
                    )
                    .collect(Collectors.toList());

            String jsonOrders = objectMapper.writeValueAsString(orders);
            response
                    .withBody(jsonOrders)
                    .withStatusCode(statusCode);

        } catch (JsonProcessingException e) {
            context.getLogger().log("Exception happens: " + e.getMessage());
            response.withBody("{\"message\": \"" + e.getMessage().replace("\"", "'") + "\"}")
                    .withStatusCode(500);
        }

        return response;

    }
}
