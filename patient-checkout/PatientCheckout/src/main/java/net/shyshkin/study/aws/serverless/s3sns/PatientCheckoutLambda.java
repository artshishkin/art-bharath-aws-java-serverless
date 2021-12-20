package net.shyshkin.study.aws.serverless.s3sns;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.PublishResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.shyshkin.study.aws.serverless.s3sns.model.PatientCheckoutEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PatientCheckoutLambda implements RequestHandler<S3Event, Void> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TypeReference<List<PatientCheckoutEvent>> checkoutEventsType = new TypeReference<>() {
    };
    private final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
    private final AmazonSNS sns = AmazonSNSClientBuilder.defaultClient();
    private final String topicArn = System.getenv("PATIENT_CHECKOUT_TOPIC");

    private static final Logger logger = LoggerFactory.getLogger(PatientCheckoutLambda.class);

    @Override
    public Void handleRequest(S3Event input, Context context) {

        List<CompletableFuture<PublishResult>> futures = input.getRecords().stream()
                .map(record -> s3.getObject(
                        record.getS3().getBucket().getName(),
                        record.getS3().getObject().getKey()))
                .map(S3Object::getObjectContent)
                .map(this::toPatientCheckoutEvents)
                .flatMap(Collection::stream)
                .peek(patientCheckoutEvent -> logger.info(patientCheckoutEvent.toString()))
                .map(this::toJson)
                .filter(Objects::nonNull)
                .map(json -> CompletableFuture.supplyAsync(() -> {
                    logger.info("Publishing to SNS: " + json);
                    PublishResult publishResult = sns.publish(topicArn, json);
                    logger.info("SNS Response: {}", publishResult);
                    return publishResult;
                }))
                .collect(Collectors.toList());

        futures.forEach(CompletableFuture::join);
        return null;
    }

    private List<PatientCheckoutEvent> toPatientCheckoutEvents(InputStream s3InputStream) {
        try {
            logger.info("Reading data from S3");
            var patientCheckoutEvents = objectMapper.readValue(s3InputStream, checkoutEventsType);
            logger.info(patientCheckoutEvents.toString());
            s3InputStream.close();
            return patientCheckoutEvents;
        } catch (IOException e) {
            logger.error("Exception is: ", e);
            throw new RuntimeException(e);
        }
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("Exception is: ", e);
            throw new RuntimeException(e);
        }
    }

}
