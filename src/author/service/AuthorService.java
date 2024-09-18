package author.service;

import DB.DB;
import common.GlobalService;

import author.model.Author;
import author.repository.AuthorRepository;
import author.service.exception.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AuthorService implements GlobalService<Author> {
    AuthorRepository authorRepository;

    public AuthorService () throws SQLException {
        Connection connection = DB.connect();
        this.authorRepository = new AuthorRepository(connection);
    }

    @Override
    public List<Author> getAll() {
        return this.authorRepository.getAll();
    }

    @Override
    public Author getBy(int id) {
        return this.authorRepository.findBy(id)
                .orElseThrow(AuthorNotFoundException::new);
    }

    public List<Author> getBy(String name) {
        return this.authorRepository.findBy(name);
    }

    @Override
    public int deleteBy(int id) {
        int checkListOfBook = this.authorRepository.checkListOfBook(id);
        if (checkListOfBook != 0) {
            throw new AuthorStillHasListOfBookException();
        }
        return this.authorRepository.delete(id);
    }

    public int create(Author author) {
        int existingAuthor = this.authorRepository.checkAuthor(author.getName());
        if (existingAuthor > 0) {
            throw new AuthorHasBeenAddedException();
        }
        return toBookRepository(author);
    }

    public int createFromBook(Author author) {
        int existingAuthor = this.authorRepository.checkAuthor(author.getName());
        if (existingAuthor > 0) {
            return existingAuthor;
        }
        return toBookRepository(author);
    }

    private int toBookRepository(Author author) {
        int newAuthor = this.authorRepository.add(author);
        if (newAuthor <= 0) {
            throw new FailedToAddAuthorException();
        }
        return newAuthor;
    }

    public int update(Author author, String name) {
        Author existingAuthor = this.getBy(author.getId());

        if (existingAuthor == null) {
            throw new AuthorNotFoundException();
        }

        Author updatedAuthor = new Author(existingAuthor.getId(), name);
        int result = this.authorRepository.update(updatedAuthor);
        if (result <= 0) {
            throw new FailedToUpdateAuthorException();
        }
        return result;
    }
}