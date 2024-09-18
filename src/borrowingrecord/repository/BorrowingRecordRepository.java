package borrowingrecord.repository;

import author.service.AuthorService;
import book.model.Book;
import book.service.BookService;
import borrowingrecord.model.BorrowingRecord;
import borrowingrecord.model.Status;
import borrowingrecord.service.BorrowingRecordService;
import common.CRUDRepository;
import customer.model.Customer;
import customer.service.CustomerService;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BorrowingRecordRepository implements CRUDRepository<BorrowingRecord> {
    private final Connection connection;
    private BookService bookService;
    private CustomerService customerService;
    private BorrowingRecordService borrowingRecordService;
    private AuthorService authorService;

    public BorrowingRecordRepository(Connection connection) throws SQLException {
        this.connection = connection;
        this.customerService = new CustomerService();
        this.bookService = new BookService(customerService, borrowingRecordService, authorService);
    }

    @Override
    public int add(BorrowingRecord t) {
        String sql = "INSERT INTO borrowing_record (book_id, customer_id, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, t.getBook().getId());
            statement.setInt(2, t.getCustomer().getId());
            statement.setString(3, t.convertTypeToString());
            statement.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            statement.setTimestamp(5, null);

            int insertedRow = statement.executeUpdate();
            if (insertedRow > 0) {
                ResultSet resultSet = statement.getGeneratedKeys();

                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int update(BorrowingRecord t) {
        String sql = "UPDATE borrowing_record SET status = ?, updated_at = ? WHERE id = ?";

        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setString(1, t.convertTypeToString());
            statement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            statement.setInt(3, t.getId());

            int isSuccess = statement.executeUpdate();
            if (isSuccess == 1) {
                return t.getId();
            }
            return isSuccess;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int delete(int id) {
        String sql = "DELETE FROM borrowing_record WHERE id = ?";

        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setInt(1, id);

            return statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public List<BorrowingRecord> getAll() {
        List<BorrowingRecord> borrowingRecords = new ArrayList<>();
        String sql = "SELECT * FROM borrowing_record";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                borrowingRecords.add(mapResultSetToBorrowingRecord(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowingRecords;
    }

    @Override
    public Optional<BorrowingRecord> findBy(int id) {
        String sql = "SELECT * FROM borrowing_record WHERE id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    BorrowingRecord borrowingRecord = mapResultSetToBorrowingRecord(resultSet);
                    return Optional.of(borrowingRecord);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<BorrowingRecord> findBy(int bookId, int customerId) {
        String sql = "SELECT * FROM borrowing_record WHERE book_id = ? AND customer_id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, bookId);
            statement.setInt(2, customerId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    BorrowingRecord borrowingRecord = mapResultSetToBorrowingRecord(resultSet);
                    return Optional.of(borrowingRecord);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<BorrowingRecord> findByCustomer(int id) {
        List<BorrowingRecord> borrowingRecords = new ArrayList<>();
        String sql = "SELECT * FROM borrowing_record WHERE customer_id = ? ORDER BY status ASC, updated_at DESC";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    borrowingRecords.add(mapResultSetToBorrowingRecord(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return borrowingRecords;
    }

    private BorrowingRecord mapResultSetToBorrowingRecord(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int bookId = resultSet.getInt("book_id");
        int customerId = resultSet.getInt("customer_id");
        Status status = Status.valueOf(resultSet.getString("status"));
        Timestamp createdAt = resultSet.getTimestamp("created_at");
        Timestamp updatedAt = resultSet.getTimestamp("updated_at");

        Book existingBook = this.bookService.getBy(bookId);
        Customer existingCustomer = this.customerService.getBy(customerId);

        return new BorrowingRecord(id, existingBook, existingCustomer, status, createdAt, updatedAt);
    }
}