import author.model.Author;
import book.model.Book;

import author.service.AuthorService;
import book.service.BookService;
import borrowingrecord.model.BorrowingRecord;
import borrowingrecord.service.BorrowingRecordService;
import customer.model.Customer;
import customer.service.CustomerService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class InputHandler {
    private final Scanner scanner;
    private final AuthorService authorService;
    private final BookService bookService;
    private final CustomerService customerService;
    private final BorrowingRecordService borrowingRecordService;

    public InputHandler() throws SQLException {
        this.authorService = new AuthorService();
        this.customerService = new CustomerService();
        this.borrowingRecordService = new BorrowingRecordService();
        this.bookService = new BookService(customerService, borrowingRecordService, authorService);

        this.scanner = new Scanner(System.in);
    }

    public void run() {
        while (true) {
            menu();
            int choice = getIntInput("Choose an option: ");
            switch (choice) {
                case 1:
                    addBook();
                    break;
                case 2:
                    editBook();
                    break;
                case 3:
                    deleteBook();
                    break;
                case 4:
                    viewBooks();
                    break;
                case 5:
                    getBookById();
                    break;
                case 6:
                    getBookByTitle();
                    break;
                case 7:
                    addAuthor();
                    break;
                case 8:
                    editAuthor();
                    break;
                case 9:
                    deleteAuthor();
                    break;
                case 10:
                    viewAuthors();
                    break;
                case 11:
                    getAuthorById();
                    break;
                case 12:
                    getAuthorByName();
                    break;
                case 13:
                    addCustomer();
                    break;
                case 14:
                    editCustomer();
                    break;
                case 15:
                    deleteCustomer();
                    break;
                case 16:
                    viewCustomers();
                    break;
                case 17:
                    getCustomerById();
                    break;
                case 18:
                    getCustomerByName();
                    break;
                case 19:
                    borrowBook();
                    break;
                case 20:
                    returnBook();
                    break;
                case 21:
                    viewAvailableBooks();
                    break;
                case 22:
                    viewBorrowingRecordByCustomer();
                    break;
                case 23:
                    System.out.println("\nExiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void menu() {
        System.out.println("\n-------------------------------------------");
        System.out.println("-------**   Library System Menu   **-------");
        System.out.println("-------------------------------------------");
        System.out.println("1.  Add Book");
        System.out.println("2.  Edit Book");
        System.out.println("3.  Delete Book");
        System.out.println("4.  View Books list");
        System.out.println("5.  Get Book By Id");
        System.out.println("6.  Get Book By Title");
        System.out.println("7.  Add Author");
        System.out.println("8.  Edit Author");
        System.out.println("9.  Delete Author");
        System.out.println("10  View Author List");
        System.out.println("11. View Author By Id");
        System.out.println("12. View Author By Name");
        System.out.println("13. Add Customer");
        System.out.println("14. Edit Customer");
        System.out.println("15. Delete Customer");
        System.out.println("16. View Customer List");
        System.out.println("17. View Customer By Id");
        System.out.println("18. View Customer By Name");
        System.out.println("19. Borrow a Book");
        System.out.println("20. Return a Book");
        System.out.println("21. View List of Available Books");
        System.out.println("22. View Borrowing Record List by Customer");
        System.out.println("23. Exit");
        System.out.println("-------------------------------------------");
    }

    private Book submenuBook(int existingBookId){
        String authorName = getStringInput("Enter author name: ");
        String title = getStringInput("Enter book title: ");
        int quantity = getIntInput("Enter quantity: ");

        int resultBook = 0;
        Author author = new Author(authorName);

        if (existingBookId == 0) {
            resultBook = bookService.create(new Book(title, author, quantity));
        }

        if (existingBookId != 0) {
            resultBook = bookService.update(new Book(title, author, quantity), existingBookId);
        }

        return this.bookService.getBy(resultBook);
    }

    private void addBook() {
        Book existingBook = submenuBook(0);
        System.out.println("Book added: " + existingBook);
    }

    private void editBook(){
        int bookId = getIntInput("Enter book id: ");
        Book existingBook = this.bookService.getBy(bookId);
        System.out.println(existingBook);

        String option = getStringInput("Are you sure to edit this Book? (Y/N): ");
        if (option.equals("Y") || option.equals("y")) {
            Book updatedBook = submenuBook(existingBook.getId());
            System.out.println("Book edited: " + updatedBook);
        }
    }

    private void deleteBook(){
        int bookId = getIntInput("Enter book id: ");
        System.out.println(this.bookService.getBy(bookId));

        String option = getStringInput("Are you sure to delete this Book? (Y/N): ");
        if (option.equals("Y") || option.equals("y")) {
            this.bookService.deleteBy(bookId);
            System.out.println("Book deleted.");
        }
    }

    private void viewBooks() {
        List<Book> books = this.bookService.getAll();
        if (books.isEmpty()) {
            System.out.println("List of Book not found.");
        }

        if (!books.isEmpty()) {
            books.forEach(System.out::println);
        }
    }

    private void getBookById() {
        int bookId = getIntInput("Enter book id: ");
        System.out.println(this.bookService.getBy(bookId));
    }

    private void getBookByTitle() {
        String bookTitle = getStringInput("Enter book title: ");
        List<Book> books = this.bookService.getBookListBy(bookTitle);
        if (books.isEmpty()) {
            System.out.println("Book title not found.");
        }

        if (!books.isEmpty()) {
            books.forEach(System.out::println);
        }
    }

    private void addAuthor() {
        String authorName = getStringInput("Enter author name: ");
        int authorId = this.authorService.create(new Author(authorName));

        System.out.println("Author added:" + this.authorService.getBy(authorId));
    }

    private void editAuthor() {
        int authorId = getIntInput("Enter author id: ");
        Author existingAuthor = this.authorService.getBy(authorId);
        System.out.println(existingAuthor);

        String option = getStringInput("Are you sure to edit this Author? (Y/N): ");
        if (option.equals("Y") || option.equals("y")) {
            String authorName = getStringInput("Enter new author name: ");
            int updatedAuthorId = this.authorService.update(existingAuthor, authorName);
            System.out.println("Author edited: " + this.authorService.getBy(updatedAuthorId));
        }
    }

    private void deleteAuthor() {
        int authorId = getIntInput("Enter author id: ");
        System.out.println(this.authorService.getBy(authorId));

        String option = getStringInput("Are you sure to delete this Author? (Y/N): ");
        if (option.equals("Y") || option.equals("y")) {
            this.authorService.deleteBy(authorId);
            System.out.println("Author deleted.");
        }
    }

    private void viewAuthors() {
        List<Author> authors = this.authorService.getAll();
        if (authors.isEmpty()) {
            System.out.println("List of Author not found.");
        }

        if (!authors.isEmpty()) {
            authors.forEach(System.out::println);
        }
    }

    private void getAuthorById() {
        int authorId = getIntInput("Enter author id: ");
        System.out.println(this.authorService.getBy(authorId));
    }

    private void getAuthorByName() {
        String authorName = getStringInput("Enter author name: ");
        List<Author> authors = this.authorService.getBy(authorName);
        if (authors.isEmpty()) {
            System.out.println("Author name not found.");
        }

        if (!authors.isEmpty()) {
            authors.forEach(System.out::println);
        }
    }

    private void addCustomer() {
        String customerName = getStringInput("Enter customer name: ");
        int customerId = this.customerService.create(new Customer(customerName));

        System.out.println("Customer added:" + this.customerService.getBy(customerId));
    }

    private void editCustomer() {
        int customerId = getIntInput("Enter customer id: ");
        Customer existingCustomer = this.customerService.getBy(customerId);
        System.out.println(existingCustomer);

        String option = getStringInput("Are you sure to edit this Customer? (Y/N): ");
        if (option.equals("Y") || option.equals("y")) {
            String customerName = getStringInput("Enter new customer name: ");
            int updatedCustomerId = this.customerService.update(existingCustomer, customerName);
            System.out.println("Author edited: " + this.customerService.getBy(updatedCustomerId));
        }
    }

    private void deleteCustomer() {
        int customerId = getIntInput("Enter author id: ");
        System.out.println(this.customerService.getBy(customerId));

        String option = getStringInput("Are you sure to delete this Customer? (Y/N): ");
        if (option.equals("Y") || option.equals("y")) {
            this.customerService.deleteBy(customerId);
            System.out.println("Customer deleted.");
        }
    }

    private void viewCustomers() {
        List<Customer> customers = this.customerService.getAll();
        if (customers.isEmpty()) {
            System.out.println("List of Customer not found.");
        }

        if (!customers.isEmpty()) {
            customers.forEach(System.out::println);
        }
    }

    private void getCustomerById() {
        int customerId = getIntInput("Enter customer id: ");
        System.out.println(this.customerService.getBy(customerId));
    }

    private void getCustomerByName() {
        String customerName = getStringInput("Enter customer name: ");
        List<Customer> customers = this.customerService.getCustomerListBy(customerName);
        if (customers.isEmpty()) {
            System.out.println("Customer name not found.");
        }

        if (!customers.isEmpty()) {
            customers.forEach(System.out::println);
        }
    }

    private void borrowBook() {
        viewAvailableBooks();
        String bookTitle = getStringInput("Enter the book title: ");
        String customerName = getStringInput("Enter customer name: ");

        Book existingBook = this.bookService.getBy(bookTitle);
        Customer existingCustomer = this.customerService.getBy(customerName);

        System.out.println(existingBook);
        System.out.println(existingCustomer);

        String option = getStringInput("The data above is correct? (Y/N): ");
        if (option.equals("Y") || option.equals("y")) {
            int resultBookId = this.borrowingRecordService.create(existingBook, existingCustomer);
            String successMessage = "Book with Title '" + existingBook.getTitle() + "' - "
                                    + existingBook.getAuthor().getName() + " Successfully Borrowed.";
            System.out.println(successMessage);
        }
    }

    private void returnBook() {
        String bookTitle = getStringInput("Enter the book title: ");
        String customerName = getStringInput("Enter customer name: ");

        Book existingBook = this.bookService.getBy(bookTitle);
        Customer existingCustomer = this.customerService.getBy(customerName);

        int resultBookId = this.borrowingRecordService.update(existingBook, existingCustomer);
        BorrowingRecord existingBorrowingRecord = this.borrowingRecordService.getBy(resultBookId);

        String successMessage = "Book with Title '" + existingBook.getTitle() + "' - "
                + existingBook.getAuthor().getName() + " Successfully Returned at "
                + existingBorrowingRecord.getUpdatedAt() + ".";
        System.out.println(successMessage);
    }

    private void viewAvailableBooks() {
        List<Book> availableBooks = this.bookService.getAvailableBook();
        if (availableBooks.isEmpty()) {
            System.out.println("No Books Available.");
        }

        if (!availableBooks.isEmpty()) {
            availableBooks.forEach(System.out::println);
        }
    }

    private void viewBorrowingRecordByCustomer() {
        String customerName = getStringInput("Enter customer name: ");
        Customer existingCustomer = this.customerService.getBy(customerName);

        List<BorrowingRecord> borrowingRecords = this.borrowingRecordService.getByCustomer(existingCustomer.getId());
        if (borrowingRecords.isEmpty()) {
            System.out.println("Customer name not found.");
        }

        if (!borrowingRecords.isEmpty()) {
            borrowingRecords.forEach(System.out::println);
        }
    }

    private int getIntInput(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.print("Invalid input. " + prompt);
            scanner.next();
        }
        int value = scanner.nextInt();
        scanner.nextLine();
        return value;
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
}