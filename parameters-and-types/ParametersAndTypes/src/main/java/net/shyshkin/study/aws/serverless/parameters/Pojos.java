package net.shyshkin.study.aws.serverless.parameters;

import net.shyshkin.study.aws.serverless.parameters.model.ClinicalData;
import net.shyshkin.study.aws.serverless.parameters.model.Patient;

/**
 * Handler for requests to Lambda function.
 */
public class Pojos {

    public ClinicalData getClinicalData(Patient patient) {

        System.out.println("Getting clinical data for: " + patient);
        return new ClinicalData("120/80","68");
    }

}
