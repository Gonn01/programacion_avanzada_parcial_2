package model;

import java.math.BigDecimal;

public class Cuenta {
    private int id;
    private int usuarioId;
    private String numero;
    private BigDecimal saldo;

    public Cuenta(int id, int usuarioId, String numero, BigDecimal saldo) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.numero = numero;
        this.saldo = saldo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int userId) {
        this.usuarioId = userId;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String accountNumber) {
        this.numero = accountNumber;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal balance) {
        this.saldo = balance;
    }
}
