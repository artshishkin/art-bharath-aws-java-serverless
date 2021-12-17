package net.shyshkin.study.aws.serverless.s3sns.assignment;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

/**
 * Handler for requests to Lambda function.
 */
public class StudentGradeLogging implements RequestHandler<SNSEvent, Void> {

    @Override
    public Void handleRequest(SNSEvent input, Context context) {

        input.getRecords()
                .stream()
                .map(record -> record.getSNS().getMessage())
                .forEach(message -> context.getLogger().log("Received message: " + message));

        return null;
    }

}
