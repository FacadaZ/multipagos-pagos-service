package com.multipagos.pagos.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(name = "deudas.client.enabled", havingValue = "true")
public class RestDeudasClient implements IDeudasClient {

    private final RestTemplate rest;
    private final String baseUrl;

    public RestDeudasClient(RestTemplateBuilder builder,
            @Value("${deudas.client.base-url:http://127.0.0.1:8000/api}") String baseUrl) {
        this.rest = builder.build();
        this.baseUrl = baseUrl;
    }

    @Override
    public DebtDTO lookupDebt(String customerRef, Long tenantId, String serviceId) {
        String url = baseUrl + "/debts/lookup";
        String body = "{\"service_id\":\"" + (serviceId != null ? serviceId : "")
                + "\",\"customer_ref\":\"" + customerRef + "\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<DebtDTO> resp = rest.exchange(url, HttpMethod.POST, entity, DebtDTO.class);
            if (resp.getStatusCode().is2xxSuccessful())
                return resp.getBody();
        } catch (HttpClientErrorException.NotFound nfe) {
            return null;
        } catch (Exception e) {
            System.out.println("RestDeudasClient.lookupDebt error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public DebtDTO lookupDebtByService(String customerRef, String serviceId, String tenantId) {
        String url = baseUrl + "/debts/lookup";
        String body = "{\"service_id\":\"" + (serviceId != null ? serviceId : "") + "\",\"customer_ref\":\""
                + (customerRef != null ? customerRef : "") + "\"}";
        if (tenantId != null && !tenantId.isEmpty()) {
            body = body.substring(0, body.length() - 1) + ",\"tenant_id\":\"" + tenantId + "\"}";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        try {
            System.out.println("[lookupDebtByService] Enviando a Django: " + body);
            ResponseEntity<GorenaDebt> resp = rest.exchange(url, HttpMethod.POST, entity, GorenaDebt.class);
            System.out.println("[lookupDebtByService] Status: " + resp.getStatusCode());
            System.out.println("[lookupDebtByService] Body: " + resp.getBody());
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                DebtDTO dto = mapGorena(resp.getBody());
                System.out.println("[lookupDebtByService] Mapeado: " + dto);
                return dto;
            }
        } catch (HttpClientErrorException.NotFound nfe) {
            System.out.println("[lookupDebtByService] NotFound: " + nfe.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("RestDeudasClient.lookupDebtByService error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public DebtDTO lookupDebtById(String debtId) {
        String url = baseUrl + "/debts/" + debtId + "/";
        try {
            ResponseEntity<GorenaDebt> resp = rest.getForEntity(url, GorenaDebt.class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                return mapGorena(resp.getBody());
            }
        } catch (HttpClientErrorException.NotFound nfe) {
            return null;
        } catch (Exception e) {
            System.out.println("RestDeudasClient.lookupDebtById error: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void updateDebtStatus(String debtId, String status) {
        String urlWithSlash = baseUrl + "/debts/" + debtId + "/";
        String urlNoSlash = baseUrl + "/debts/" + debtId;
        Map<String, Object> body = Map.of("status", status);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            try {
                ResponseEntity<Void> r1 = rest.exchange(urlWithSlash, HttpMethod.PATCH, entity, Void.class);
                System.out
                        .println("RestDeudasClient.updateDebtStatus PATCH with slash response: " + r1.getStatusCode());
                return;
            } catch (HttpClientErrorException e) {
                System.out.println("RestDeudasClient.updateDebtStatus PATCH with slash failed: " + e.getStatusCode()
                        + " " + e.getResponseBodyAsString());
            }

            try {
                ResponseEntity<Void> r2 = rest.exchange(urlNoSlash, HttpMethod.PATCH, entity, Void.class);
                System.out.println("RestDeudasClient.updateDebtStatus PATCH no-slash response: " + r2.getStatusCode());
                return;
            } catch (HttpClientErrorException e) {
                System.out.println("RestDeudasClient.updateDebtStatus PATCH no-slash failed: " + e.getStatusCode() + " "
                        + e.getResponseBodyAsString());
            }

            try {
                ResponseEntity<Void> r3 = rest.exchange(urlNoSlash, HttpMethod.PUT, entity, Void.class);
                System.out.println("RestDeudasClient.updateDebtStatus PUT no-slash response: " + r3.getStatusCode());
                return;
            } catch (HttpClientErrorException e) {
                System.out.println("RestDeudasClient.updateDebtStatus PUT no-slash failed: " + e.getStatusCode() + " "
                        + e.getResponseBodyAsString());
            }

            try {
                ResponseEntity<Void> r4 = rest.exchange(urlWithSlash, HttpMethod.PUT, entity, Void.class);
                System.out.println("RestDeudasClient.updateDebtStatus PUT with-slash response: " + r4.getStatusCode());
                return;
            } catch (HttpClientErrorException e) {
                System.out.println("RestDeudasClient.updateDebtStatus PUT with-slash failed: " + e.getStatusCode() + " "
                        + e.getResponseBodyAsString());
            }

            System.out.println("RestDeudasClient.updateDebtStatus: all attempts failed for debtId=" + debtId);
        } catch (Exception e) {
            System.out.println("RestDeudasClient.updateDebtStatus error: " + e.getMessage());
        }
    }

    @Override
    public List<DebtDTO> listDebts() {
        String url = baseUrl + "/debts/";
        try {
            ResponseEntity<GorenaDebt[]> resp = rest.getForEntity(url, GorenaDebt[].class);
            if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                return Arrays.stream(resp.getBody())
                        .map(this::mapGorena)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.out.println("RestDeudasClient.listDebts error: " + e.getMessage());
        }
        return java.util.Collections.emptyList();
    }

    private DebtDTO mapGorena(GorenaDebt g) {
        DebtDTO d = new DebtDTO();
        if (g == null)
            return d;
        d.setId(g.getId() != null ? String.valueOf(g.getId()) : null);
        try {
            if (g.getTenantId() != null && !g.getTenantId().isEmpty()) {
                String t = g.getTenantId();
                if (t.matches("\\d+")) {
                    d.setTenantId(Long.valueOf(t));
                } else {
                    d.setTenantId(null);
                }
            }
        } catch (Exception ex) {
            d.setTenantId(null);
        }
        d.setServiceId(g.getServiceId());
        d.setCustomerRef(g.getCustomerRef());
        try {
            if (g.getAmount() != null && !g.getAmount().isEmpty())
                d.setAmount(Double.valueOf(g.getAmount()));
        } catch (Exception ex) {
            d.setAmount(null);
        }
        d.setStatus(g.getStatus());
        d.setPeriod(g.getPeriod());
        return d;
    }

}
