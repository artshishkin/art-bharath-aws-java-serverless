package net.shyshkin.study.aws.serverless.parameters;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Handler for requests to Lambda function.
 */
public class InputOutputStreams {

    public void getOutput(InputStream input, OutputStream output) throws IOException {
        int data;
        while ((data = input.read()) > -1) {
            int modified = Character.toUpperCase(data);
            output.write(modified);
        }

    }

}
