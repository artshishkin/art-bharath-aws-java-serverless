package net.shyshkin.study.aws.serverless.sqs;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClaimManagementLambda implements RequestHandler<SQSEvent, Void> {

    private static final Logger log = LoggerFactory.getLogger(ClaimManagementLambda.class);

    @Override
    public Void handleRequest(SQSEvent event, Context context) {

        log.info("SQS Claim Management Event happened");
        event.getRecords()
                .forEach(record -> log.info("SQS Claim record: {}", record.getBody()));
        return null;
    }
}
