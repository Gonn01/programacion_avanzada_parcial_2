package dao;

import model.Transaccion;
import java.sql.SQLException;
import java.util.List;

public interface TransaccionDAO {
    /** Registra una nueva transacción */
    void registrar(Transaccion transaccion) throws SQLException;

    /** Todas las transacciones de una cuenta */
    List<Transaccion> findByAccountId(int accountId) throws SQLException;

    /** Todas las transacciones del sistema */
    List<Transaccion> findAll() throws SQLException;
}
