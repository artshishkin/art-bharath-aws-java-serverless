package net.shyshkin.study.aws.serverless.parameters.model;

public class Patient {

    public String name;
    private String ssn;

    public String getName() {
        return name;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "name='" + name + '\'' +
                ", ssn='" + ssn + '\'' +
                '}';
    }
}
