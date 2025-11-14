package com.multipagos.pagos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Transaccion")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_arrendatario")
    private Long idArrendatario;

    @Column(name = "id_servicio")
    private String idServicio;

    @Column(name = "referencia_cliente")
    private String referenciaCliente;

    @Column(name = "monto")
    private Double monto;

    @Column(name = "estado")
    private String estado;

    @Column(name = "hash_recibo")
    private String hashRecibo;

    @Column(name = "debt_id")
    private String debtId;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    public Transaccion() {
    }

    // Getters and setters (implement only what is needed)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdArrendatario() {
        return idArrendatario;
    }

    public void setIdArrendatario(Long idArrendatario) {
        this.idArrendatario = idArrendatario;
    }

    public String getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(String idServicio) {
        this.idServicio = idServicio;
    }

    public String getReferenciaCliente() {
        return referenciaCliente;
    }

    public void setReferenciaCliente(String referenciaCliente) {
        this.referenciaCliente = referenciaCliente;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getHashRecibo() {
        return hashRecibo;
    }

    public void setHashRecibo(String hashRecibo) {
        this.hashRecibo = hashRecibo;
    }

    public String getDebtId() {
        return debtId;
    }

    public void setDebtId(String debtId) {
        this.debtId = debtId;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    // Simple builder implementation used by the service
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long idArrendatario;
        private String idServicio;
        private String referenciaCliente;
        private Double monto;
        private String estado;
        private String debtId;

        public Builder idArrendatario(Long idArrendatario) {
            this.idArrendatario = idArrendatario;
            return this;
        }

        public Builder idServicio(String idServicio) {
            this.idServicio = idServicio;
            return this;
        }

        public Builder referenciaCliente(String referenciaCliente) {
            this.referenciaCliente = referenciaCliente;
            return this;
        }

        public Builder monto(Double monto) {
            this.monto = monto;
            return this;
        }

        public Builder estado(String estado) {
            this.estado = estado;
            return this;
        }

        public Builder debtId(String debtId) {
            this.debtId = debtId;
            return this;
        }

        public Transaccion build() {
            Transaccion t = new Transaccion();
            t.setIdArrendatario(this.idArrendatario);
            t.setIdServicio(this.idServicio);
            t.setReferenciaCliente(this.referenciaCliente);
            t.setMonto(this.monto);
            t.setEstado(this.estado);
            t.setDebtId(this.debtId);
            t.setFechaCreacion(LocalDateTime.now());
            return t;
        }
    }
}
