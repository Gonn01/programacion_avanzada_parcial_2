package service;

import dao.CuentaDAO;
import dao.ATMSaldoDAO;
import dao.TransaccionDAO;
import dao.UsuarioDAO;
import dao.impl.CuentaDAOImpl;
import dao.impl.ATMSaldoDAOImpl;
import dao.impl.TransactionDAOImpl;
import dao.impl.UserDAOImpl;
import model.Cuenta;
import model.ATMSaldo;
import model.Transaccion;
import model.TipoTransaccion;
import model.User;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ATMService {
    private final UsuarioDAO userDAO;
    private final CuentaDAO accountDAO;
    private final TransaccionDAO txDAO;
    private final ATMSaldoDAO cashDAO;

    public ATMService() throws SQLException {
        this.userDAO = new UserDAOImpl();
        this.accountDAO = new CuentaDAOImpl();
        this.txDAO = new TransactionDAOImpl();
        this.cashDAO = new ATMSaldoDAOImpl();
    }

    /** Autentica y devuelve el User (empleado o cliente) o null si no encuentra */
    public User login(String username, String passwordHash) throws SQLException {
        return userDAO.findByUsernameAndHash(username, passwordHash);
    }

    /** Devuelve el saldo de la cuenta asociada a este usuario */
    public BigDecimal getBalance(int userId) throws SQLException {
        Cuenta acct = accountDAO.findByUserId(userId);
        return acct.getBalance();
    }

    /** Lista todas las transacciones de la cuenta de este usuario */
    public List<Transaccion> getUserTransactions(int userId) throws SQLException {
        Cuenta acct = accountDAO.findByUserId(userId);
        return txDAO.findByAccountId(acct.getId());
    }

    /** Lista todas las transacciones (solo empleado) */
    public List<Transaccion> getAllTransactions() throws SQLException {
        return txDAO.findAll();
    }

    public Cuenta getAccountById(int accountId) throws SQLException {
        return accountDAO.findById(accountId);
    }

    /** Hace un depósito: actualiza cuenta, registra TX y suma efectivo al cajero */
    public void deposit(int userId, BigDecimal amount) throws SQLException {
        Cuenta acct = accountDAO.findByUserId(userId);
        BigDecimal newBal = acct.getBalance().add(amount);
        accountDAO.updateBalance(acct.getId(), newBal);
        txDAO.add(new Transaccion(
                0,
                acct.getId(),
                TipoTransaccion.DEPOSITO,
                amount,
                LocalDateTime.now(),
                null));
        ATMSaldo inv = cashDAO.getSaldo();
        inv.setTotalCash(inv.getTotalCash().add(amount));
        cashDAO.actualizarSaldo(inv);
    }

    /**
     * Hace un retiro: valida fondos de cuenta y del cajero, registra TX y descuenta
     * efectivo
     */
    public void withdraw(int userId, BigDecimal amount) throws SQLException {
        Cuenta acct = accountDAO.findByUserId(userId);
        if (acct.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
        ATMSaldo inv = cashDAO.getSaldo();
        if (inv.getTotalCash().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Cajero sin efectivo suficiente");
        }
        BigDecimal newBal = acct.getBalance().subtract(amount);
        accountDAO.updateBalance(acct.getId(), newBal);
        txDAO.add(new Transaccion(
                0,
                acct.getId(),
                TipoTransaccion.RETIRO,
                amount,
                LocalDateTime.now(),
                null));
        inv.setTotalCash(inv.getTotalCash().subtract(amount));
        cashDAO.actualizarSaldo(inv);
    }

    /**
     * Transfiere de la cuenta del usuario a otra cuenta (por número):
     * actualiza ambos saldos y registra la TX con target_account_id.
     */
    public void transfer(int userId, String toAccountNumber, BigDecimal amount) throws SQLException {
        Cuenta from = accountDAO.findByUserId(userId);
        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
        Cuenta to = accountDAO.findByAccountNumber(toAccountNumber);
        if (to == null) {
            throw new IllegalArgumentException("Cuenta destino no encontrada");
        }
        // Actualizar saldos
        accountDAO.updateBalance(from.getId(), from.getBalance().subtract(amount));
        accountDAO.updateBalance(to.getId(), to.getBalance().add(amount));
        // Registrar transacción
        txDAO.add(new Transaccion(
                0,
                from.getId(),
                TipoTransaccion.TRANSFERENCIA,
                amount,
                LocalDateTime.now(),
                to.getId()));
    }

    /** Reponer efectivo al cajero (solo empleado) */
    public void refillCash(BigDecimal amount) throws SQLException {
        ATMSaldo inv = cashDAO.getSaldo();
        inv.setTotalCash(inv.getTotalCash().add(amount));
        cashDAO.actualizarSaldo(inv);
    }

    /**
     * Estadísticas diarias por tipo de transacción (empleado):
     * devuelve un mapa {DEPOSIT→#count, WITHDRAWAL→#count, TRANSFER→#count}
     */
    public Map<TipoTransaccion, Long> dailyStats() throws SQLException {
        List<Transaccion> all = txDAO.findAll();
        return all.stream()
                .collect(Collectors.groupingBy(
                        Transaccion::getType,
                        Collectors.counting()));
    }
}
