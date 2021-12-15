package net.shyshkin.study.aws.serverless.parameters;

/**
 * Handler for requests to Lambda function.
 */
public class DataTypes {

    public int getNumber(double number) {
        return (int) number;
    }

    public boolean getBoolean(double number) {
        return number > 100;
    }

    public Double getWrappedDouble(int number) {
        return (double) number;
    }

}
