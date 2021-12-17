package net.shyshkin.study.aws.serverless.s3sns;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;

public class BillManagementLambda implements RequestHandler<SNSEvent, Void> {

    @Override
    public Void handleRequest(SNSEvent input, Context context) {
        return null;
    }

}
