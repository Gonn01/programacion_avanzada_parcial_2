package dao.impl;

import dao.CashInventoryDAO;
import model.CashInventory;
import util.DBConnection;
import java.sql.*;

public class CashInventoryDAOImpl implements CashInventoryDAO {
    private Connection conn;

    public CashInventoryDAOImpl() throws SQLException {
        conn = DBConnection.getInstance().getConnection();
    }

    @Override
    public CashInventory getInventory() throws SQLException {
        String sql = "SELECT * FROM atm_cash WHERE id=1";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return new CashInventory(rs.getInt("id"), rs.getBigDecimal("total_cash"));
            }
            return null;
        }
    }

    @Override
    public void update(CashInventory inventory) throws SQLException {
        String sql = "UPDATE atm_cash SET total_cash=? WHERE id=1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, inventory.getTotalCash());
            ps.executeUpdate();
        }
    }
}