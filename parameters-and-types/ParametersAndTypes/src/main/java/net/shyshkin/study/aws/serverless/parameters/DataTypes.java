package net.shyshkin.study.aws.serverless.parameters;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

    public List<Integer> getScores() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return IntStream.range(0, 5)
                .boxed()
                .map(i -> random.nextInt(1, 100))
                .collect(Collectors.toList());
    }

    public List<Integer> getScoresByName(List<String> names) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return names.stream()
                .map(name -> random.nextInt(1, 100))
                .collect(Collectors.toList());
    }

    public void saveEmployeeData(Map<String, Integer> employeeData) {
        System.out.println("Employee data has been saved: " + employeeData);
    }

    public Map<String, List<Integer>> getStudentScores() {
        return Map.of(
                "Art", getRandomList(),
                "Kate", getRandomList(),
                "Arina", getRandomList(),
                "Nazar", getRandomList()
        );
    }

    private List<Integer> getRandomList() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return IntStream.range(0, random.nextInt(1, 10))
                .boxed()
                .map(i -> random.nextInt(1, 100))
                .collect(Collectors.toList());
    }

}
