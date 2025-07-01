package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaccion {
    private int id;
    private int numeroCuenta;
    private TipoTransaccion tipo;
    private BigDecimal cantidad;
    private LocalDateTime fecha;
    private Integer idCuentaDestinatario;

    public Transaccion(
            int id,
            int numeroCuenta,
            TipoTransaccion tipo,
            BigDecimal cantidad,
            LocalDateTime fecha,
            Integer idCuentaDestinatario) {
        this.id = id;
        this.numeroCuenta = numeroCuenta;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.idCuentaDestinatario = idCuentaDestinatario;
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

    public void setNumeroCuenta(int numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public TipoTransaccion getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransaccion tipo) {
        this.tipo = tipo;
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

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Integer getIdCuentaDestinatario() {
        return idCuentaDestinatario;
    }

    public void setIdCuentaDestinatario(Integer idCuentaDestinatario) {
        this.idCuentaDestinatario = idCuentaDestinatario;
    }
}
