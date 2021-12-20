package net.shyshkin.study.aws.serverless.s3sns.assignment;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import net.shyshkin.study.aws.serverless.s3sns.assignment.model.StudentWithGrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for requests to Lambda function.
 */
public class StudentGradeLogging implements RequestHandler<SQSEvent, Void> {

    private static final Logger log = LoggerFactory.getLogger(StudentGradeLogging.class);

    private final Gson gson = new Gson();

    @Override
    public Void handleRequest(SQSEvent input, Context context) {

        input.getRecords()
                .stream()
                .map(SQSEvent.SQSMessage::getBody)
                .map(this::toStudentWithGrade)
                .forEach(studentWithGrade -> log.debug("Received student grade update: " + studentWithGrade));

        return null;
    }

    private StudentWithGrade toStudentWithGrade(String message) {
        try {
            return gson.fromJson(message, StudentWithGrade.class);
        } catch (JsonSyntaxException e) {
            log.error("Exception occurred: ", e);
            return null;
        }
    }

}
