package dao;

import model.Cuenta;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public interface CuentaDAO {
    /** Busca la cuenta asociada al usuario */
    Cuenta findByUserId(int userId) throws SQLException;

    /** Busca la cuenta por su número (para transferencias) */
    Cuenta findByAccountNumber(String accountNumber) throws SQLException;

    Cuenta findById(int id) throws SQLException;

    /** Inserta una nueva cuenta */
    void create(Cuenta account) throws SQLException;

    /** Sólo actualiza el saldo de una cuenta existente */
    void updateBalance(int accountId, BigDecimal newBalance) throws SQLException;

    /** Lista todas las cuentas (para estadísticas o vistas de empleado) */
    List<Cuenta> findAll() throws SQLException;
}
