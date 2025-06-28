package dao.impl;

import dao.CuentaDAO;
import model.Cuenta;
import util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CuentaDAOImpl implements CuentaDAO {
    private final Connection conn;

    public CuentaDAOImpl() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    @Override
    public Cuenta findByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return map(rs);
                return null;
            }
        }
    }

    @Override
    public Cuenta findByAccountNumber(String accountNumber) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, accountNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return map(rs);
                return null;
            }
        }
    }

    @Override
    public Cuenta findById(int id) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public void create(Cuenta account) throws SQLException {
        String sql = "INSERT INTO accounts(user_id, account_number, balance) VALUES(?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, account.getUserId());
            ps.setString(2, account.getAccountNumber());
            ps.setBigDecimal(3, account.getBalance());
            ps.executeUpdate();
        }
    }

    @Override
    public void updateBalance(int accountId, BigDecimal newBalance) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, newBalance);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Cuenta> findAll() throws SQLException {
        String sql = "SELECT * FROM accounts";
        List<Cuenta> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    private Cuenta map(ResultSet rs) throws SQLException {
        Cuenta a = new Cuenta();
        a.setId(rs.getInt("id"));
        a.setUserId(rs.getInt("user_id"));
        a.setAccountNumber(rs.getString("account_number"));
        a.setBalance(rs.getBigDecimal("balance"));
        return a;
    }
}
