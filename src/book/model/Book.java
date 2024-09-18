package book.model;

import author.model.Author;
import book.model.exception.*;

import java.util.Objects;

public class Book {
    private int id;
    private final String title;
    private final Author author;
    private int quantity;

    public Book(String title, Author author, int quantity){
        this.title = title;
        this.author = author;
        this.quantity = quantity;
    }

    public Book(int id, String title, Author author, int quantity){
        this.id = id;
        this.title = title;
        this.author = author;
        this.quantity = quantity;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public Author getAuthor() {
        return this.author;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public Book increaseQuantity() {
        return new Book(this.getTitle(), this.getAuthor(), this.quantity++);
    }

    public Book decreaseQuantity() {
        if (this.quantity > 0) {
            throw new NotEnoughToBorrowException();
        }
        return new Book(this.getTitle(), this.getAuthor(), this.quantity--);
    }

    @Override
    public String toString() {
        return "Book{ " +
                "id:" + this.id + ", title:" + this.title +
                ", quantity:" + this.quantity + ", author:" + this.author +  "}";
    }
}