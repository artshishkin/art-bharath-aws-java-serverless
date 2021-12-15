package net.shyshkin.study.aws.serverless.api;

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


            response
                    .withBody("{\"orderId\": \"" + order.id + "\"}")
                    .withStatusCode(200);

        } catch (JsonProcessingException e) {
            context.getLogger().log("Exception happens: " + e.getMessage());
            response.withBody("{\"message\": \"" + e.getMessage().replace("\"", "'") + "\"}")
                    .withStatusCode(500);
        }

        return response;
    }

}
