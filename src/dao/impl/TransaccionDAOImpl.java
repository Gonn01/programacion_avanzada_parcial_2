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
    private final Connection conexion;

    public TransaccionDAOImpl() throws SQLException {
        this.conexion = ConexionDB.getInstancia().getConexion();
    }

    @Override
    public void registrar(Transaccion transaccion) throws SQLException {
        String sql = """
                INSERT INTO transactions
                  (account_id, type, amount, timestamp, target_account_id)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, transaccion.getNumeroCuenta());
            ps.setString(2, transaccion.getTipo().name());
            ps.setBigDecimal(3, transaccion.getCantidad());
            ps.setTimestamp(4, Timestamp.valueOf(
                    transaccion.getFecha() != null
                            ? transaccion.getFecha()
                            : LocalDateTime.now()));
            if (transaccion.getIdCuentaDestinatario() != null) {
                ps.setInt(5, transaccion.getIdCuentaDestinatario());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.executeUpdate();
        }
    }

    @Override
    public List<Transaccion> findByAccountId(int idCuenta) throws SQLException {
        String sql = "SELECT * FROM transactions WHERE account_id = ?";
        List<Transaccion> list = new ArrayList<>();
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idCuenta);
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
        try (Statement st = conexion.createStatement();
                ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    private Transaccion map(ResultSet data) throws SQLException {
        int id = data.getInt("id");

        int idCuenta = data.getInt("account_id");

        TipoTransaccion tipo = TipoTransaccion.valueOf(data.getString("type"));

        BigDecimal cantidad = data.getBigDecimal("amount");

        LocalDateTime fecha = data.getTimestamp("timestamp").toLocalDateTime();

        Integer idCuentaDestinatario = data.getObject("target_account_id", Integer.class);

        Transaccion transaccion = new Transaccion(id, idCuenta, tipo, cantidad, fecha, idCuentaDestinatario);

        return transaccion;
    }

}
