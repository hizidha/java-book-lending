package book.repository;

import author.model.Author;
import author.service.AuthorService;
import book.model.Book;
import common.CRUDRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository implements CRUDRepository<Book> {
    private final Connection connection;
    private final AuthorService authorService;

    public BookRepository(Connection connection) throws SQLException {
        this.connection = connection;
        this.authorService = new AuthorService();
    }

    @Override
    public int add(Book t) {
        String sql = "INSERT INTO book (title, author_id, quantity) VALUES (?, ?, ?)";

        try {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, t.getTitle());
            statement.setInt(2, t.getAuthor().getId());
            statement.setInt(3, t.getQuantity());

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
    public int update(Book t) {
        String sql = "UPDATE book SET title = ?, author_id = ?, quantity = ? WHERE id = ?";

        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setString(1, t.getTitle());
            statement.setInt(2, t.getAuthor().getId());
            statement.setInt(3, t.getQuantity());
            statement.setInt(4, t.getId());

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
        String sql = "DELETE FROM book WHERE id = ?";

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
    public List<Book> getAll() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                books.add(mapResultSetToBook(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> getAvailableBook() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE quantity > 0";

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                books.add(mapResultSetToBook(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    @Override
    public Optional<Book> findBy(int id) {
        String sql = "SELECT * FROM book WHERE id = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Book book = mapResultSetToBook(resultSet);
                    return Optional.of(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<Book> findBy(String title) {
        String sql = "SELECT * FROM book WHERE UPPER(title) = ?";

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, title.toUpperCase());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Book book = mapResultSetToBook(resultSet);
                    return Optional.of(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public List<Book> findBookListBy(String title) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM book WHERE UPPER(title) LIKE ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + title.toUpperCase() + "%");

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    books.add(mapResultSetToBook(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    private Book mapResultSetToBook(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String title = resultSet.getString("title");
        int authorId = resultSet.getInt("author_id");
        int quantity = resultSet.getInt("quantity");

        Author existingAuthor = this.authorService.getBy(authorId);

        return new Book(id, title, existingAuthor, quantity);
    }

    public int checkBook(String title) {
        String sql = "SELECT id FROM book WHERE UPPER(title) = ?";

        try {
            PreparedStatement statement = this.connection.prepareStatement(sql);
            statement.setString(1, title.toUpperCase());

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
