package dao.impl;

import dao.TransaccionDAO;
import model.Transaccion;
import model.TipoTransaccion;
import util.ConexionDB;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransaccionDAOImpl implements TransaccionDAO {
    private final Connection conn;

    public TransaccionDAOImpl() throws SQLException {
        this.conn = ConexionDB.getInstancia().getConexion();
    }

    @Override
    public void add(Transaccion tx) throws SQLException {
        String sql = """
                INSERT INTO transactions
                  (account_id, type, amount, timestamp, target_account_id)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tx.getNumeroCuenta());
            ps.setString(2, tx.getTipo().name());
            ps.setBigDecimal(3, tx.getCantidad());
            // timestamp lo pasamos explícito (o null para NOW())
            ps.setTimestamp(4, Timestamp.valueOf(
                    tx.getFecha() != null
                            ? tx.getFecha()
                            : LocalDateTime.now()));
            if (tx.getCuentaDestinatario() != null) {
                ps.setInt(5, tx.getCuentaDestinatario());
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
    private Transaccion map(ResultSet data) throws SQLException {
        int id = data.getInt("id");

        int accountId = data.getInt("account_id");

        TipoTransaccion tipo = TipoTransaccion.valueOf(data.getString("type"));

        BigDecimal cantidad = data.getBigDecimal("amount");

        LocalDateTime fecha = data.getTimestamp("timestamp").toLocalDateTime();

        Integer cuentaDestinatario = data.getObject("target_account_id", Integer.class);

        Transaccion transaccion = new Transaccion(id, accountId, tipo, cantidad, fecha, cuentaDestinatario);

        return transaccion;
    }

}
