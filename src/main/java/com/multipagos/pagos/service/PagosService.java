package com.multipagos.pagos.service;

import com.multipagos.pagos.model.Transaccion;
import com.multipagos.pagos.client.IDeudasClient;
import com.multipagos.pagos.repository.TransaccionRepository;
import com.multipagos.pagos.utils.GenerarPDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import com.multipagos.pagos.client.DebtDTO;

@Service
public class PagosService {

    @Autowired
    private TransaccionRepository transaccionRepository;

    @Autowired
    private IDeudasClient deudasClient;

    @Autowired
    private GenerarPDF generarPDF;

    public Transaccion processPayment(String debtId, Double amount) {

        String status = "APROBADO";

        Transaccion tx;
        try {
            com.multipagos.pagos.client.DebtDTO debt = deudasClient.lookupDebtById(debtId);
            if (debt != null) {
                tx = Transaccion.builder()
                        .idArrendatario(debt.getTenantId())
                        .idServicio(debt.getServiceId())
                        .referenciaCliente(debt.getCustomerRef())
                        .monto(amount != null ? amount : debt.getAmount())
                        .debtId(debt.getId())
                        .estado(status)
                        .build();

                // marcar como PAID en el servicio externo (Gorena espera 'PAID')
                deudasClient.updateDebtStatus(debt.getId(), "PAID");
            } else {
                deudasClient.updateDebtStatus(debtId, "PAGO");
                tx = Transaccion.builder()
                        .referenciaCliente("N/A - sin lookup")
                        .monto(amount)
                        .estado(status)
                        .debtId(debtId)
                        .build();
            }
        } catch (Exception e) {
            tx = Transaccion.builder()
                    .referenciaCliente("N/A - error lookup")
                    .monto(amount)
                    .estado(status)
                    .debtId(debtId)
                    .build();
        }

        Transaccion savedTx = transaccionRepository.save(tx);

        String receiptUrl = generarPDF.generarComprobante(savedTx);

        savedTx.setHashRecibo(receiptUrl);
        return transaccionRepository.save(savedTx);
    }

    public List<Transaccion> findAllTransacciones() {
        return transaccionRepository.findAll();
    }

    public DebtDTO lookupDebtByService(String customerRef, String serviceId, String tenantId) {
        return deudasClient.lookupDebtByService(customerRef, serviceId, tenantId);
    }
}