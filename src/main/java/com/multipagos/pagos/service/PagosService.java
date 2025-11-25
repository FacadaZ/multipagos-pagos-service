package com.multipagos.pagos.service;

import com.multipagos.pagos.model.Transaccion;
import com.multipagos.pagos.client.IDeudasClient;
import com.multipagos.pagos.repository.TransaccionRepository;
import com.multipagos.pagos.utils.GenerarPDF;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import com.multipagos.pagos.client.DebtDTO;
import com.multipagos.pagos.dto.KpiReportDTO;
import java.time.LocalDateTime;
import java.util.Map;

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

    public KpiReportDTO getKpiReport(Long companyId, String serviceId, String dateFrom, String dateTo, String groupBy,
            String granularity) {
        // Filtros básicos
        List<Transaccion> transacciones = transaccionRepository.findAll();
        // Filtrar por company_id
        if (companyId != null) {
            transacciones = transacciones.stream()
                    .filter(t -> t.getIdArrendatario() != null && t.getIdArrendatario().equals(companyId)).toList();
        }
        // Filtrar por service_id
        if (serviceId != null) {
            transacciones = transacciones.stream()
                    .filter(t -> t.getIdServicio() != null && t.getIdServicio().equals(serviceId)).toList();
        }
        // Filtrar por fechas
        if (dateFrom != null) {
            LocalDateTime from = LocalDateTime.parse(dateFrom);
            transacciones = transacciones.stream()
                    .filter(t -> t.getFechaCreacion() != null && !t.getFechaCreacion().isBefore(from)).toList();
        }
        if (dateTo != null) {
            LocalDateTime to = LocalDateTime.parse(dateTo);
            transacciones = transacciones.stream()
                    .filter(t -> t.getFechaCreacion() != null && !t.getFechaCreacion().isAfter(to)).toList();
        }

        long totalPagos = transacciones.size();
        long pagosExitosos = transacciones.stream()
                .filter(t -> "PAID".equalsIgnoreCase(t.getEstado()) || "APROBADO".equalsIgnoreCase(t.getEstado()))
                .count();
        double tasaAprobacion = totalPagos > 0 ? (double) pagosExitosos / totalPagos : 0.0;
        double montoTotalPagado = transacciones.stream()
                .filter(t -> "PAID".equalsIgnoreCase(t.getEstado()) || "APROBADO".equalsIgnoreCase(t.getEstado()))
                .mapToDouble(t -> t.getMonto() != null ? t.getMonto() : 0.0).sum();

        Map<String, Long> conteoPorEstado = transacciones.stream().collect(
                java.util.stream.Collectors.groupingBy(
                        t -> t.getEstado() != null ? t.getEstado() : "UNKNOWN",
                        java.util.stream.Collectors.counting()));

        KpiReportDTO dto = new KpiReportDTO();
        dto.setTotalPagos(totalPagos);
        dto.setPagosExitosos(pagosExitosos);
        dto.setTasaAprobacion(tasaAprobacion);
        dto.setMontoTotalPagado(montoTotalPagado);
        dto.setConteoPorEstado(conteoPorEstado);
        return dto;
    }
}