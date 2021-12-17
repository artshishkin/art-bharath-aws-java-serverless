package net.shyshkin.study.aws.serverless.s3sns;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.shyshkin.study.aws.serverless.s3sns.model.PatientCheckoutEvent;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PatientCheckoutLambda implements RequestHandler<S3Event, Void> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TypeReference<List<PatientCheckoutEvent>> checkoutEventsType = new TypeReference<>() {
    };
    private final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

    @Override
    public Void handleRequest(S3Event input, Context context) {

        input.getRecords().stream()
                .map(record -> s3.getObject(
                        record.getS3().getBucket().getName(),
                        record.getS3().getObject().getKey()))
                .map(S3Object::getObjectContent)
                .map(this::toPatientCheckoutEvents)
                .flatMap(Collection::stream)
                .forEach(patientCheckoutEvent -> context.getLogger().log(patientCheckoutEvent.toString()));

        return null;
    }

    private List<PatientCheckoutEvent> toPatientCheckoutEvents(InputStream s3InputStream) {
        try {
            return objectMapper.readValue(s3InputStream, checkoutEventsType);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

}
