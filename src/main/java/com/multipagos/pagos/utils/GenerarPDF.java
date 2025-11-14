package com.multipagos.pagos.utils;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.multipagos.pagos.model.Transaccion;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.*;

@Service
public class GenerarPDF {

    public String generarComprobante(Transaccion tx) {
        try {
            Path carpeta = Paths.get(System.getProperty("java.io.tmpdir"), "receipts");
            Files.createDirectories(carpeta);

            String nombreArchivo = "comprobante_" + tx.getId() + ".pdf";
            Path rutaArchivo = carpeta.resolve(nombreArchivo);

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(rutaArchivo.toFile()));
            document.open();

            document.add(new Paragraph("COMPROBANTE DE PAGO"));
            document.add(new Paragraph("-----------------------------"));
            document.add(new Paragraph("Transacción ID: " + tx.getId()));
            if (tx.getDebtId() != null) {
                document.add(new Paragraph("Debt ID: " + tx.getDebtId()));
            }
            if (tx.getIdArrendatario() != null) {
                document.add(new Paragraph("Tenant ID: " + tx.getIdArrendatario()));
            }
            if (tx.getIdServicio() != null) {
                document.add(new Paragraph("Service ID: " + tx.getIdServicio()));
            }
            document.add(new Paragraph("Cliente: " + tx.getReferenciaCliente()));
            document.add(new Paragraph("Monto: Bs " + tx.getMonto()));
            document.add(new Paragraph("Estado: " + tx.getEstado()));
            document.add(new Paragraph("Fecha: " + tx.getFechaCreacion()));
            document.add(new Paragraph("-----------------------------"));
            document.add(new Paragraph("¡Gracias por su pago!"));

            document.close();

            return "/pagos/receipts/" + nombreArchivo;
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage());
        }
    }
}
