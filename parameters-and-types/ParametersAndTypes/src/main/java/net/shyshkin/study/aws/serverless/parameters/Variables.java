package net.shyshkin.study.aws.serverless.parameters;

public class Variables {

    public String viewEnvironmentVariables() {
        String envVar1 = System.getenv("MY_ENV_VAR_1");
        System.out.println("MY_ENV_VAR_1 value is `" + envVar1 + "`");
        return envVar1;
    }

}
