package net.shyshkin.study.aws.serverless.s3sns.assignment;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.google.gson.Gson;
import net.shyshkin.study.aws.serverless.s3sns.assignment.model.StudentWithGrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for requests to Lambda function.
 */
public class StudentGradeLogging implements RequestHandler<SNSEvent, Void> {

    private static final Logger log = LoggerFactory.getLogger(StudentGradeLogging.class);

    private final Gson gson = new Gson();

    @Override
    public Void handleRequest(SNSEvent input, Context context) {

        input.getRecords()
                .stream()
                .map(record -> record.getSNS().getMessage())
                .map(message -> gson.fromJson(message, StudentWithGrade.class))
                .forEach(studentWithGrade -> log.debug("Received student grade update: " + studentWithGrade));

        return null;
    }

}
