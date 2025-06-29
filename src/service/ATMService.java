package service;

import dao.CuentaDAO;
import dao.ATMSaldoDAO;
import dao.TransaccionDAO;
import dao.UsuarioDAO;
import dao.impl.CuentaDAOImpl;
import dao.impl.ATMSaldoDAOImpl;
import dao.impl.TransaccionDAOImpl;
import dao.impl.UsuarioDAOImpl;
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
    private final UsuarioDAO usuarioDAO;
    private final CuentaDAO cuentaDAO;
    private final TransaccionDAO transaccionDAO;
    private final ATMSaldoDAO ATMSaldoDAO;

    public ATMService() throws SQLException {
        this.usuarioDAO = new UsuarioDAOImpl();
        this.cuentaDAO = new CuentaDAOImpl();
        this.transaccionDAO = new TransaccionDAOImpl();
        this.ATMSaldoDAO = new ATMSaldoDAOImpl();
    }

    public User login(String username, String passwordHash) throws SQLException {
        return usuarioDAO.findByUsernameAndPassword(username, passwordHash);
    }

    public BigDecimal getSaldo(int idUsuario) throws SQLException {
        Cuenta cuenta = cuentaDAO.findByUserId(idUsuario);
        return cuenta.getSaldo();
    }

    public List<Transaccion> getTransaccionesUsuario(int userId) throws SQLException {
        Cuenta acct = cuentaDAO.findByUserId(userId);
        return transaccionDAO.findByAccountId(acct.getId());
    }

    public List<Transaccion> getAllTransactions() throws SQLException {
        return transaccionDAO.findAll();
    }

    public Cuenta getAccountById(int accountId) throws SQLException {
        return cuentaDAO.findById(accountId);
    }

    public void deposit(int userId, BigDecimal amount) throws SQLException {
        Cuenta acct = cuentaDAO.findByUserId(userId);
        BigDecimal newBal = acct.getSaldo().add(amount);
        cuentaDAO.updateBalance(acct.getId(), newBal);
        transaccionDAO.add(new Transaccion(
                0,
                acct.getId(),
                TipoTransaccion.DEPOSITO,
                amount,
                LocalDateTime.now(),
                null));
        ATMSaldo inv = ATMSaldoDAO.getSaldo();
        inv.setSaldo(inv.getSaldo().add(amount));
        ATMSaldoDAO.actualizarSaldo(inv);
    }

    /**
     * Hace un retiro: valida fondos de cuenta y del cajero, registra TX y descuenta
     * efectivo
     */
    public void withdraw(int userId, BigDecimal amount) throws SQLException {
        Cuenta acct = cuentaDAO.findByUserId(userId);
        if (acct.getSaldo().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
        ATMSaldo inv = ATMSaldoDAO.getSaldo();
        if (inv.getSaldo().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Cajero sin efectivo suficiente");
        }
        BigDecimal newBal = acct.getSaldo().subtract(amount);
        cuentaDAO.updateBalance(acct.getId(), newBal);
        transaccionDAO.add(new Transaccion(
                0,
                acct.getId(),
                TipoTransaccion.RETIRO,
                amount,
                LocalDateTime.now(),
                null));
        inv.setSaldo(inv.getSaldo().subtract(amount));
        ATMSaldoDAO.actualizarSaldo(inv);
    }

    /**
     * Transfiere de la cuenta del usuario a otra cuenta (por número):
     * actualiza ambos saldos y registra la TX con target_account_id.
     */
    public void transfer(int userId, String toAccountNumber, BigDecimal amount) throws SQLException {
        Cuenta from = cuentaDAO.findByUserId(userId);
        if (from.getSaldo().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }
        Cuenta to = cuentaDAO.findByAccountNumber(toAccountNumber);
        if (to == null) {
            throw new IllegalArgumentException("Cuenta destino no encontrada");
        }
        // Actualizar saldos
        cuentaDAO.updateBalance(from.getId(), from.getSaldo().subtract(amount));
        cuentaDAO.updateBalance(to.getId(), to.getSaldo().add(amount));
        // Registrar transacción
        transaccionDAO.add(new Transaccion(
                0,
                from.getId(),
                TipoTransaccion.TRANSFERENCIA,
                amount,
                LocalDateTime.now(),
                to.getId()));
    }

    /** Reponer efectivo al cajero (solo empleado) */
    public void refillCash(BigDecimal amount) throws SQLException {
        ATMSaldo inv = ATMSaldoDAO.getSaldo();
        inv.setSaldo(inv.getSaldo().add(amount));
        ATMSaldoDAO.actualizarSaldo(inv);
    }

    /**
     * Estadísticas diarias por tipo de transacción (empleado):
     * devuelve un mapa {DEPOSIT→#count, WITHDRAWAL→#count, TRANSFER→#count}
     */
    public Map<TipoTransaccion, Long> dailyStats() throws SQLException {
        List<Transaccion> all = transaccionDAO.findAll();
        return all.stream()
                .collect(Collectors.groupingBy(
                        Transaccion::getTipo,
                        Collectors.counting()));
    }
}
