package customer.model;

import java.util.Objects;

public class Customer {
    private int id;
    private final String name;

    public Customer(String name) {
        this.name = name;
    }

    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return "Customer{ id:" + this.id +
                ", name:" + this.name + " }";
    }
}