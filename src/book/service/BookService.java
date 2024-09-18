package book.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import DB.DB;
import author.service.AuthorService;
import borrowingrecord.service.BorrowingRecordService;
import borrowingrecord.service.exception.BorrowingRecordNotFoundException;
import common.GlobalService;

import book.model.Book;
import customer.model.Customer;
import book.repository.BookRepository;
import book.service.exception.*;
import customer.service.CustomerService;

public class BookService implements GlobalService<Book> {
    BookRepository bookRepository;
    CustomerService customerService;
    BorrowingRecordService borrowingRecordService;
    AuthorService authorService;

    public BookService(CustomerService customerService, BorrowingRecordService borrowingRecordService, AuthorService authorService) throws SQLException {
        Connection connection = DB.connect();
        this.bookRepository = new BookRepository(connection);
        this.customerService = customerService;
        this.borrowingRecordService = borrowingRecordService;
        this.authorService = authorService;
    }

    @Override
    public List<Book> getAll() {
        return this.bookRepository.getAll();
    }

    @Override
    public Book getBy(int id) {
        return this.bookRepository.findBy(id)
                .orElseThrow(BookNotFoundException::new);
    }

    public Book getBy(String title) {
        return this.bookRepository.findBy(title)
                .orElseThrow(BookNotFoundException::new);
    }

    public List<Book> getBookListBy(String title) {
        return this.bookRepository.findBookListBy(title);
    }

    public List<Book> getAvailableBook() {
        return this.bookRepository.getAvailableBook();
    }

    @Override
    public int deleteBy(int id) {
        return this.bookRepository.delete(id);
    }

    public int create(Book book) {
        int existingBook = this.bookRepository.checkBook(book.getTitle());
        if (existingBook > 0) {
            throw new BookHasBeenAddedException();
        }

        int idAuthor = authorService.createFromBook(book.getAuthor());
        book.getAuthor().setId(idAuthor);

        int newBook = this.bookRepository.add(book);
        if (newBook <= 0) {
            throw new FailedToAddBookException();
        }
        return newBook;
    }

    public int update(Book book, int id) {
        int idAuthor = authorService.create(book.getAuthor());
        book.getAuthor().setId(idAuthor);
        book.setId(id);

        int result = this.bookRepository.update(book);
        if (result <= 0) {
            throw new FailedToUpdateBookDataException();
        }
        return result;
    }

    public int borrowed(Book book, Customer customer) {
        Book existingBook = this.getBy(book.getId());
        Customer existingCustomer = this.customerService.getBy(customer.getId());

        int result = bookRepository.update(existingBook.decreaseQuantity());
        if (result <= 0) {
            throw new FailedToUpdateBookDataException();
        }
        this.borrowingRecordService.create(existingBook, customer);

        return 1;
    }

    public int returned(Book book, Customer customer) {
        int existingBorrowingRecord = this.borrowingRecordService.update(book, customer);
        if (existingBorrowingRecord <= 0) {
            throw new BorrowingRecordNotFoundException();
        }

        int result = bookRepository.update(this.getBy(book.getId()).increaseQuantity());
        if (result <= 0) {
            throw new FailedToUpdateBookDataException();
        }
        return 1;
    }
}