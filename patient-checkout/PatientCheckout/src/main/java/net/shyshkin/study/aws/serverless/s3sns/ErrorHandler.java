package net.shyshkin.study.aws.serverless.s3sns;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ErrorHandler implements RequestHandler<SNSEvent, Void> {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    @Override
    public Void handleRequest(SNSEvent event, Context context) {

        event.getRecords()
                .forEach(record -> log.info("Dead Letter Queue Event: {}", record));
        return null;
    }
}
