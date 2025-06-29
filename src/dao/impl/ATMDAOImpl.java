package dao.impl;

import dao.ATMDAO;
import model.ATM;
import util.ConexionDB;
import java.sql.*;

public class ATMDAOImpl implements ATMDAO {
    private Connection conexion;

    public ATMDAOImpl() throws SQLException {
        conexion = ConexionDB.getInstancia().getConexion();
    }

    @Override
    public ATM getSaldo() throws SQLException {
        String sql = "SELECT * FROM atm_cash WHERE id=1";

        try (Statement st = conexion.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return new ATM(rs.getInt("id"), rs.getBigDecimal("total_cash"));
            }
            return null;
        }
    }

    @Override
    public void actualizarSaldo(ATM atm) throws SQLException {
        String sql = "UPDATE atm_cash SET total_cash=? WHERE id=1";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setBigDecimal(1, atm.getSaldo());
            ps.executeUpdate();
        }
    }
}