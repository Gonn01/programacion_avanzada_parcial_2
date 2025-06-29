package dao;

import model.Cuenta;
import java.math.BigDecimal;
import java.sql.SQLException;

public interface CuentaDAO {
    /** Busca la cuenta asociada al usuario */
    Cuenta findByUserId(int userId) throws SQLException;

    /** Busca la cuenta por su número (para transferencias) */
    Cuenta findByAccountNumber(String accountNumber) throws SQLException;

    Cuenta findById(int id) throws SQLException;

    /** Sólo actualiza el saldo de una cuenta existente */
    void updateBalance(int accountId, BigDecimal newBalance) throws SQLException;
}
