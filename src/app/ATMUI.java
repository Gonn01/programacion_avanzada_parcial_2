package app;

import model.Cuenta;
import model.Transaccion;
import model.TipoTransaccion;
import model.Usuario;
import model.TipoUsuario;
import service.ATMService;
import util.HashUtil;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ATMUI extends JFrame {

    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);

    private ATMService service;
    private Usuario usuarioLogeado;

    private JTextField textfieldUsuario = new JTextField(15);
    private JPasswordField textfieldPassword = new JPasswordField(15);
    private JLabel textoError = new JLabel();

    private JLabel textoBienvenida = new JLabel();
    private JTextArea outputArea = new JTextArea(10, 30);

    private JButton botonVerSaldo = new JButton("Ver Saldo");
    private JButton botonDepositar = new JButton("Depositar");
    private JButton botonRetirar = new JButton("Retirar");
    private JButton botonTransferir = new JButton("Transferir");
    private JButton botonVerTransacciones = new JButton("Ver Transacciones");
    private JButton botonReponerEfectivo = new JButton("Reponer Efectivo");
    private JButton botonCerrarSesion = new JButton("Logout");

    public ATMUI() throws SQLException {
        super("Cajero Automático");
        service = new ATMService();
        initUI();
    }

    private void initUI() {
        JPanel vistaLogin = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        c.gridx = 0;
        c.gridy = 0;
        vistaLogin.add(new JLabel("Usuario:"), c);
        c.gridx = 1;
        vistaLogin.add(textfieldUsuario, c);

        c.gridx = 0;
        c.gridy = 1;
        vistaLogin.add(new JLabel("Clave:"), c);
        c.gridx = 1;
        vistaLogin.add(textfieldPassword, c);

        JButton botonIngresar = new JButton("Ingresar");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        vistaLogin.add(botonIngresar, c);

        textoError.setForeground(Color.RED);
        c.gridy = 3;
        vistaLogin.add(textoError, c);

        botonIngresar.addActionListener(e -> handleLogin());

        JPanel vistaDashboard = new JPanel(new BorderLayout(10, 10));

        JPanel nav = new JPanel(new BorderLayout());
        nav.add(textoBienvenida, BorderLayout.WEST);
        nav.add(botonCerrarSesion, BorderLayout.EAST);
        vistaDashboard.add(nav, BorderLayout.NORTH);

        outputArea.setEditable(false);
        vistaDashboard.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottom.add(botonVerSaldo);
        bottom.add(botonDepositar);
        bottom.add(botonRetirar);
        bottom.add(botonTransferir);
        bottom.add(botonVerTransacciones);
        bottom.add(botonReponerEfectivo);
        vistaDashboard.add(bottom, BorderLayout.SOUTH);

        botonCerrarSesion.addActionListener(e -> cerrarSesion());
        botonVerSaldo.addActionListener(e -> verSaldo());
        botonDepositar.addActionListener(e -> hacerDeposito());
        botonRetirar.addActionListener(e -> hacerRetiro());
        botonTransferir.addActionListener(e -> hacerTransaccion());
        botonVerTransacciones.addActionListener(e -> verTransacciones());
        botonReponerEfectivo.addActionListener(e -> reponerSaldo());

        mainPanel.add(vistaLogin, "login");
        mainPanel.add(vistaDashboard, "dashboard");
        add(mainPanel);

        cardLayout.show(mainPanel, "login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void handleLogin() {
        String usuarioValue = textfieldUsuario.getText().trim();
        String passwordValue = new String(textfieldPassword.getPassword());
        try {
            String hash = HashUtil.sha256(passwordValue);
            usuarioLogeado = service.login(usuarioValue, hash);
            if (usuarioLogeado == null) {
                textoError.setText("Credenciales inválidas");
            } else {
                textoError.setText("");
                setupDashboardFor(usuarioLogeado.getTipoUsuario());
                cardLayout.show(mainPanel, "dashboard");
            }
        } catch (SQLException ex) {
            textoError.setText("Error de conexión");
            ex.printStackTrace();
        }
    }

    private void setupDashboardFor(TipoUsuario type) {
        textoBienvenida.setText("Bienvenido, " + usuarioLogeado.getUsername());
        boolean isEmp = type == TipoUsuario.EMPLEADO;
        botonVerSaldo.setVisible(!isEmp);
        botonDepositar.setVisible(!isEmp);
        botonRetirar.setVisible(!isEmp);
        botonTransferir.setVisible(!isEmp);
        botonReponerEfectivo.setVisible(isEmp);
        outputArea.setText("");
    }

    private void cerrarSesion() {
        textfieldUsuario.setText("");
        textfieldPassword.setText("");
        outputArea.setText("");
        textoError.setText("");
        cardLayout.show(mainPanel, "login");
    }

    private void verSaldo() {
        try {
            BigDecimal saldo = service.getSaldo(usuarioLogeado.getId());
            outputArea.setText("Saldo actual: " + saldo);
        } catch (SQLException e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private void hacerDeposito() {
        String montoString = JOptionPane.showInputDialog(this, "Monto a depositar:");
        if (montoString != null) {
            try {
                BigDecimal monto = new BigDecimal(montoString);
                service.depositar(usuarioLogeado.getId(), monto);
                outputArea.setText("Depositado: " + monto);
            } catch (Exception ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
        }
    }

    private void hacerRetiro() {
        String montoString = JOptionPane.showInputDialog(this, "Monto a retirar:");
        if (montoString != null) {
            try {
                BigDecimal monto = new BigDecimal(montoString);
                service.retiro(usuarioLogeado.getId(), monto);
                outputArea.setText("Retirado: " + monto);
            } catch (Exception ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
        }
    }

    private void hacerTransaccion() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField textfieldCuenta = new JTextField();
        JTextField textfieldMonto = new JTextField();
        panel.add(new JLabel("Cuenta destino:"));
        panel.add(textfieldCuenta);
        panel.add(new JLabel("Monto:"));
        panel.add(textfieldMonto);
        int confirmado = JOptionPane.showConfirmDialog(this, panel, "Transferir", JOptionPane.OK_CANCEL_OPTION);
        if (confirmado == JOptionPane.OK_OPTION) {
            try {
                String cuentaDestinatario = textfieldCuenta.getText().trim();
                BigDecimal monto = new BigDecimal(textfieldMonto.getText().trim());
                service.transfer(usuarioLogeado.getId(), cuentaDestinatario, monto);
                outputArea.setText("Transferido: " + monto + " a cuenta " + cuentaDestinatario);
            } catch (Exception exception) {
                outputArea.setText("Error: " + exception.getMessage());
            }
        }
    }

    private void verTransacciones() {
        try {
            List<Transaccion> transacciones;
            if (usuarioLogeado.getTipoUsuario() == TipoUsuario.EMPLEADO) {
                transacciones = service.getAllTransacciones();
            } else {
                transacciones = service.getTransaccionesUsuario(usuarioLogeado.getId());
            }

            if (transacciones.isEmpty()) {
                outputArea.setText("Sin transacciones");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (Transaccion transaccion : transacciones) {
                sb.append(transaccion.getFecha())
                        .append(" | ")
                        .append(transaccion.getTipo())
                        .append(" | ")
                        .append(transaccion.getCantidad());
                if (transaccion.getTipo() == TipoTransaccion.TRANSFERENCIA
                        && transaccion.getIdCuentaDestinatario() != null) {
                    try {
                        Cuenta toAcct = service.getCuentaById(transaccion.getIdCuentaDestinatario());
                        sb.append(" → ").append(toAcct.getNumero());
                    } catch (SQLException e) {
                        sb.append(" → [Cuenta #" + transaccion.getIdCuentaDestinatario() + "]");
                    }
                }
                sb.append("\n");
            }
            outputArea.setText(sb.toString());

        } catch (SQLException e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private void reponerSaldo() {
        String montoString = JOptionPane.showInputDialog(this, "Monto a reponer en el cajero:");
        if (montoString != null) {
            try {
                BigDecimal monto = new BigDecimal(montoString);
                service.agregarSaldo(monto);
                outputArea.setText("Cajero repuesto con: " + monto);
            } catch (Exception exception) {
                outputArea.setText("Error: " + exception.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ATMUI().setVisible(true);
            } catch (SQLException exception) {
                JOptionPane.showMessageDialog(
                        null,
                        "No se pudo conectar con la base de datos:\n" + exception.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                exception.printStackTrace();
            }
        });
    }
}
