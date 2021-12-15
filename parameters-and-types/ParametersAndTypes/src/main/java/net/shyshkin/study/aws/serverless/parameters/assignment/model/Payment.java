package net.shyshkin.study.aws.serverless.parameters.assignment.model;

import java.math.BigDecimal;

public class Payment {

    private String name;
    private BigDecimal amount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Payment{" +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                '}';
    }
}
