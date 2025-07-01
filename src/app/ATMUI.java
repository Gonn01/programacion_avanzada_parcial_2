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

    // --- Login panel ---
    private JTextField userField = new JTextField(15);
    private JPasswordField passField = new JPasswordField(15);
    private JLabel errorLabel = new JLabel();

    // --- Dashboard panel ---
    private JLabel welcomeLabel = new JLabel();
    private JTextArea outputArea = new JTextArea(10, 30);

    // Botones comunes
    private JButton btnBalance = new JButton("Ver Saldo");
    private JButton btnDeposit = new JButton("Depositar");
    private JButton btnWithdraw = new JButton("Retirar");
    private JButton btnTransfer = new JButton("Transferir");
    private JButton btnViewTx = new JButton("Ver Transacciones");
    private JButton btnRefillCash = new JButton("Reponer Efectivo");
    private JButton btnLogout = new JButton("Logout");

    public ATMUI() throws SQLException {
        super("Cajero Automático");
        service = new ATMService();
        initUI();
    }

    private void initUI() {
        // Login Panel
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);

        c.gridx = 0;
        c.gridy = 0;
        loginPanel.add(new JLabel("Usuario:"), c);
        c.gridx = 1;
        loginPanel.add(userField, c);

        c.gridx = 0;
        c.gridy = 1;
        loginPanel.add(new JLabel("Clave:"), c);
        c.gridx = 1;
        loginPanel.add(passField, c);

        JButton btnLogin = new JButton("Ingresar");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        loginPanel.add(btnLogin, c);

        errorLabel.setForeground(Color.RED);
        c.gridy = 3;
        loginPanel.add(errorLabel, c);

        btnLogin.addActionListener(e -> handleLogin());

        // Dashboard Panel
        JPanel dashPanel = new JPanel(new BorderLayout(10, 10));

        // Top: bienvenida + logout
        JPanel top = new JPanel(new BorderLayout());
        top.add(welcomeLabel, BorderLayout.WEST);
        top.add(btnLogout, BorderLayout.EAST);
        dashPanel.add(top, BorderLayout.NORTH);

        // Center: output
        outputArea.setEditable(false);
        dashPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        // Bottom: operaciones
        JPanel ops = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        ops.add(btnBalance);
        ops.add(btnDeposit);
        ops.add(btnWithdraw);
        ops.add(btnTransfer);
        ops.add(btnViewTx);
        ops.add(btnRefillCash);
        dashPanel.add(ops, BorderLayout.SOUTH);

        // Listeners de botones
        btnLogout.addActionListener(e -> logout());
        btnBalance.addActionListener(e -> doBalance());
        btnDeposit.addActionListener(e -> doDeposit());
        btnWithdraw.addActionListener(e -> doWithdraw());
        btnTransfer.addActionListener(e -> doTransfer());
        btnViewTx.addActionListener(e -> doViewTransactions());
        btnRefillCash.addActionListener(e -> doRefillCash());

        // Añadir panels al mainPanel
        mainPanel.add(loginPanel, "login");
        mainPanel.add(dashPanel, "dashboard");
        add(mainPanel);

        cardLayout.show(mainPanel, "login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void handleLogin() {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());
        try {
            // Hash de la contraseña
            String hash = HashUtil.sha256(pass);
            usuarioLogeado = service.login(user, hash);
            if (usuarioLogeado == null) {
                errorLabel.setText("Credenciales inválidas");
            } else {
                errorLabel.setText("");
                setupDashboardFor(usuarioLogeado.getTipoUsuario());
                cardLayout.show(mainPanel, "dashboard");
            }
        } catch (SQLException ex) {
            errorLabel.setText("Error de conexión");
            ex.printStackTrace();
        }
    }

    private void setupDashboardFor(TipoUsuario type) {
        welcomeLabel.setText("Bienvenido, " + usuarioLogeado.getUsername());
        boolean isEmp = type == TipoUsuario.EMPLEADO;
        btnBalance.setVisible(!isEmp);
        btnDeposit.setVisible(!isEmp);
        btnWithdraw.setVisible(!isEmp);
        btnTransfer.setVisible(!isEmp);
        btnRefillCash.setVisible(isEmp);
        // btnViewTx siempre visible (cliente ve sus TX, empleado las todas)
        outputArea.setText("");
    }

    private void logout() {
        userField.setText("");
        passField.setText("");
        outputArea.setText("");
        errorLabel.setText("");
        cardLayout.show(mainPanel, "login");
    }

    private void doBalance() {
        try {
            BigDecimal bal = service.getSaldo(usuarioLogeado.getId());
            outputArea.setText("Saldo actual: " + bal);
        } catch (SQLException e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private void doDeposit() {
        String s = JOptionPane.showInputDialog(this, "Monto a depositar:");
        if (s != null) {
            try {
                BigDecimal amt = new BigDecimal(s);
                service.depositar(usuarioLogeado.getId(), amt);
                outputArea.setText("Depositado: " + amt);
            } catch (Exception ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
        }
    }

    private void doWithdraw() {
        String s = JOptionPane.showInputDialog(this, "Monto a retirar:");
        if (s != null) {
            try {
                BigDecimal amt = new BigDecimal(s);
                service.retiro(usuarioLogeado.getId(), amt);
                outputArea.setText("Retirado: " + amt);
            } catch (Exception ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
        }
    }

    private void doTransfer() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField accField = new JTextField();
        JTextField amtField = new JTextField();
        panel.add(new JLabel("Cuenta destino:"));
        panel.add(accField);
        panel.add(new JLabel("Monto:"));
        panel.add(amtField);
        int ok = JOptionPane.showConfirmDialog(this, panel, "Transferir", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                String toAcct = accField.getText().trim();
                BigDecimal amt = new BigDecimal(amtField.getText().trim());
                service.transfer(usuarioLogeado.getId(), toAcct, amt);
                outputArea.setText("Transferido: " + amt + " a cuenta " + toAcct);
            } catch (Exception ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
        }
    }

    private void doViewTransactions() {
        try {
            List<Transaccion> txs;
            if (usuarioLogeado.getTipoUsuario() == TipoUsuario.EMPLEADO) {
                txs = service.getAllTransacciones();
            } else {
                txs = service.getTransaccionesUsuario(usuarioLogeado.getId());
            }

            if (txs.isEmpty()) {
                outputArea.setText("Sin transacciones");
                return;
            }

            StringBuilder sb = new StringBuilder();
            for (Transaccion t : txs) {
                sb.append(t.getFecha())
                        .append(" | ")
                        .append(t.getTipo())
                        .append(" | ")
                        .append(t.getCantidad());
                if (t.getTipo() == TipoTransaccion.TRANSFERENCIA && t.getIdCuentaDestinatario() != null) {
                    try {
                        Cuenta toAcct = service.getCuentaById(t.getIdCuentaDestinatario());
                        sb.append(" → ").append(toAcct.getNumero());
                    } catch (SQLException e) {
                        sb.append(" → [Cuenta #" + t.getIdCuentaDestinatario() + "]");
                    }
                }
                sb.append("\n");
            }
            outputArea.setText(sb.toString());

        } catch (SQLException e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private void doRefillCash() {
        String s = JOptionPane.showInputDialog(this, "Monto a reponer en el cajero:");
        if (s != null) {
            try {
                BigDecimal amt = new BigDecimal(s);
                service.agregarSaldo(amt);
                outputArea.setText("Cajero repuesto con: " + amt);
            } catch (Exception ex) {
                outputArea.setText("Error: " + ex.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ATMUI().setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                        null,
                        "No se pudo conectar con la base de datos:\n" + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
}
