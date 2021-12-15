package net.shyshkin.study.aws.serverless.parameters.model;

public class ClinicalData {

    private String bloodPressure;
    private String heardRate;

    public ClinicalData() {
    }

    public ClinicalData(String bloodPressure, String heardRate) {
        this.bloodPressure = bloodPressure;
        this.heardRate = heardRate;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public String getHeardRate() {
        return heardRate;
    }

    public void setHeardRate(String heardRate) {
        this.heardRate = heardRate;
    }
}
