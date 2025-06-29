package dao.impl;

import dao.ATMSaldoDAO;
import model.ATMSaldo;
import util.ConexionDB;
import java.sql.*;

public class ATMSaldoDAOImpl implements ATMSaldoDAO {
    private Connection conn;

    public ATMSaldoDAOImpl() throws SQLException {
        conn = ConexionDB.getInstancia().getConexion();
    }

    @Override
    public ATMSaldo getSaldo() throws SQLException {
        String sql = "SELECT * FROM atm_cash WHERE id=1";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return new ATMSaldo(rs.getInt("id"), rs.getBigDecimal("total_cash"));
            }
            return null;
        }
    }

    @Override
    public void actualizarSaldo(ATMSaldo inventory) throws SQLException {
        String sql = "UPDATE atm_cash SET total_cash=? WHERE id=1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, inventory.getTotalCash());
            ps.executeUpdate();
        }
    }
}