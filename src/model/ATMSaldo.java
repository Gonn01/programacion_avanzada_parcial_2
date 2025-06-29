package model;

import java.math.BigDecimal;

public class ATMSaldo {
    private int id;
    private BigDecimal saldo;

    public ATMSaldo(int id, BigDecimal totalCash) {
        this.id = id;
        this.saldo = totalCash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal totalCash) {
        this.saldo = totalCash;
    }
}
