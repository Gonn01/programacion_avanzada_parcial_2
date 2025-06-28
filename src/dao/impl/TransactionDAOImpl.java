package dao.impl;

import dao.TransaccionDAO;
import model.Transaccion;
import model.TipoTransaccion;
import util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAOImpl implements TransaccionDAO {
    private final Connection conn;

    public TransactionDAOImpl() throws SQLException {
        this.conn = DBConnection.getInstance().getConnection();
    }

    @Override
    public void add(Transaccion tx) throws SQLException {
        String sql = """
                INSERT INTO transactions
                  (account_id, type, amount, timestamp, target_account_id)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tx.getAccountId());
            ps.setString(2, tx.getType().name());
            ps.setBigDecimal(3, tx.getAmount());
            // timestamp lo pasamos explícito (o null para NOW())
            ps.setTimestamp(4, Timestamp.valueOf(
                    tx.getTimestamp() != null
                            ? tx.getTimestamp()
                            : LocalDateTime.now()));
            if (tx.getTargetAccountId() != null) {
                ps.setInt(5, tx.getTargetAccountId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.executeUpdate();
        }
    }

    @Override
    public List<Transaccion> findByAccountId(int accountId) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ?";
        List<Transaccion> list = new ArrayList<>();
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
    public List<Transaccion> findAll() throws SQLException {
        String sql = "SELECT * FROM transactions";
        List<Transaccion> list = new ArrayList<>();
        try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    /** Mapea una fila a un objeto Transaction */
    private Transaccion map(ResultSet rs) throws SQLException {
        Transaccion tx = new Transaccion();
        tx.setId(rs.getInt("id"));
        tx.setAccountId(rs.getInt("account_id"));
        tx.setType(TipoTransaccion.valueOf(rs.getString("type")));
        tx.setAmount(rs.getBigDecimal("amount"));
        Timestamp ts = rs.getTimestamp("timestamp");
        if (ts != null)
            tx.setTimestamp(ts.toLocalDateTime());

        int tgt = rs.getInt("target_account_id");
        if (!rs.wasNull())
            tx.setTargetAccountId(tgt);

        return tx;
    }
}
