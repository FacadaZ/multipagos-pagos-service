package com.multipagos.pagos.client;

// Data Transfer Object para representar una Deuda del servicio externo
public class DebtDTO {
    private String id; // ID de la deuda (clave para marcar como PAID)
    private Long tenantId; // ID de la Empresa proveedora (Multi-tenant)
    private String serviceId; // ID del Servicio
    private String customerRef; // CI/NIT o código de cliente
    private Double amount; // Monto a pagar
    private String status; // PENDING, PAID, CANCELLED
    private String period; // Período de la deuda (Ej: 2025-05)

    public DebtDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getCustomerRef() {
        return customerRef;
    }

    public void setCustomerRef(String customerRef) {
        this.customerRef = customerRef;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final DebtDTO dto = new DebtDTO();

        public Builder id(String id) {
            dto.setId(id);
            return this;
        }

        public Builder tenantId(Long tenantId) {
            dto.setTenantId(tenantId);
            return this;
        }

        public Builder serviceId(String serviceId) {
            dto.setServiceId(serviceId);
            return this;
        }

        public Builder customerRef(String customerRef) {
            dto.setCustomerRef(customerRef);
            return this;
        }

        public Builder amount(Double amount) {
            dto.setAmount(amount);
            return this;
        }

        public Builder status(String status) {
            dto.setStatus(status);
            return this;
        }

        public Builder period(String period) {
            dto.setPeriod(period);
            return this;
        }

        public DebtDTO build() {
            return dto;
        }
    }
}