package net.shyshkin.study.aws.serverless.simplest;

/**
 * Handler for requests to Lambda function.
 */
public class App {

    public String helloWorld(String name) {

        String message = "Hello " + name;
        System.out.println(message);
        return message;
    }

}
