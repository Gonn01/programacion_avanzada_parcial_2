package dao.impl;

import dao.CustomerDAO;
import model.Customer;
import util.DBConnection;
import java.sql.*;

public class CustomerDAOImpl implements CustomerDAO {
    private Connection conn;

    public CustomerDAOImpl() throws SQLException {
        conn = DBConnection.getInstance().getConnection();
    }

    @Override
    public Customer findByUsernameAndPassword(String username, String password) throws SQLException {
        String sql = "SELECT * FROM customers WHERE username=? AND password=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                            rs.getString("full_name"));
                }
                return null;
            }
        }
    }

    @Override
    public Customer findById(int id) throws SQLException {
        String sql = "SELECT * FROM customers WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(rs.getInt("id"), rs.getString("username"), rs.getString("password"),
                            rs.getString("full_name"));
                }
                return null;
            }
        }
    }

    @Override
    public void create(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers(username,password,full_name) VALUES(?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getUsername());
            ps.setString(2, customer.getPassword());
            ps.setString(3, customer.getFullName());
            ps.executeUpdate();
        }
    }
}