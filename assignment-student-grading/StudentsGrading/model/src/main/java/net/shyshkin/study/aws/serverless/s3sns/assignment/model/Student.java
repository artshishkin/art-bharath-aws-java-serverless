package net.shyshkin.study.aws.serverless.s3sns.assignment.model;

public class Student {

    public int roleNumber;
    public String name;
    public int testScore;

    public Student() {
    }

    public Student(int roleNumber, String name, int testScore) {
        this.roleNumber = roleNumber;
        this.name = name;
        this.testScore = testScore;
    }

    @Override
    public String toString() {
        return "Student{" +
                "roleNumber=" + roleNumber +
                ", name='" + name + '\'' +
                ", testScore=" + testScore +
                '}';
    }
}
