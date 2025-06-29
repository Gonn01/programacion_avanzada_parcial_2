package dao;

import model.Cuenta;
import java.math.BigDecimal;
import java.sql.SQLException;

public interface CuentaDAO {
    Cuenta findByUserId(int idUsuario) throws SQLException;

    Cuenta findByAccountNumber(String numeroCuenta) throws SQLException;

    Cuenta findById(int id) throws SQLException;

    void actualizarSaldo(int idCuenta, BigDecimal nuevoSaldo) throws SQLException;
}
