package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaccion {
    private int id;
    private int numeroCuenta;
    private TipoTransaccion tipo;
    private BigDecimal cantidad;
    private LocalDateTime fecha;
    private Integer cuentaDestinatario;

    public Transaccion(
            int id,
            int numeroCuenta,
            TipoTransaccion tipo,
            BigDecimal cantidad,
            LocalDateTime fecha,
            Integer cuentaDestinatario) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.cuentaDestinatario = cuentaDestinatario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(int accountId) {
        this.numeroCuenta = accountId;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransaccion type) {
        this.tipo = type;
    }

    public BigDecimal getCantidad() {
        return cantidad;
    }

    public void setCantidad(BigDecimal amount) {
        this.cantidad = amount;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime timestamp) {
        this.fecha = timestamp;
    }

    public Integer getCuentaDestinatario() {
        return cuentaDestinatario;
    }

    public void setCuentaDestinatario(Integer targetAccountId) {
        this.cuentaDestinatario = targetAccountId;
    }
}
