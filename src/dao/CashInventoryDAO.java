package dao;

import model.CashInventory;
import java.sql.SQLException;

public interface CashInventoryDAO {
    CashInventory getInventory() throws SQLException;

    void update(CashInventory inventory) throws SQLException;
}