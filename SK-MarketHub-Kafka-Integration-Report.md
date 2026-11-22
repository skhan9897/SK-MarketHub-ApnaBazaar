# SK-MarketHub Apache Kafka Integration Report

## 1. Event Processing Architecture

Apache Kafka is integrated into the Spring Boot backend via `spring-kafka` to enable event-driven decoupled notification processing, order events, and system audit logging.

---

## 2. Topic Architecture

| Topic Name | Producer Component | Consumer Component | Payload Description |
| :--- | :--- | :--- | :--- |
| `email-otp-requested` | `EmailNotificationProducer` | `EmailNotificationConsumer` | `{ email, otpCode, timestamp }` |
| `user-registered` | `EmailNotificationProducer` | `EmailNotificationConsumer` | `{ email, firstName, timestamp }` |
| `email-otp-verified` | `OtpController` | `AuditLogger` | `{ email, status, timestamp }` |
| `order-created` | `OrderService` | `NotificationConsumer` | `{ orderId, totalAmount, email }` |
| `payment-confirmed` | `PaymentService` | `OrderService` | `{ orderId, paymentId, status }` |
| `inventory-updated` | `InventoryService` | `SellerService` | `{ productId, newStock }` |
| `shipment-status-updated` | `ShipmentService` | `NotificationConsumer` | `{ orderId, trackingNumber, status }` |
| `notification-requested` | `NotificationProducer` | `NotificationConsumer` | `{ userId, message }` |

---

## 3. Producer & Consumer Resilience

- **Idempotency & Non-blocking Direct Fallback:** If Kafka cluster is temporarily unreachable, `EmailNotificationProducer` logs a warning and dispatches notifications via direct provider fallback to prevent UI registration blocks.
- **Consumer Group:** `skmarkethub-email-group`.
