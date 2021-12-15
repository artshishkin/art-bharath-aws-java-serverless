package net.shyshkin.study.aws.serverless.parameters;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

public class ContextExample {

    public String viewContext(Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("\nFunction name: " + context.getFunctionName());
        logger.log("\nRequest ID: " + context.getAwsRequestId());
        logger.log("\nRemainingTimeInMillis: " + context.getRemainingTimeInMillis());
        logger.log("\nMemoryLimitInMB: " + context.getMemoryLimitInMB());
        logger.log("\nClientContext().getEnvironment(): " +
                (
                        context.getClientContext() != null ?
                                context.getClientContext().getEnvironment() :
                                "absent"
                )
        );

        return context.getLogGroupName();
    }

    public String timeoutExample(Context context) throws InterruptedException {

        Thread.sleep(2000);

        return "OK";
    }

}
