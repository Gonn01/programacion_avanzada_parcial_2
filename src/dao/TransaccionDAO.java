package dao;

import model.Transaccion;
import java.sql.SQLException;
import java.util.List;

public interface TransaccionDAO {
    void registrar(Transaccion transaccion) throws SQLException;

    List<Transaccion> findByAccountId(int accountId) throws SQLException;

    List<Transaccion> findAll() throws SQLException;
}
