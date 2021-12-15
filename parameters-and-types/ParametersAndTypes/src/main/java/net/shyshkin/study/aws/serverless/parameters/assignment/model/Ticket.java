package net.shyshkin.study.aws.serverless.parameters.assignment.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Ticket {
    private LocalDateTime date;
    private BigDecimal price;
    private String name;
    private String description;
    private String customerName;
    private Place place;

    public Ticket() {
    }

    public Ticket(LocalDateTime date, BigDecimal price, String name, String description, String customerName, Place place) {
        this.date = date;
        this.price = price;
        this.name = name;
        this.description = description;
        this.customerName = customerName;
        this.place = place;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "date=" + date +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", customerName='" + customerName + '\'' +
                ", place=" + place +
                '}';
    }
}
