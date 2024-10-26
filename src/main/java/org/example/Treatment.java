package org.example;

public class Treatment {
    private static int idCounter = 1;
    private int id;
    private String name;
    private Double price;

    public Treatment(String name, Double price) {
        this.name = name;
        this.price = price;
        this.id=idCounter++;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
