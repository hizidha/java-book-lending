package borrowingrecord.model;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

import book.model.Book;
import book.service.BookService;
import customer.model.Customer;
import customer.service.CustomerService;

public class BorrowingRecord {
    private int id;
    private final Book book;
    private final Customer customer;
    private Status status;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public BorrowingRecord(Book book, Customer customer, Status status) {
        this.book = book;
        this.customer = customer;
        this.status = status;
    }

    public BorrowingRecord(int id, Book book, Customer customer, Status status, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.book = book;
        this.customer = customer;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return this.id;
    }

    public Book getBook() {
        return book;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Status getStatus() {
        return this.status;
    }

    public String getUpdatedAt() {
        return updatedAt.toString();
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String convertTypeToString() {
        return Status.convertToString(this.getStatus());
    }

    @Override
    public String toString() {
        return "BorrowingRecord{ Book:" + book + ", Customer:" + customer + ", Status:" + status +
                ", Borrowed At:" + createdAt + ", Returned At:" + updatedAt + " }";
    }
}