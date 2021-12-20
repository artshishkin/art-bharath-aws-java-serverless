package net.shyshkin.study.aws.serverless.s3sns.assignment;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.google.gson.Gson;
import net.shyshkin.study.aws.serverless.s3sns.assignment.model.Student;
import net.shyshkin.study.aws.serverless.s3sns.assignment.model.StudentWithGrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.crt.AwsCrtAsyncHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Handler for requests to Lambda function.
 */
public class StudentUpdateMonitoring implements RequestHandler<S3Event, Void> {

    private static final Logger log = LoggerFactory.getLogger(StudentUpdateMonitoring.class);

    private final Gson gson = new Gson();

    private final SdkAsyncHttpClient httpClient = initHttpClient();
    private final S3AsyncClient s3 = initS3();
    private final SqsAsyncClient sqs = initSqs();
    private final String queueUrl = System.getenv("STUDENTS_GRADE_QUEUE_URL");

    private final AwsCredentialsProvider credentialsProvider = EnvironmentVariableCredentialsProvider.create();

    @Override
    public Void handleRequest(S3Event input, Context context) {

        final int BATCH_SIZE = 10;
        final AtomicInteger counter = new AtomicInteger(0);

        input.getRecords()
                .stream()
                .peek(record -> log.debug("File created/updated {}", record.getS3().getObject().getKey()))
                .map(record -> GetObjectRequest.builder()
                        .bucket(record.getS3().getBucket().getName())
                        .key(record.getS3().getObject().getKey())
                        .build()
                )
                .peek(request -> log.debug("Start fetching data from s3: {}", request))
                .map(request -> s3.getObject(request, AsyncResponseTransformer.toBytes()))
                .map(
                        cf -> cf
                                .thenApply(getObjectResponseResponseBytes ->
                                        {
                                            String studentsJson = getObjectResponseResponseBytes.asUtf8String();
                                            log.debug("Data fetched from s3 bucket: {}", studentsJson);
                                            return studentsJson;
                                        }
                                )
                                .thenApply(this::toStudents)
                                .thenApply(students -> students
                                        .stream()
                                        .map(st -> new StudentWithGrade(st.roleNumber, st.name, st.testScore, calcGrade(st.testScore)))
                                        .map(grade -> SendMessageBatchRequestEntry.builder()
                                                .id(UUID.randomUUID().toString())
                                                .messageBody(asJson(grade))
                                                .build()
                                        )
                                        .collect(Collectors.groupingBy(it -> counter.getAndIncrement() / BATCH_SIZE))
                                        .values()
                                )
                                .thenCompose(grades -> CompletableFuture
                                        .allOf(
                                                grades.stream()
                                                        .map(st -> sqs
                                                                .sendMessageBatch(b -> b.queueUrl(queueUrl).entries(st))
                                                                .thenAccept(response -> log.debug("Response of sending to SQS: {}", response))
                                                        )
                                                        .toArray(CompletableFuture[]::new))
                                )
                )
                .forEach(CompletableFuture::join);

        return null;
    }

    private String asJson(StudentWithGrade st) {
        return gson.toJson(st);
    }

    private List<Student> toStudents(String json) {
        return List.of(gson.fromJson(json, Student[].class));
    }

    private String calcGrade(int testScore) {
        if (testScore > 90) return "A";
        if (testScore > 70) return "B";
        return "C";
    }

    private S3AsyncClient initS3() {

        return S3AsyncClient.builder()
                .httpClient(httpClient)
                .region(Region.of(System.getenv("AWS_REGION")))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    private SqsAsyncClient initSqs() {
        return SqsAsyncClient.builder()
                .httpClient(httpClient)
                .region(Region.of(System.getenv("AWS_REGION")))
                .credentialsProvider(credentialsProvider)
                .build();
    }

    private SdkAsyncHttpClient initHttpClient() {
        return AwsCrtAsyncHttpClient.builder()
                .maxConcurrency(50)
                .build();
    }
}
