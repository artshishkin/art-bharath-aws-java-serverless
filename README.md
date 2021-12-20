# art-bharath-aws-java-serverless
Serverless using AWS Lambda for Java Developers - Tutorial from Bharath Thippireddy (Udemy)

####  Section 4: Create a Serverless Project

#####  31. Passing parameters

-  Test with event in Lambda Console
    -  Event: just `"Art"`
-  Test locally
    -  sam local invoke SimplestLambdaFunction -e events/event-string.txt

#####  34.1 Maven Shade Plugin

-  `cd SimplestLambdaFunction`
-  `mvn clean package` -> will invoke shade plugin
-  `cd ..`
-  modify `template.yaml` to reference codeUri to JAR    
-  `sam deploy` (**WITHOUT** `build`)

#####  34.2 Custom build process

1.  Make sure `make` command is installed
   -  for Windows, I installed [chocolatey package manager](https://chocolatey.org/install)
   -  then `choco install make`
   -  reboot
   -  `make -h` -> ok
2.  Create [Makefile](simplest-lambda/SimplestLambdaFunction/Makefile)
3.  Modify template.yaml to use `makefile` BuildMethod
4.  Build
   -  `sam build`
   -  got uber-jar in `.aws-sam/build/SimplestLambdaFunction/`
5.  Deploy      
   -  `sam deploy`
   -  deployed **zip with jar**)))
6.  Test
   -  error -> 
```json
{
  "errorMessage": "Class not found: net.shyshkin.study.aws.serverless.simplest.App",
  "errorType": "java.lang.ClassNotFoundException"
}
```

#####  35. Test Locally

-  `sam local invoke SimplestLambdaFunction --event events/event.json`
-  `docker image ls`
   -  `public.ecr.aws/sam/emulation-java11   rapid-1.36.0-x86_64`
   -  pulled image and executed in it

#####  52. Invoke Remotely From Command Line

1.  Asynchronous invocation
   -  return void
   -  `aws lambda invoke --invocation-type Event --function-name parameters-and-types-stac-ViewEnvironmentVariables-kgGuhdvUNXqN outputfile.txt`
2.  Synchronous invocation without payload
   -  `aws lambda invoke --invocation-type RequestResponse --function-name parameters-and-types-stack-ViewContextFunction-ksJFkQHLn6nK outputfile.txt`
3.  Synchronous invocation with payload
   -  input number - inline
   -  `aws lambda invoke --cli-binary-format raw-in-base64-out --invocation-type RequestResponse --function-name parameters-and-types-stack-GetNumberFunction-5bfi9R7rI0ej --payload 123.456 outputfile.txt`
   -  input number - from file
   -  `aws lambda invoke --cli-binary-format raw-in-base64-out --invocation-type RequestResponse --function-name parameters-and-types-stack-GetNumberFunction-5bfi9R7rI0ej --payload file://events/event-number.txt outputfile.txt`
   -  input Array
   -  `aws lambda invoke --cli-binary-format raw-in-base64-out --invocation-type RequestResponse --function-name parameters-and-types-stack-GetScoresByNameFunction-CcXKamLclrTc --payload file://events/event-list.txt outputfile.txt`
   -  input Json Map
   -  `aws lambda invoke --cli-binary-format raw-in-base64-out --invocation-type RequestResponse --function-name parameters-and-types-stac-SaveEmployeeDataFunction-xtSfPd87MDGI --payload file://events/event-map.json outputfile.txt`
   -  input POJO
   -  `aws lambda invoke --cli-binary-format raw-in-base64-out --invocation-type RequestResponse --function-name parameters-and-types-stack-GetClinicalDataFunction-biyr6RLO7sGj --payload file://events/event-pojos.json outputfile.txt`

#####  Gateway to DynamoDB implementation without Lambda

Timing compare:
1.  Lambda-less approach
   -  Create new order
      -  First request
         -  `Response code: 201 (Created); Time: 455ms; Content length: 2 bytes`
      -  Other requests
         -  `Response code: 201 (Created); Time: 241ms; Content length: 2 bytes`
   -  Read all orders
      -  `Response code: 200 (OK); Time: 262ms; Content length: 537 bytes`
2.  Lambda approach
   -  Create new order
      -  First request
         -  `Response code: 200 (OK); Time: 10993ms; Content length: 18 bytes    <-  COLD start`
      -  Other requests
         -  `Response code: 200 (OK); Time: 277ms; Content length: 18 bytes`
   -  Read all orders
      -  `Response code: 200 (OK); Time: 9628ms; Content length: 656 bytes   <- COLD start`
      -  `Response code: 200 (OK); Time: 386ms; Content length: 656 bytes`

####  Section 8: Logging and Error Handling

#####  94. Test Log4j logs

-  Correct Data
    -  `aws lambda invoke --cli-binary-format raw-in-base64-out --invocation-type Event --function-name patient-checkout-stack-PatientCheckoutLambdaFuncti-7lREXMY1aWxR --payload file://events/event.json outputfile.txt`
-  Corrupt data
    -  `aws lambda invoke --cli-binary-format raw-in-base64-out --invocation-type Event --function-name patient-checkout-stack-PatientCheckoutLambdaFuncti-7lREXMY1aWxR --payload file://events/event-corrupt.json outputfile.txt`
-  View logs
    -  `sam logs -n PatientCheckoutLambdaFunction --stack-name patient-checkout-stack`
    -  `sam logs -n PatientCheckoutLambdaFunction --stack-name patient-checkout-stack -s "10min ago" -e "2min ago"`
    -  `sam logs -n PatientCheckoutLambdaFunction --stack-name patient-checkout-stack --tail`

#####  Assignment 4.2: Corrupt Data Test (#11, #9)

-  `aws lambda invoke --cli-binary-format raw-in-base64-out --invocation-type Event --function-name student-grading-stack-StudentsUpdateMonitoringFunc-dbf5rxuO6G22 --payload file://events/event-corrupt.json outputfile.txt`


#####  12.1 Optimize cold start performance for AWS Lambda - AwsCredentialsProvider

AWS Recommendations:
-  [Optimizing cold start performance for AWS Lambda](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/lambda-optimize-starttime.html)
-  [Introducing AWS Common Runtime HTTP Client in the AWS SDK for Java 2.x](https://aws.amazon.com/blogs/developer/introducing-aws-common-runtime-http-client-in-the-aws-sdk-for-java-2-x/)

#####  12.2 AWS Common Runtime HTTP Client

1. With Netty-NIO-Client (netty-nio-client)
    -  `2021-12-19 15:07:10.290 - 2021-12-19 13:07:10  [main] DEBUG StudentUpdateMonitoring - Start fetching data from s3: GetObjectRequest(Bucket=student-grading-stack-392971033516-eu-north-1, Key=studentData.json)`
    -  `2021-12-19 15:07:17.109 - 2021-12-19 13:07:17  [aws-java-sdk-NettyEventLoop-0-2] DEBUG request - Received successful response: 200`
    -  Cold S3 call took ~7s
    -  `REPORT RequestId: d0a64632-a7fd-47d2-9f2a-947e6eb5929c	Duration: 9325.31 ms	Billed Duration: 9326 ms	Memory Size: 512 MB	Max Memory Used: 188 MB	Init Duration: 2825.91 ms`
2.  With AWS Common Runtime HTTP Client (aws-crt-client)
    -  `2021-12-19 16:20:23.407	- 2021-12-19 14:20:23  [main] DEBUG StudentUpdateMonitoring - Start fetching data from s3: GetObjectRequest(Bucket=student-grading-stack-392971033516-eu-north-1, Key=studentData.json)`
    -  `2021-12-19 16:20:24.089	- 2021-12-19 14:20:24  [main] DEBUG request - Sending Request: DefaultSdkHttpFullRequest(httpMethod=GET, protocol=https, host=student-grading-stack-392971033516-eu-north-1.s3.eu-north-1.amazonaws.com, port=443, encodedPath=/studentData.json, headers=[amz-sdk-invocation-id, User-Agent, x-amz-te], queryParameters=[])`
    -  `2021-12-19 16:20:24.608	- 2021-12-19 14:20:24  [Thread-0] DEBUG request - Received successful response: 200`
    -  `REPORT RequestId: 587303b8-90c4-4712-afdd-c9097e4106ab	Duration: 2234.34 ms	Billed Duration: 2235 ms	Memory Size: 512 MB	Max Memory Used: 155 MB	Init Duration: 2505.03 ms`

####  10 Optimize patient-checkout-stack to publish to SNS asynchronously

#####  10.1 Synchronous invocation in main thread

```
2021-12-20 14:41:04.111	START RequestId: ab5b8124-c26f-4e84-a47d-394e931f8936 Version: $LATEST
2021-12-20 14:41:07.629	2021-12-20 12:41:07 [main] ab5b8124-c26f-4e84-a47d-394e931f8936 INFO  PatientCheckoutLambda - Reading data from S3
2021-12-20 14:41:07.849	2021-12-20 12:41:07 [main] ab5b8124-c26f-4e84-a47d-394e931f8936 INFO  PatientCheckoutLambda - PatientCheckoutEvent{firstName='Artem-01', middleName='Viktorovych', lastName='Shyshkin', ssn='123456789'}
2021-12-20 14:41:08.110	2021-12-20 12:41:08 [main] ab5b8124-c26f-4e84-a47d-394e931f8936 INFO  PatientCheckoutLambda - Published to SNS: {"firstName":"Artem-01","middleName":"Viktorovych","lastName":"Shyshkin","ssn":"123456789"}
2021-12-20 14:41:09.509	2021-12-20 12:41:09 [main] ab5b8124-c26f-4e84-a47d-394e931f8936 INFO  PatientCheckoutLambda - {MessageId: f0b59fb1-1da8-5b16-9d0c-c58b618d5a0f,}    
.
.
.
2021-12-20 14:41:10.387	2021-12-20 12:41:10 [main] ab5b8124-c26f-4e84-a47d-394e931f8936 INFO  PatientCheckoutLambda - PatientCheckoutEvent{firstName='Arina-20', middleName='Artemivna', lastName='Shyshkina', ssn='123456781'}
2021-12-20 14:41:10.388	2021-12-20 12:41:10 [main] ab5b8124-c26f-4e84-a47d-394e931f8936 INFO  PatientCheckoutLambda - Published to SNS: {"firstName":"Arina-20","middleName":"Artemivna","lastName":"Shyshkina","ssn":"123456781"}
2021-12-20 14:41:10.411	2021-12-20 12:41:10 [main] ab5b8124-c26f-4e84-a47d-394e931f8936 INFO  PatientCheckoutLambda - {MessageId: a82f0c67-166a-5adb-b5d9-565d6a4c4bc2,}
2021-12-20 14:41:10.412	END RequestId: ab5b8124-c26f-4e84-a47d-394e931f8936
2021-12-20 14:41:10.412	REPORT RequestId: ab5b8124-c26f-4e84-a47d-394e931f8936	Duration: 6300.03 ms	Billed Duration: 6301 ms	Memory Size: 512 MB	Max Memory Used: 174 MB	Init Duration: 3448.52 ms
```

#####  10.2 Synchronous client invoked on many threads

```
2021-12-20 15:18:16.077	START RequestId: 2b404889-728a-44db-ac5b-ed6d3c795b6e Version: $LATEST
2021-12-20 15:18:19.633	2021-12-20 13:18:19 [main] 2b404889-728a-44db-ac5b-ed6d3c795b6e INFO  PatientCheckoutLambda - Reading data from S3
.
.
.
2021-12-20 15:18:27.734	2021-12-20 13:18:27 [Thread-16]  INFO  PatientCheckoutLambda - SNS Response: {MessageId: 7449bb86-302c-5730-9b06-e1bcb4cfc0c7,}
2021-12-20 15:18:27.734	2021-12-20 13:18:27 [Thread-2]  INFO  PatientCheckoutLambda - SNS Response: {MessageId: 578f2a5a-7cdd-5049-917c-7060f83f77be,}
2021-12-20 15:18:27.734	2021-12-20 13:18:27 [Thread-14]  INFO  PatientCheckoutLambda - SNS Response: {MessageId: ac56cec5-a868-588c-8c5e-94ff2c8aa317,}
2021-12-20 15:18:27.754	END RequestId: 2b404889-728a-44db-ac5b-ed6d3c795b6e
2021-12-20 15:18:27.754	REPORT RequestId: 2b404889-728a-44db-ac5b-ed6d3c795b6e	Duration: 11676.26 ms	Billed Duration: 11677 ms	Memory Size: 512 MB	Max Memory Used: 185 MB	Init Duration: 3425.30 ms
```

#####  10.3 Comparing with asynchronous client in students-grading-stack

```
2021-12-20 15:35:42.882	START RequestId: 485f5d28-f515-4b19-8dc5-13abfcdce37c Version: $LATEST
2021-12-20 15:35:43.221	2021-12-20 13:35:43  [main] DEBUG StudentUpdateMonitoring - File created/updated studentData-20.json
2021-12-20 15:35:43.320	2021-12-20 13:35:43  [main] DEBUG StudentUpdateMonitoring - Start fetching data from s3: GetObjectRequest(Bucket=student-grading-stack-392971033516-eu-north-1, Key=studentData-20.json)
2021-12-20 15:35:44.080	2021-12-20 13:35:44  [main] DEBUG request - Sending Request: DefaultSdkHttpFullRequest(httpMethod=GET, protocol=https, host=student-grading-stack-392971033516-eu-north-1.s3.eu-north-1.amazonaws.com, port=443, encodedPath=/studentData-20.json, headers=[amz-sdk-invocation-id, User-Agent, x-amz-te], queryParameters=[])
2021-12-20 15:35:44.704	2021-12-20 13:35:44  [Thread-0] DEBUG request - Received successful response: 200
2021-12-20 15:35:45.022	2021-12-20 13:35:45  [sdk-async-response-0-0] DEBUG StudentUpdateMonitoring - Data fetched from s3 bucket: [
...
2021-12-20 15:35:45.302	2021-12-20 13:35:45  [sdk-async-response-0-0] DEBUG request - Sending Request: DefaultSdkHttpFullRequest(httpMethod=POST, protocol=https, host=sns.eu-north-1.amazonaws.com, encodedPath=, headers=[amz-sdk-invocation-id, Content-Length, Content-Type, User-Agent], queryParameters=[])
2021-12-20 15:35:45.321	2021-12-20 13:35:45  [sdk-async-response-0-0] DEBUG request - Sending Request: DefaultSdkHttpFullRequest(httpMethod=POST, protocol=https, host=sns.eu-north-1.amazonaws.com, encodedPath=, headers=[amz-sdk-invocation-id, Content-Length, Content-Type, User-Agent], queryParameters=[])
2021-12-20 15:35:45.341	2021-12-20 13:35:45  [sdk-async-response-0-0] DEBUG request - Sending Request: DefaultSdkHttpFullRequest(httpMethod=POST, protocol=https, host=sns.eu-north-1.amazonaws.com, encodedPath=, headers=[amz-sdk-invocation-id, Content-Length, Content-Type, User-Agent], queryParameters=[])
2021-12-20 15:35:45.360	2021-12-20 13:35:45  [sdk-async-response-0-0] DEBUG request - Sending Request: DefaultSdkHttpFullRequest(httpMethod=POST, protocol=https, host=sns.eu-north-1.amazonaws.com, encodedPath=, headers=[amz-sdk-invocation-id, Content-Length, Content-Type, User-Agent], queryParameters=[])
2021-12-20 15:35:45.441	2021-12-20 13:35:45  [sdk-async-response-0-0] DEBUG request - Sending Request: DefaultSdkHttpFullRequest(httpMethod=POST, protocol=https, host=sns.eu-north-1.amazonaws.com, encodedPath=, headers=[amz-sdk-invocation-id, Content-Length, Content-Type, User-Agent], queryParameters=[])
2021-12-20 15:35:45.461	2021-12-20 13:35:45  [sdk-async-response-0-0] DEBUG request - Sending Request: DefaultSdkHttpFullRequest(httpMethod=POST, protocol=https, host=sns.eu-north-1.amazonaws.com, encodedPath=, headers=[amz-sdk-invocation-id, Content-Length, Content-Type, User-Agent], queryParameters=[])
2021-12-20 15:35:45.480	2021-12-20 13:35:45  [sdk-async-response-0-0] DEBUG request - Sending Request: DefaultSdkHttpFullRequest(httpMethod=POST, protocol=https, host=sns.eu-north-1.amazonaws.com, encodedPath=, headers=[amz-sdk-invocation-id, Content-Length, Content-Type, User-Agent], queryParameters=[])
2021-12-20 15:35:45.482	2021-12-20 13:35:45  [sdk-async-response-0-0] DEBUG request - Sending Request: DefaultSdkHttpFullRequest(httpMethod=POST, protocol=https, host=sns.eu-north-1.amazonaws.com, encodedPath=, headers=[amz-sdk-invocation-id, Content-Length, Content-Type, User-Agent], queryParameters=[])
2021-12-20 15:35:45.500	2021-12-20 13:35:45  [Thread-0] DEBUG request - Received successful response: 200
2021-12-20 15:35:45.501	2021-12-20 13:35:45  [sdk-async-response-0-0] DEBUG request - Sending Request: DefaultSdkHttpFullRequest(httpMethod=POST, protocol=https, host=sns.eu-north-1.amazonaws.com, encodedPath=, headers=[amz-sdk-invocation-id, Content-Length, Content-Type, User-Agent], queryParameters=[])
2021-12-20 15:35:45.520	2021-12-20 13:35:45  [sdk-async-response-0-0] DEBUG request - Sending Request: DefaultSdkHttpFullRequest(httpMethod=POST, protocol=https, host=sns.eu-north-1.amazonaws.com, encodedPath=, headers=[amz-sdk-invocation-id, Content-Length, Content-Type, User-Agent], queryParameters=[])
...
2021-12-20 15:35:46.339	2021-12-20 13:35:46  [sdk-async-response-2-7] DEBUG StudentUpdateMonitoring - Response of publishing to SNS: PublishResponse(MessageId=dbf29833-adf3-56ea-9ece-c2092501aeb2)
2021-12-20 15:35:46.361	2021-12-20 13:35:46  [sdk-async-response-2-3] DEBUG StudentUpdateMonitoring - Response of publishing to SNS: PublishResponse(MessageId=2307ed26-4f4c-5221-a86e-b71cc7be7418)
2021-12-20 15:35:46.361	2021-12-20 13:35:46  [Thread-1] DEBUG request - Received successful response: 200
2021-12-20 15:35:46.379	2021-12-20 13:35:46  [sdk-async-response-2-2] DEBUG StudentUpdateMonitoring - Response of publishing to SNS: PublishResponse(MessageId=230482fc-05c3-5e29-a846-d8c68d315e31)
2021-12-20 15:35:46.399	END RequestId: 485f5d28-f515-4b19-8dc5-13abfcdce37c
2021-12-20 15:35:46.399	REPORT RequestId: 485f5d28-f515-4b19-8dc5-13abfcdce37c	Duration: 3517.01 ms	Billed Duration: 3518 ms	Memory Size: 512 MB	Max Memory Used: 163 MB	Init Duration: 2520.09 ms
```

#####  10.4 Parallel stream (but no ForkJoinPool)

```
2021-12-20 15:53:56.955	START RequestId: 2c74d5d2-38ba-455c-8c65-3e57cd6cbfa9 Version: $LATEST
2021-12-20 15:54:00.236	2021-12-20 13:54:00 [main] 2c74d5d2-38ba-455c-8c65-3e57cd6cbfa9 INFO  PatientCheckoutLambda - Reading data from S3
...
2021-12-20 15:54:02.173	2021-12-20 13:54:02 [main] 2c74d5d2-38ba-455c-8c65-3e57cd6cbfa9 INFO  PatientCheckoutLambda - SNS Response: {MessageId: 874e5085-e2d3-5f4a-8512-0abf6fe93a58,}
2021-12-20 15:54:02.175	END RequestId: 2c74d5d2-38ba-455c-8c65-3e57cd6cbfa9
2021-12-20 15:54:02.175	REPORT RequestId: 2c74d5d2-38ba-455c-8c65-3e57cd6cbfa9	Duration: 5219.30 ms	Billed Duration: 5220 ms	Memory Size: 512 MB	Max Memory Used: 165 MB	Init Duration: 3460.38 ms
```

#####  10.5 PublishBatchRequest - subList 

```
2021-12-20 17:13:22.632	REPORT RequestId: c58ffacd-4bbf-4b3f-8013-8ad968290134	Duration: 4481.32 ms	Billed Duration: 4482 ms	Memory Size: 512 MB	Max Memory Used: 163 MB	Init Duration: 3357.61 ms
```
