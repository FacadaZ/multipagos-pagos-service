package com.multipagos.pagos.dto;

import java.util.Map;

public class KpiReportDTO {
    private long totalPagos;
    private long pagosExitosos;
    private double tasaAprobacion;
    private double montoTotalPagado;
    private Map<String, Long> conteoPorEstado;

    public long getTotalPagos() {
        return totalPagos;
    }

    public void setTotalPagos(long totalPagos) {
        this.totalPagos = totalPagos;
    }

    public long getPagosExitosos() {
        return pagosExitosos;
    }

    public void setPagosExitosos(long pagosExitosos) {
        this.pagosExitosos = pagosExitosos;
    }

    public double getTasaAprobacion() {
        return tasaAprobacion;
    }

    public void setTasaAprobacion(double tasaAprobacion) {
        this.tasaAprobacion = tasaAprobacion;
    }

    public double getMontoTotalPagado() {
        return montoTotalPagado;
    }

    public void setMontoTotalPagado(double montoTotalPagado) {
        this.montoTotalPagado = montoTotalPagado;
    }

    public Map<String, Long> getConteoPorEstado() {
        return conteoPorEstado;
    }

    public void setConteoPorEstado(Map<String, Long> conteoPorEstado) {
        this.conteoPorEstado = conteoPorEstado;
    }
}
