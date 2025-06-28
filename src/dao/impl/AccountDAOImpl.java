package dao.impl;

import dao.AccountDAO;
import model.Account;
import util.DBConnection;
import java.sql.*;
import java.util.*;

public class AccountDAOImpl implements AccountDAO {
    private Connection conn;

    public AccountDAOImpl() throws SQLException {
        conn = DBConnection.getInstance().getConnection();
    }

    @Override
    public Account findByCustomerId(int customerId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE customer_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Account(rs.getInt("id"), rs.getInt("customer_id"), rs.getBigDecimal("balance"));
                }
                return null;
            }
        }
    }

    @Override
    public Account findById(int id) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Account(rs.getInt("id"), rs.getInt("customer_id"), rs.getBigDecimal("balance"));
                }
                return null;
            }
        }
    }

    @Override
    public void update(Account account) throws SQLException {
        String sql = "UPDATE accounts SET balance=? WHERE id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, account.getBalance());
            ps.setInt(2, account.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public List<Account> findAll() throws SQLException {
        String sql = "SELECT * FROM accounts";
        List<Account> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Account(rs.getInt("id"), rs.getInt("customer_id"), rs.getBigDecimal("balance")));
            }
        }
        return list;
    }
}