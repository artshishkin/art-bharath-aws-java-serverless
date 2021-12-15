package net.shyshkin.study.aws.serverless.parameters.assignment;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;

import java.util.Map;

public class TruckTracker {

    public void truckTracker(Map<String, Double> coordinates, Context context) {
        LambdaLogger logger = context.getLogger();
        Double latitude = coordinates.get("latitude");
        Double longitude = coordinates.get("longitude");
        logger.log("Latitude: " + latitude + "; Longitude: " + longitude);
    }

}
