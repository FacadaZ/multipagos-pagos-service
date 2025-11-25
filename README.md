## Endpoints del microservicio de pagos

- `POST /pagos/confirm`
  Confirma un pago.
  **Body:**
  ```json
  {
    "debt_id": "22",
    "amount": 100.0
  }
  ```

- `POST /pagos/lookup`
  Busca una deuda por identificador.
  **Body:**
  ```json
  {
    "service_id": "srv_luz",
    "customer_ref": "1010",
    "tenant_id": "1"
  }
  ```

- `GET /pagos/receipts/{filename}`
  Descarga el recibo PDF por nombre de archivo.

- `GET /pagos/report/kpis`
  Devuelve los KPIs en formato JSON.
