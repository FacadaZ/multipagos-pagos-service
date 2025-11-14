package com.multipagos.pagos.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "deudas.client.enabled", havingValue = "false", matchIfMissing = true)
public class DeudasClientStub implements IDeudasClient {

    private static final String MOCK_CUSTOMER = "1234567";
    private static final Long MOCK_TENANT_ID = 1L;
    private static final Long MOCK_SERVICE_ID = 10L;
    private final java.util.Map<String, DebtDTO> store = new java.util.concurrent.ConcurrentHashMap<>();

    public DeudasClientStub() {
        DebtDTO d1 = DebtDTO.builder()
                .id("debt-001")
                .amount(150.0)
                .customerRef("555444")
                .serviceId("srv_luz")
                .period("2023-10")
                .status("PENDIENTE")
                .build();
        store.put(d1.getId(), d1);

        DebtDTO d2 = DebtDTO.builder()
                .id("debt-002")
                .amount(75.0)
                .customerRef("777888")
                .serviceId("srv_agua")
                .period("2023-09")
                .status("PENDIENTE")
                .build();
        store.put(d2.getId(), d2);
    }

    @Override
    public DebtDTO lookupDebt(String customerRef, Long tenantId, String serviceId) {
        // Buscar en el store por coincidencia de tenant/service/customer
        return store.values().stream()
                .filter(d -> d.getCustomerRef().equals(customerRef)
                        && java.util.Objects.equals(d.getTenantId(), tenantId)
                        && java.util.Objects.equals(d.getServiceId(), serviceId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public DebtDTO lookupDebtById(String debtId) {
        return store.get(debtId);
    }

    @Override
    public void updateDebtStatus(String debtId, String status) {
        System.out.println("STUB: Simulación EXITOSA de actualización de deuda " + debtId + " a estado: " + status);
        DebtDTO d = store.get(debtId);
        if (d != null) {
            d.setStatus(status);
            store.put(debtId, d);
        }
    }

    @Override
    public java.util.List<DebtDTO> listDebts() {
        return new java.util.ArrayList<>(store.values());
    }

    @Override
    public DebtDTO lookupDebtByService(String customerRef, String serviceId, String tenantId) {
        // Match by customerRef and optionally by serviceId/tenantId (string form)
        return store.values().stream()
                .filter(d -> d.getCustomerRef().equals(customerRef)
                        && (serviceId == null || serviceId.isEmpty() || d.getServiceId().equals(serviceId))
                        && (tenantId == null || tenantId.isEmpty() || tenantId.equals(String.valueOf(d.getTenantId()))))
                .findFirst()
                .orElse(null);
    }
}