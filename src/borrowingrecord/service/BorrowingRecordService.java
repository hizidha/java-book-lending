package borrowingrecord.service;

import DB.DB;
import book.model.Book;
import borrowingrecord.model.BorrowingRecord;
import borrowingrecord.model.Status;
import borrowingrecord.repository.BorrowingRecordRepository;
import borrowingrecord.service.exception.BookHasBeenReturnedException;
import borrowingrecord.service.exception.BorrowingRecordNotFoundException;
import borrowingrecord.service.exception.FailedToCreateBorrowingRecordException;
import borrowingrecord.service.exception.FailedToUpdateBorrowingRecordException;
import common.GlobalService;
import customer.model.Customer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BorrowingRecordService implements GlobalService<BorrowingRecord> {
    BorrowingRecordRepository borrowingRecordRepository;

    public BorrowingRecordService() throws SQLException {
        Connection connection = DB.connect();
        this.borrowingRecordRepository = new BorrowingRecordRepository(connection);
    }

    @Override
    public List<BorrowingRecord> getAll() {
        return this.borrowingRecordRepository.getAll();
    }

    public List<BorrowingRecord> getByCustomer(int id) {
        return this.borrowingRecordRepository.findByCustomer(id);
    }

    @Override
    public BorrowingRecord getBy(int id) {
        return this.borrowingRecordRepository.findBy(id)
                .orElseThrow(BorrowingRecordNotFoundException::new);
    }

    public BorrowingRecord getBy(int bookId, int customerId) {
        return this.borrowingRecordRepository.findBy(bookId, customerId)
                .orElseThrow(BorrowingRecordNotFoundException::new);
    }

    @Override
    public int deleteBy(int id) {
        return this.borrowingRecordRepository.delete(id);
    }

    public int create(Book book, Customer customer) {
        int result = borrowingRecordRepository.add(new BorrowingRecord(book, customer, Status.BORROWED));

        if (result <= 0) {
            throw new FailedToCreateBorrowingRecordException();
        }
        return result;
    }

    public int update(Book book, Customer customer) {
        BorrowingRecord existingBorrowingRecord = this.getBy(book.getId(), customer.getId());
        int result = 0;

        if (existingBorrowingRecord.getStatus() != Status.BORROWED) {
            throw new BookHasBeenReturnedException();
        }

        if (existingBorrowingRecord.getStatus() == Status.BORROWED) {
            existingBorrowingRecord.setStatus(Status.RETURNED);
            result = borrowingRecordRepository.update(existingBorrowingRecord);

            if (result <= 0) {
                throw new FailedToUpdateBorrowingRecordException();
            }
        }
        return result;
    }
}