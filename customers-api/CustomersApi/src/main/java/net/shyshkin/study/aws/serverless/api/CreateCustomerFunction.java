package net.shyshkin.study.aws.serverless.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.shyshkin.study.aws.serverless.api.model.Customer;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.util.Map;

/**
 * Handler for requests to Lambda function.
 */
public class CreateCustomerFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final DynamoDbClient ddbClient = DynamoDbClient.builder()
            .build();

    private final String tableName = System.getenv("CUSTOMER_TABLE");

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        Map<String, String> headers = Map.of("Content-Type", "application/json");

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(headers);

        try {
            Customer customer = objectMapper.readValue(input.getBody(), Customer.class);

            PutItemRequest putItemRequest = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(Map.of(
                            "id", AttributeValue.builder().n(String.valueOf(customer.id)).build(),
                            "firstName", AttributeValue.builder().s(customer.firstName).build(),
                            "lastName", AttributeValue.builder().s(customer.lastName).build(),
                            "rewardPoints", AttributeValue.builder().n(String.valueOf(customer.rewardPoints)).build()
                    ))
                    .build();
            PutItemResponse putItemResponse = ddbClient.putItem(putItemRequest);

            int statusCode = putItemResponse.sdkHttpResponse().statusCode();

            response
                    .withBody("{\"customerId\": \"" + customer.id + "\"}")
                    .withStatusCode(statusCode);

        } catch (JsonProcessingException e) {
            context.getLogger().log("Exception happens: " + e.getMessage());
            response.withBody("{\"message\": \"" + e.getMessage().replace("\"", "'") + "\"}")
                    .withStatusCode(500);
        }

        return response;
    }

}
