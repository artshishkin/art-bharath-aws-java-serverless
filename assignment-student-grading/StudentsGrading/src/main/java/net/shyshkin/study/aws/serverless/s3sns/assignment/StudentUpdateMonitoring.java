package net.shyshkin.study.aws.serverless.s3sns.assignment;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.google.gson.Gson;
import net.shyshkin.study.aws.serverless.s3sns.assignment.model.Student;
import net.shyshkin.study.aws.serverless.s3sns.assignment.model.StudentWithGrade;

import java.io.InputStreamReader;
import java.util.stream.Stream;

/**
 * Handler for requests to Lambda function.
 */
public class StudentUpdateMonitoring implements RequestHandler<S3Event, Void> {

    private final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
    private final Gson gson = new Gson();
    private final AmazonSNSAsync sns = AmazonSNSAsyncClientBuilder.defaultClient();
    private final String topicArn = System.getenv("STUDENTS_GRADE_TOPIC");

    @Override
    public Void handleRequest(S3Event input, Context context) {

        input.getRecords()
                .stream()
                .map(record -> s3.getObject(record.getS3().getBucket().getName(), record.getS3().getObject().getKey()))
                .map(s3Object -> gson.fromJson(new InputStreamReader(s3Object.getObjectContent()), Student[].class))
                .flatMap(Stream::of)
                .map(st -> new StudentWithGrade(st.roleNumber, st.name, st.testScore, calcGrade(st.testScore)))
                .forEach(st -> sns.publish(topicArn, gson.toJson(st)));

        return null;
    }

    private String calcGrade(int testScore) {
        if (testScore > 90) return "A";
        if (testScore > 70) return "B";
        return "C";
    }
}
