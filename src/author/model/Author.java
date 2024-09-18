package author.model;

import java.util.Objects;

public class Author {
    private int id;
    private final String name;

    public Author(String name){
        this.name = name;
    }

    public Author(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Author{ id:" + this.id +
                ", name:" + this.name + " }";
    }
}