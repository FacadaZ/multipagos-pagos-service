package com.multipagos.pagos.controller;

import com.multipagos.pagos.client.DebtDTO;
import com.multipagos.pagos.client.IDeudasClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deudas")
public class DeudasController {

    private final IDeudasClient deudasClient;

    @Autowired
    public DeudasController(IDeudasClient deudasClient) {
        this.deudasClient = deudasClient;
    }

    @GetMapping("/{debtId}")
    public ResponseEntity<DebtDTO> getById(@PathVariable String debtId) {
        DebtDTO debt = deudasClient.lookupDebtById(debtId);
        if (debt == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(debt);
    }

    @GetMapping("")
    public ResponseEntity<java.util.List<DebtDTO>> listAll() {
        try {
            java.util.List<DebtDTO> list = deudasClient.listDebts();
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
