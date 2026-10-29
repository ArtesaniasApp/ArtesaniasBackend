# Backend Ale y Leo SpA (Spring Boot 4.1, Java 21, MySQL, JWT)

Proyecto universitario (DSY1105, Duoc UC). Ver `README.md` para estructura, endpoints y reglas de negocio.
Contexto completo del proyecto (app Android, entregas, reparto): `..\ArtesanApp\ArtesaniasAndroid\CLAUDE.md` (proyecto Android compartido, package com.example.ArtesanApp).

- El estudiante debe poder explicar el código: mantenerlo simple, nombres en español, comentarios cortos.
- Capas: controller → service → repository; DTOs (records) en `dto`, nunca exponer entidades con contraseña.
- Spring Boot 4 = Spring Framework 7, Spring Security 7, Hibernate 7, Jackson 3.
- JWT con jjwt 0.12.x. Security sin AuthenticationManager: el login se valida a mano en `AuthService`.
- Tests: `mvnw test` usa H2 (`src/test/resources/application.properties`).
- El código se escribió sin poder compilarlo: si `mvnw compile` falla, corregir los errores manteniendo el diseño.
- Entrega Prueba 2: jueves 29-oct-2026 19:00, backend con ≥75% de avance (no se presenta, se revisa el código).
