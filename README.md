# Billing Service

A healthcare clinic backend API built with **Kotlin + Micronaut** that handles patient registration, doctor onboarding, appointment booking, and consultation bill calculation with GST, insurance, and loyalty discounts.

## Table of Contents
- [Flow](#flow)

## Flow

The end-to-end journey from onboarding to billing:
```
1. Register Doctor        POST /doctors
         ↓
2. Register Patient       POST /patients
         ↓
3. Book Appointment       POST /appointments
         ↓
4. Complete Appointment   PATCH /appointments/{id}/status
         ↓
5. Get Bill               GET  /appointments/{id}/bill
```
Checkout Flow.md for more details

### Billing Rules

```
1. Base fee       = fee.lookup(specialty, yearsOfExperience)

2. Discount       = min(priorCompletedAppointments, 10) %
                    e.g. 3 prior visits → 3% discount, capped at 10%

3. GST            = 12% applied on amount after discount

4. Insurance      = 90% of total (after GST)

5. Patient co-pay = 10% of total (after GST)
```

## How to Run

### Prerequisites

- Java 21 (recommended: Liberica or Temurin via SDKMAN)
- Maven 3.8+

### Install Java via SDKMAN (if needed)

```bash
sdk install java 21.0.10-tem
sdk use java 21.0.10-tem
```

### Start the application

```bash
./mvnw mn:run
```

The server starts on `http://localhost:8080`.

### Verify it's running

```bash
curl http://localhost:8080/patients
# → []
```

---

## How to Test

### Run all tests

```bash
./mvnw clean test
```

### Run a specific test class

```bash
./mvnw test -Dtest=BillingServiceTest
```

## Assumptions

- **No database** — all data is stored in-memory using `ConcurrentHashMap`. Data is lost on restart.
- **No authentication** — all endpoints are publicly accessible. In production, role-based access control would be required.
- **No pagination** — `GET /patients`, `GET /doctors`, `GET /appointments` return all records.
- **NPI uniqueness** — each doctor must have a unique NPI number. Duplicate registration returns `409 Conflict`.
- **Date format** — all dates (DOB, practice start date) use `dd/MM/yyyy`.
- **Age field** — age is accepted as part of the patient request and stored as-is. It is not derived from DOB.
- **Bill on demand** — the bill is calculated fresh every time `GET /appointments/{id}/bill` is called. It is not stored.
- **Discount counts prior visits only** — the current appointment is excluded from the prior completed count when calculating the discount.
- **Bill only for COMPLETE appointments** — calling `GET /appointments/{id}/bill` on a SCHEDULED or CANCELLED appointment returns `400 Bad Request`.
- **Cancelled appointments are final** — once an appointment is CANCELLED, its status cannot be updated.
- **Specialties with fees** — only `GENERAL`, `ORTHOPEDICS`, and `CARDIOLOGY` have defined fees. Other specialties will throw an error at billing time.

## Design Decisions

### Layered architecture
Controller → Service → Repository, with no layer skipping. Each layer has a single responsibility.

### Repository interface pattern
`PatientRepository`, `DoctorRepository`, `AppointmentRepository` are interfaces. In-memory implementations are swappable with a real DB implementation without touching the service layer.

### Exception-based error handling
Services throw typed exceptions (`NotFoundException`, `ValidationException`, `ConflictException`). A global `ExceptionHandler` per exception type maps them to HTTP responses — controllers stay clean.

### Extension function mappers
`PatientRequest.toModel()`, `Patient.toResponse()` — mappers live in a dedicated `mapper` package as Kotlin extension functions, keeping models and DTOs free of conversion logic.

### `FeeResolver<K>` generic interface
Fee lookup is abstracted behind `FeeResolver<ConsultationFeeKey>`. `BillingService` depends on the interface, not the implementation. Fees can be swapped to a DB-backed resolver without changing any service code.

### `ExperienceTier` enum
Experience ranges (JUNIOR / MID / SENIOR) are modelled as an enum with `minYears` and `maxYears`. Adding a new tier requires only a new enum entry — no `when` branches scattered across the codebase.

### Bill calculated on demand
The bill is not stored on the appointment. `GET /appointments/{id}/bill` always computes a fresh bill from current data. This keeps the `Appointment` model clean and avoids stale billing data.

use seed.sh to generate mock data

calculate bill using appointment/bill/1

Not considered for now:
- booking appointments per date
- checking duplicate appointments
- update and delete for patients and doctors


