package com.multipagos.pagos.client;

// DTO para la petición de lookup de deuda
public class LookupRequestDTO {
    private String customer_ref;
    private String service_id;
    private String tenant_id;

    public LookupRequestDTO() {
    }

    public String getCustomer_ref() {
        return customer_ref;
    }

    public void setCustomer_ref(String customer_ref) {
        this.customer_ref = customer_ref;
    }

    public String getService_id() {
        return service_id;
    }

    public void setService_id(String service_id) {
        this.service_id = service_id;
    }

    public String getTenant_id() {
        return tenant_id;
    }

    public void setTenant_id(String tenant_id) {
        this.tenant_id = tenant_id;
    }
}