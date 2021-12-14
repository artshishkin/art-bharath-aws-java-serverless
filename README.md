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
