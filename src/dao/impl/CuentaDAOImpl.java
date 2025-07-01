package dao.impl;

import dao.CuentaDAO;
import model.Cuenta;
import util.ConexionDB;

import java.math.BigDecimal;
import java.sql.*;

public class CuentaDAOImpl implements CuentaDAO {
    private final Connection conexion;

    public CuentaDAOImpl() throws SQLException {
        this.conexion = ConexionDB.getInstancia().getConexion();
    }

    @Override
    public Cuenta findByUserId(int idUsuario) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE user_id = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return map(rs);
                return null;
            }
        }
    }

    @Override
    public Cuenta findByAccountNumber(String numeroCuenta) throws SQLException {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, numeroCuenta);
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

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    @Override
    public void actualizarSaldo(int cuentaId, BigDecimal nuevoSaldo) throws SQLException {
        String sql = "UPDATE accounts SET balance = ? WHERE id = ?";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setBigDecimal(1, nuevoSaldo);
            ps.setInt(2, cuentaId);
            ps.executeUpdate();
        }
    }

    private Cuenta map(ResultSet data) throws SQLException {
        final int id = data.getInt("id");

        final int idUsuario = data.getInt("user_id");

        final String numero = data.getString("account_number");

        final BigDecimal saldo = data.getBigDecimal("balance");

        Cuenta cuenta = new Cuenta(id, idUsuario, numero, saldo);

        return cuenta;
    }
}
