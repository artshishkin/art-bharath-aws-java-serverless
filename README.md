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


    