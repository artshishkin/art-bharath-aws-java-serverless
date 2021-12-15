package net.shyshkin.study.aws.serverless.parameters;

public class Variables {

    private Double instanceVariable = Math.random();
    private static Double staticVariable = Math.random();

    public Variables() {
        System.out.println("Inside Constructor");
    }

    static {
        System.out.println("Static Block Execution");
    }

    public void coldStartBasics() {
        double localVariable = Math.random();
        System.out.println("Instance Variable: " + instanceVariable +
                "; Static Variable: " + staticVariable +
                "; Local Variable: " + localVariable
        );
    }

    public String viewEnvironmentVariables() {
        String envVar1 = System.getenv("MY_ENV_VAR_1");
        System.out.println("MY_ENV_VAR_1 value is `" + envVar1 + "`");
        return envVar1;
    }

}
