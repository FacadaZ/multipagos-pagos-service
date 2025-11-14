package com.multipagos.pagos.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.multipagos.pagos.client.DebtDTO;
import com.multipagos.pagos.client.IDeudasClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class QrService {

    @Autowired
    private IDeudasClient deudasClient;

    public Map<String, Object> lookupAndGenerateQr(String customerRef, Long tenantId, String serviceId)
            throws Exception {
        DebtDTO debt = deudasClient.lookupDebt(customerRef, tenantId, serviceId);

        if (debt == null) {
            throw new RuntimeException("Deuda no encontrada o ya pagada para la referencia: " + customerRef);
        }

        String contenido = String.format("ID_DEUDA:%s|MTO:%.2f|REF:%s",
                debt.getId(), debt.getAmount(), debt.getCustomerRef());

        String qrUrl = generarQr(contenido);

        return Map.of(
                "debt_id", debt.getId(),
                "amount", debt.getAmount(),
                "qr_url", qrUrl,
                "status", "PENDIENTE");
    }

    public String generarQr(String contenido) throws Exception {

        String nombreArchivo = "qr_" + System.currentTimeMillis() + ".png";

        Path carpeta = Paths.get(System.getProperty("java.io.tmpdir"), "qrs");
        Files.createDirectories(carpeta);
        Path rutaArchivo = carpeta.resolve(nombreArchivo);

        BitMatrix matrix = new MultiFormatWriter()
                .encode(contenido, BarcodeFormat.QR_CODE, 250, 250);

        MatrixToImageWriter.writeToPath(matrix, "PNG", rutaArchivo);

        return "/qrs/" + nombreArchivo; // But this won't work if not served
    }

    public java.util.Map<String, Object> generarQrFromDebtId(String debtId) throws Exception {
        DebtDTO debt = deudasClient.lookupDebtById(debtId);
        if (debt == null) {
            throw new RuntimeException("Deuda no encontrada para id: " + debtId);
        }

        String contenido = String.format("ID_DEUDA:%s|MTO:%.2f|REF:%s",
                debt.getId(), debt.getAmount(), debt.getCustomerRef());

        String qrUrl = generarQr(contenido);

        return java.util.Map.of(
                "debt_id", debt.getId(),
                "amount", debt.getAmount(),
                "qr_url", qrUrl,
                "status", debt.getStatus() != null ? debt.getStatus() : "PENDIENTE");
    }
}