package net.shyshkin.study.aws.serverless.s3sns.assignment;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.fasterxml.jackson.jr.ob.JSON;
import net.shyshkin.study.aws.serverless.s3sns.assignment.model.StudentWithGrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Handler for requests to Lambda function.
 */
public class StudentGradeLogging implements RequestHandler<SNSEvent, Void> {

    private static final Logger log = LoggerFactory.getLogger(StudentGradeLogging.class);

    @Override
    public Void handleRequest(SNSEvent input, Context context) {

        input.getRecords()
                .stream()
                .map(record -> record.getSNS().getMessage())
                .map(this::toStudentWithGrade)
                .forEach(studentWithGrade -> log.debug("Received student grade update: " + studentWithGrade));

        return null;
    }

    private StudentWithGrade toStudentWithGrade(String message) {
        try {
            return JSON.std.beanFrom(StudentWithGrade.class, message);
        } catch (IOException e) {
            log.error("Exception occurred: ", e);
            return null;
        }
    }

}
