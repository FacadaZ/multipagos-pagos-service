package com.multipagos.pagos.controller;

import com.multipagos.pagos.service.QrService;
import com.multipagos.pagos.service.PagosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/qr")
public class QrController {

    private final QrService qrService;
    private final PagosService pagosService;

    @Autowired
    public QrController(QrService qrService, PagosService pagosService) {
        this.qrService = qrService;
        this.pagosService = pagosService;
    }

    // Genera un QR a partir de customer_ref que seria el ci del cliente y amount:
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generarQr(@RequestBody Map<String, Object> request) throws Exception {
        String customerRef = (String) request.get("customer_ref");
        double amount = Double.parseDouble(request.get("amount").toString());

        String contenido = String.format("REF:%s|MTO:%.2f", customerRef, amount);

        String qrUrl = qrService.generarQr(contenido);

        return ResponseEntity.ok(Map.of(
                "qr_url", qrUrl,
                "status", "pendiente"));
    }

    @PostMapping("/scan")
    public ResponseEntity<Map<String, Object>> scanQr(@RequestBody Map<String, Object> request) {
        String debtId = (String) request.get("debt_id");
        try {
            var tx = pagosService.processPayment(debtId, null);
            return ResponseEntity.ok(Map.of(
                    "message", "Pago procesado desde lectura QR",
                    "transaction_id", tx.getId(),
                    "receipt_url", tx.getHashRecibo()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/generate_from_debt")
    public ResponseEntity<Map<String, Object>> generarQrDesdeDeuda(@RequestBody Map<String, Object> request) {
        String debtId = (String) request.get("debt_id");
        Double amount = Double.valueOf(request.get("amount").toString());
        String customerRef = (String) request.get("customer_ref");
        if (debtId == null || debtId.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "debt_id es requerido"));
        }
        try {
            String contenido = String.format("ID_DEUDA:%s|MTO:%.2f|REF:%s", debtId, amount, customerRef);
            String qrUrl = qrService.generarQr(contenido);
            return ResponseEntity.ok(Map.of(
                    "debt_id", debtId,
                    "amount", amount,
                    "qr_url", qrUrl,
                    "status", "PENDIENTE"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

}