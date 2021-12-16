package net.shyshkin.study.aws.serverless.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.shyshkin.study.aws.serverless.api.model.Customer;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handler for requests to Lambda function.
 */
public class ReadCustomersFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final DynamoDbClient ddbClient = DynamoDbClient.builder()
            .build();

    private final String tableName = System.getenv("CUSTOMER_TABLE");

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = Map.of("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        try {
            ScanResponse scanResponse = ddbClient.scan(builder -> builder.tableName(tableName));
            int statusCode = scanResponse.sdkHttpResponse().statusCode();

            List<Customer> customers = scanResponse.items()
                    .stream()
                    .map(item -> new Customer(
                            Integer.parseInt(item.get("id").n()),
                            item.get("firstName").s(),
                            item.get("lastName").s(),
                            Integer.parseInt(item.get("rewardPoints").n())
                    ))
                    .collect(Collectors.toList());

            response
                    .withBody(objectMapper.writeValueAsString(customers))
                    .withStatusCode(statusCode);

        } catch (JsonProcessingException e) {
            context.getLogger().log("Exception happens: " + e.getMessage());
            response.withBody("{\"message\": \"" + e.getMessage().replace("\"", "'") + "\"}")
                    .withStatusCode(500);
        }

        return response;
    }

}
