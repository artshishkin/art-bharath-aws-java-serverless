package net.shyshkin.study.aws.serverless.api;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Utils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private static final AmazonDynamoDB AMAZON_DYNAMO_DB = AmazonDynamoDBClientBuilder
            .standard()
            .withRegion(System.getenv("AWS_REGION"))
            .build();

    private static final DynamoDB DYNAMO_DB = new DynamoDB(AMAZON_DYNAMO_DB);

    public static ObjectMapper objectMapper() {
        return OBJECT_MAPPER;
    }

    public static AmazonDynamoDB amazonDynamoDB() {
        return AMAZON_DYNAMO_DB;
    }

    public static DynamoDB dynamoDB() {
        return DYNAMO_DB;
    }

    public static final String TABLE_NAME = System.getenv("ORDERS_TABLE");


}
