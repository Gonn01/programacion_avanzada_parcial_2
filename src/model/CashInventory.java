package model;

import java.math.BigDecimal;

public class CashInventory {
    private int id;
    private BigDecimal totalCash;

    public CashInventory() {
    }

    public CashInventory(int id, BigDecimal totalCash) {
        this.id = id;
        this.totalCash = totalCash;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getTotalCash() {
        return totalCash;
    }

    public void setTotalCash(BigDecimal totalCash) {
        this.totalCash = totalCash;
    }
}
