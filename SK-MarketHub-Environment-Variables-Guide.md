# SK-MarketHub Production Environment Variables Guide

## 1. Required Render Environment Variables

| Variable Name | Sample / Recommended Value | Description |
| :--- | :--- | :--- |
| `SPRING_PROFILES_ACTIVE` | `prod` | Activates `application-prod.properties` |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://host:3306/sk_markethub_db?...` | Production MySQL JDBC URL |
| `SPRING_DATASOURCE_USERNAME` | `skmarket` | MySQL Database Username |
| `SPRING_DATASOURCE_PASSWORD` | `skmarket123_prod` | MySQL Database Password |
| `JWT_SECRET` | `9a8b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f...` | 32-byte JWT Signing Key |
| `KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` / `upstash-kafka:9092` | Kafka Broker Host & Port |
| `MAIL_HOST` | `smtp.gmail.com` / `smtp.brevo.com` | SMTP Email Server Host |
| `MAIL_PORT` | `587` | SMTP Server Port |
| `MAIL_USERNAME` | `notifications@skmarkethub.com` | SMTP Username |
| `MAIL_PASSWORD` | `your_smtp_app_password` | SMTP Password / API Key |
| `VITE_API_URL` | `https://sk-markethub-backend.onrender.com/api` | Web Frontend API URL |
