package net.shyshkin.study.aws.serverless.parameters.assignment.model;

public class Place {

    private String name;
    private int row;
    private int column;

    public Place() {
    }

    public Place(String name, int row, int column) {
        this.name = name;
        this.row = row;
        this.column = column;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public String toString() {
        return "Place{" +
                "name='" + name + '\'' +
                ", row=" + row +
                ", column=" + column +
                '}';
    }
}
