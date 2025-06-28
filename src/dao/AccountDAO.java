package dao;

import model.Account;
import java.sql.SQLException;
import java.util.List;

public interface AccountDAO {
    Account findByCustomerId(int customerId) throws SQLException;

    Account findById(int id) throws SQLException;

    void update(Account account) throws SQLException;

    List<Account> findAll() throws SQLException;
}
