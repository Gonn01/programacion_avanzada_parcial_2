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

    public List<Transaccion> getTransaccionesUsuario(int idUsuario) throws SQLException {
        Cuenta cuenta = cuentaDAO.findByUserId(idUsuario);
        return transaccionDAO.findByAccountId(cuenta.getId());
    }

    public List<Transaccion> getAllTransacciones() throws SQLException {
        return transaccionDAO.findAll();
    }

    public Cuenta getCuentaById(int accountId) throws SQLException {
        return cuentaDAO.findById(accountId);
    }

    public void depositar(int idUsuario, BigDecimal monto) throws SQLException {
        Cuenta cuenta = cuentaDAO.findByUserId(idUsuario);

        BigDecimal nuevoSaldo = cuenta.getSaldo().add(monto);

        cuentaDAO.actualizarSaldo(cuenta.getId(), nuevoSaldo);

        transaccionDAO.registrar(new Transaccion(
                0,
                cuenta.getId(),
                TipoTransaccion.DEPOSITO,
                monto,
                LocalDateTime.now(),
                null));

        ATMSaldo atmSaldo = ATMSaldoDAO.getSaldo();

        atmSaldo.setSaldo(atmSaldo.getSaldo().add(monto));

        ATMSaldoDAO.actualizarSaldo(atmSaldo);
    }

    public void retiro(int idUsuario, BigDecimal monto) throws SQLException {
        Cuenta cuenta = cuentaDAO.findByUserId(idUsuario);
        if (cuenta.getSaldo().compareTo(monto) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        final Transaccion nuevaTransaccion = new Transaccion(
                0,
                cuenta.getId(),
                TipoTransaccion.RETIRO,
                monto,
                LocalDateTime.now(),
                null);
        transaccionDAO.registrar(nuevaTransaccion);

        ATMSaldo inv = ATMSaldoDAO.getSaldo();
        if (inv.getSaldo().compareTo(monto) < 0) {
            throw new IllegalArgumentException("Cajero sin efectivo suficiente");
        }

        BigDecimal nuevoSaldo = cuenta.getSaldo().subtract(monto);

        cuentaDAO.actualizarSaldo(cuenta.getId(), nuevoSaldo);

        inv.setSaldo(inv.getSaldo().subtract(monto));

        ATMSaldoDAO.actualizarSaldo(inv);
    }

    public void transfer(int idUsuario, String numeroCuentaDestinatario, BigDecimal monto) throws SQLException {
        Cuenta cuentaRemitente = cuentaDAO.findByUserId(idUsuario);

        if (cuentaRemitente.getSaldo().compareTo(monto) < 0) {
            throw new IllegalArgumentException("Saldo insuficiente");
        }

        Cuenta cuentaDestinatario = cuentaDAO.findByAccountNumber(numeroCuentaDestinatario);
        if (cuentaDestinatario == null) {
            throw new IllegalArgumentException("Cuenta destino no encontrada");
        }

        cuentaDAO.actualizarSaldo(cuentaRemitente.getId(), cuentaRemitente.getSaldo().subtract(monto));

        cuentaDAO.actualizarSaldo(cuentaDestinatario.getId(), cuentaDestinatario.getSaldo().add(monto));

        final Transaccion transaccion = new Transaccion(
                0,
                cuentaRemitente.getId(),
                TipoTransaccion.TRANSFERENCIA,
                monto,
                LocalDateTime.now(),
                cuentaDestinatario.getId());

        transaccionDAO.registrar(transaccion);
    }

    public void agregarSaldo(BigDecimal amount) throws SQLException {
        ATMSaldo atmSaldo = ATMSaldoDAO.getSaldo();

        atmSaldo.setSaldo(atmSaldo.getSaldo().add(amount));

        ATMSaldoDAO.actualizarSaldo(atmSaldo);
    }
}
