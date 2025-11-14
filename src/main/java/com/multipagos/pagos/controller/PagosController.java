package com.multipagos.pagos.controller;

import com.multipagos.pagos.model.Transaccion;
import com.multipagos.pagos.service.PagosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import com.multipagos.pagos.client.LookupRequestDTO;
import com.multipagos.pagos.client.DebtDTO;

@RestController
@RequestMapping("/pagos")
public class PagosController {

    @Autowired
    private PagosService pagosService;

    @GetMapping
    public String indexPagos() {
        return "¡Servicio de Pagos Activo! Ahora puedes acceder a /pagos/transacciones.";
    }

    @GetMapping("/transacciones")
    public ResponseEntity<List<Transaccion>> getAllTransacciones() {
        List<Transaccion> transacciones = pagosService.findAllTransacciones();
        return ResponseEntity.ok(transacciones);
    }

    @PostMapping("/confirm")
    public ResponseEntity<Map<String, String>> confirmPayment(@RequestBody Map<String, Object> request) {
        String debtId = (String) request.get("debt_id");
        Double amount = Double.parseDouble(request.get("amount").toString());

        Transaccion tx = pagosService.processPayment(debtId, amount);

        return ResponseEntity.ok(Map.of(
                "message", "Pago Aprobado",
                "transaction_id", tx.getId().toString(),
                "receipt_url", tx.getHashRecibo()));
    }

    @PostMapping("/lookup")
    public ResponseEntity<DebtDTO> lookupDebt(@RequestBody LookupRequestDTO request) {
        String serviceId = request.getService_id();
        String customerRef = request.getCustomer_ref();
        String tenantId = request.getTenant_id();

        if (customerRef == null || customerRef.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        DebtDTO debt = pagosService.lookupDebtByService(customerRef, serviceId, tenantId);
        if (debt == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(debt);
    }

    @GetMapping("/receipts/{filename:.+}")
    public ResponseEntity<Resource> getReceipt(@PathVariable String filename) {
        try {
            java.nio.file.Path file = java.nio.file.Paths.get(System.getProperty("java.io.tmpdir"), "receipts",
                    filename);
            if (java.nio.file.Files.exists(file)) {
                Resource resource = new FileSystemResource(file);
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}