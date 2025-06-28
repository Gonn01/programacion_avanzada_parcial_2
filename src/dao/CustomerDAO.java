package dao;

import model.Customer;
import java.sql.SQLException;

public interface CustomerDAO {
    Customer findByUsernameAndPassword(String username, String password) throws SQLException;

    Customer findById(int id) throws SQLException;

    void create(Customer customer) throws SQLException;
}