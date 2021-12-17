package net.shyshkin.study.aws.serverless.s3sns;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.shyshkin.study.aws.serverless.s3sns.model.PatientCheckoutEvent;

public class BillManagementLambda implements RequestHandler<SNSEvent, Void> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Void handleRequest(SNSEvent input, Context context) {

        input.getRecords()
                .stream()
                .map(snsRecord -> snsRecord.getSNS().getMessage())
                .map(this::toPatientCheckoutEvent)
                .forEach(patientCheckoutEvent -> context.getLogger().log("" + patientCheckoutEvent));

        return null;
    }

    private PatientCheckoutEvent toPatientCheckoutEvent(String json) {
        try {
            return objectMapper.readValue(json, PatientCheckoutEvent.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

}
