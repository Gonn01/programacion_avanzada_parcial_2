package service;

import dao.*;
import dao.impl.*;
import model.*;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ATMService {
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO txDAO;
    private CashInventoryDAO cashDAO;

    public ATMService() throws SQLException {
        customerDAO = new CustomerDAOImpl();
        accountDAO = new AccountDAOImpl();
        txDAO = new TransactionDAOImpl();
        cashDAO = new CashInventoryDAOImpl();
    }

    public Customer login(String user, String pass) throws SQLException {
        return customerDAO.findByUsernameAndPassword(user, pass);
    }

    public BigDecimal getBalance(int customerId) throws SQLException {
        Account acc = accountDAO.findByCustomerId(customerId);
        return acc.getBalance();
    }

    public void deposit(int customerId, BigDecimal amount) throws SQLException {
        Account acc = accountDAO.findByCustomerId(customerId);
        acc.setBalance(acc.getBalance().add(amount));
        accountDAO.update(acc);
        txDAO.add(new Transaction(0, acc.getId(), TransactionType.DEPOSIT, amount, null, null));
        CashInventory inv = cashDAO.getInventory();
        inv.setTotalCash(inv.getTotalCash().add(amount));
        cashDAO.update(inv);
    }

    public void withdraw(int customerId, BigDecimal amount) throws SQLException {
        Account acc = accountDAO.findByCustomerId(customerId);
        if (acc.getBalance().compareTo(amount) < 0)
            throw new IllegalArgumentException("Saldo insuficiente");
        CashInventory inv = cashDAO.getInventory();
        if (inv.getTotalCash().compareTo(amount) < 0)
            throw new IllegalArgumentException("Cajero sin efectivo suficiente");
        acc.setBalance(acc.getBalance().subtract(amount));
        accountDAO.update(acc);
        txDAO.add(new Transaction(0, acc.getId(), TransactionType.WITHDRAWAL, amount, null, null));
        inv.setTotalCash(inv.getTotalCash().subtract(amount));
        cashDAO.update(inv);
    }

    public void transfer(int fromCustomerId, int toAccountId, BigDecimal amount) throws SQLException {
        Account fromAcc = accountDAO.findByCustomerId(fromCustomerId);
        Account toAcc = accountDAO.findById(toAccountId);
        if (fromAcc.getBalance().compareTo(amount) < 0)
            throw new IllegalArgumentException("Saldo insuficiente");
        fromAcc.setBalance(fromAcc.getBalance().subtract(amount));
        toAcc.setBalance(toAcc.getBalance().add(amount));
        accountDAO.update(fromAcc);
        accountDAO.update(toAcc);
        txDAO.add(new Transaction(0, fromAcc.getId(), TransactionType.TRANSFER, amount, null, toAcc.getId()));
    }

    public List<Transaction> viewAllTransactions() throws SQLException {
        return txDAO.findAll();
    }

    public void refillCash(BigDecimal amount) throws SQLException {
        CashInventory inv = cashDAO.getInventory();
        inv.setTotalCash(inv.getTotalCash().add(amount));
        cashDAO.update(inv);
    }

    public Map<TransactionType, Long> dailyStats() throws SQLException {
        List<Transaction> all = txDAO.findAll();
        return all.stream()
                .collect(Collectors.groupingBy(Transaction::getType, Collectors.counting()));
    }
}