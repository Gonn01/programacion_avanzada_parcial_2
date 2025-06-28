package dao.impl;

import dao.TransactionDAO;
import model.Transaction;
import model.TransactionType;
import util.DBConnection;
import java.sql.*;
import java.util.*;

public class TransactionDAOImpl implements TransactionDAO {
    private Connection conn;

    public TransactionDAOImpl() throws SQLException {
        conn = DBConnection.getInstance().getConnection();
    }

    @Override
    public void add(Transaction tx) throws SQLException {
        String sql = "INSERT INTO transactions(account_id,type,amount,target_account_id) VALUES(?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tx.getAccountId());
            ps.setString(2, tx.getType().name());
            ps.setBigDecimal(3, tx.getAmount());
            if (tx.getTargetAccountId() != null)
                ps.setInt(4, tx.getTargetAccountId());
            else
                ps.setNull(4, Types.INTEGER);
            ps.executeUpdate();
        }
    }

    @Override
    public List<Transaction> findByAccountId(int accountId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id=?";
        List<Transaction> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    @Override
    public List<Transaction> findAll() throws SQLException {
        String sql = "SELECT * FROM transactions";
        List<Transaction> list = new ArrayList<>();
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    private Transaction map(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getInt("id"),
                rs.getInt("account_id"),
                TransactionType.valueOf(rs.getString("type")),
                rs.getBigDecimal("amount"),
                rs.getTimestamp("timestamp").toLocalDateTime(),
                rs.getObject("target_account_id") != null ? rs.getInt("target_account_id") : null);
    }
}
