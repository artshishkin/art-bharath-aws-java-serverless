package net.shyshkin.study.aws.serverless.api;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.shyshkin.study.aws.serverless.api.model.Order;

import java.util.List;
import java.util.Map;

public class ReadOrdersFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {

        ObjectMapper objectMapper = new ObjectMapper();
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent()
                .withHeaders(Map.of("Content-Type", "application/json"));

        List<Order> orders = List.of(
                new Order(123, "Tesla", 2),
                new Order(125, "Rocket", 1),
                new Order(127, "Bike", 10)
        );

        try {
            String jsonOrders = objectMapper.writeValueAsString(orders);

            response
                    .withBody(jsonOrders)
                    .withStatusCode(200);

        } catch (JsonProcessingException e) {
            context.getLogger().log("Exception happens: " + e.getMessage());
            response.withBody("{\"message\": \"" + e.getMessage().replace("\"", "'") + "\"}")
                    .withStatusCode(500);
        }

        return response;

    }
}
