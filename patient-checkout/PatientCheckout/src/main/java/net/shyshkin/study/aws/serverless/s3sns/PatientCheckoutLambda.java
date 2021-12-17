package net.shyshkin.study.aws.serverless.s3sns;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;

public class PatientCheckoutLambda implements RequestHandler<S3Event,Void> {

    @Override
    public Void handleRequest(S3Event input, Context context) {
        return null;
    }

}
