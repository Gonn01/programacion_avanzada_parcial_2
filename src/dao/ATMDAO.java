package dao;

import model.ATM;
import java.sql.SQLException;

public interface ATMDAO {
    ATM getSaldo() throws SQLException;

    void actualizarSaldo(ATM saldo) throws SQLException;
}