package net.shyshkin.study.aws.serverless.s3sns.assignment.model;

public class StudentWithGrade {

    public int roleNumber;
    public String name;
    public int testScore;
    public String grade;

    public StudentWithGrade() {
    }

    public StudentWithGrade(int roleNumber, String name, int testScore, String grade) {
        this.roleNumber = roleNumber;
        this.name = name;
        this.testScore = testScore;
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "StudentWithGrade{" +
                "roleNumber=" + roleNumber +
                ", name='" + name + '\'' +
                ", testScore=" + testScore +
                ", grade='" + grade + '\'' +
                '}';
    }
}
