package com.multipagos.pagos.client;

public interface IDeudasClient {
    DebtDTO lookupDebt(String customerRef, Long tenantId, String serviceId);

    DebtDTO lookupDebtById(String debtId);

    void updateDebtStatus(String debtId, String status);

    // List all debts (used to fetch array responses like Gorena's /debts/)
    java.util.List<DebtDTO> listDebts();

    // Lookup where service identifier is a string (e.g. "srv_agua") and tenant
    // may also be provided as string. This maps to Gorena's /debts/lookup contract.
    DebtDTO lookupDebtByService(String customerRef, String serviceId, String tenantId);
}