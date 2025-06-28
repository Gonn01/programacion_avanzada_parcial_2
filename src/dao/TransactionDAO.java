package dao;

import model.Transaction;
import java.sql.SQLException;
import java.util.List;

public interface TransactionDAO {
    void add(Transaction tx) throws SQLException;

    List<Transaction> findByAccountId(int accountId) throws SQLException;

    List<Transaction> findAll() throws SQLException;
}