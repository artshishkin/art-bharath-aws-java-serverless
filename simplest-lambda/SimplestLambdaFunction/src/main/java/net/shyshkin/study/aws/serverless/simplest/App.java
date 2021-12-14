package net.shyshkin.study.aws.serverless.simplest;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

/**
 * Handler for requests to Lambda function.
 */
public class App implements RequestHandler<APIGatewayProxyRequestEvent, String> {

    @Override
    public String handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String name = input.getHeaders().get("Art-User-Name");
        String message = "Hello " + name;
        System.out.println(message);
        return message;
    }

}
