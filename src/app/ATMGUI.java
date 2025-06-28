package app;

import model.Customer;
import service.ATMService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;

public class ATMGUI extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private ATMService service;
    private Customer currentCustomer;
    private JTextField userField = new JTextField(15);
    private JPasswordField passField = new JPasswordField(15);
    private JLabel loginErrorLabel = new JLabel();
    private JLabel welcomeLabel = new JLabel();
    private JTextArea outputArea = new JTextArea(8, 30);

    public ATMGUI() throws Exception {
        super("Cajero Automático");
        service = new ATMService();
        initUI();
    }

    private void initUI() {
        mainPanel.add(createLoginPanel(), "login");
        mainPanel.add(createDashboardPanel(), "dashboard");
        add(mainPanel);
        cardLayout.show(mainPanel, "login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JPanel createLoginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.gridx = 0;
        c.gridy = 0;
        p.add(new JLabel("Usuario:"), c);
        c.gridx = 1;
        p.add(userField, c);
        c.gridx = 0;
        c.gridy = 1;
        p.add(new JLabel("Clave:"), c);
        c.gridx = 1;
        p.add(passField, c);
        JButton btnLogin = new JButton("Ingresar");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        p.add(btnLogin, c);
        loginErrorLabel.setForeground(Color.RED);
        c.gridy = 3;
        p.add(loginErrorLabel, c);
        btnLogin.addActionListener(e -> handleLogin());
        return p;
    }

    private JPanel createDashboardPanel() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        JPanel top = new JPanel(new BorderLayout());
        top.add(welcomeLabel, BorderLayout.WEST);
        JButton btnLogout = new JButton("Logout");
        top.add(btnLogout, BorderLayout.EAST);
        p.add(top, BorderLayout.NORTH);
        outputArea.setEditable(false);
        p.add(new JScrollPane(outputArea), BorderLayout.CENTER);
        JPanel ops = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton b1 = new JButton("Ver Saldo"), b2 = new JButton("Depositar"), b3 = new JButton("Retirar"),
                b4 = new JButton("Transferir");
        ops.add(b1);
        ops.add(b2);
        ops.add(b3);
        ops.add(b4);
        p.add(ops, BorderLayout.SOUTH);
        btnLogout.addActionListener(e -> {
            userField.setText("");
            passField.setText("");
            loginErrorLabel.setText("");
            outputArea.setText("");
            cardLayout.show(mainPanel, "login");
        });
        b1.addActionListener(e -> doBalance());
        b2.addActionListener(e -> doDeposit());
        b3.addActionListener(e -> doWithdraw());
        b4.addActionListener(e -> doTransfer());
        return p;
    }

    private void handleLogin() {
        try {
            String u = userField.getText();
            String p = new String(passField.getPassword());
            currentCustomer = service.login(u, p);
            if (currentCustomer == null) {
                loginErrorLabel.setText("Credenciales inválidas");
            } else {
                welcomeLabel.setText("Bienvenido, " + currentCustomer.getFullName());
                loginErrorLabel.setText("");
                cardLayout.show(mainPanel, "dashboard");
            }
        } catch (Exception ex) {
            loginErrorLabel.setText("Error al conectar con DB");
            ex.printStackTrace();
        }
    }

    private void doBalance() {
        try {
            BigDecimal bal = service.getBalance(currentCustomer.getId());
            outputArea.setText("Saldo actual: " + bal);
        } catch (Exception e) {
            outputArea.setText("Error: " + e.getMessage());
        }
    }

    private void doDeposit() {
        String amt = JOptionPane.showInputDialog(this, "Monto a depositar:");
        if (amt != null)
            try {
                service.deposit(currentCustomer.getId(), new BigDecimal(amt));
                outputArea.setText("Depositado: " + amt);
            } catch (Exception e) {
                outputArea.setText("Error: " + e.getMessage());
            }
    }

    private void doWithdraw() {
        String amt = JOptionPane.showInputDialog(this, "Monto a retirar:");
        if (amt != null)
            try {
                service.withdraw(currentCustomer.getId(), new BigDecimal(amt));
                outputArea.setText("Retirado: " + amt);
            } catch (Exception e) {
                outputArea.setText("Error: " + e.getMessage());
            }
    }

    private void doTransfer() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField accField = new JTextField();
        JTextField amtField = new JTextField();
        panel.add(new JLabel("Cuenta destino ID:"));
        panel.add(accField);
        panel.add(new JLabel("Monto:"));
        panel.add(amtField);
        int ok = JOptionPane.showConfirmDialog(this, panel,
                "Transferencia", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            try {
                int toAcc = Integer.parseInt(accField.getText());
                BigDecimal amt = new BigDecimal(amtField.getText());
                service.transfer(currentCustomer.getId(), toAcc, amt);
                outputArea.setText("Transferido: " + amt + " a cuenta " + toAcc);
            } catch (Exception e) {
                outputArea.setText("Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ATMGUI().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
