package dao;

import model.ATMSaldo;
import java.sql.SQLException;

public interface ATMSaldoDAO {
    ATMSaldo getSaldo() throws SQLException;

    void actualizarSaldo(ATMSaldo saldo) throws SQLException;
}