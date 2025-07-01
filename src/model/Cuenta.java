package model;

import java.math.BigDecimal;

public class Cuenta {
    private int id;
    private int idUsuario;
    private String numero;
    private BigDecimal saldo;

    public Cuenta(int id, int idUsuario, String numero, BigDecimal saldo) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.numero = numero;
        this.saldo = saldo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getidUsuario() {
        return idUsuario;
    }

    public void setidUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal balance) {
        this.saldo = balance;
    }
}
