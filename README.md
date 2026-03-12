# Billing Service

A healthcare clinic backend API built with **Kotlin + Micronaut** that handles patient registration, doctor onboarding, appointment booking, and consultation bill calculation with GST, insurance, and loyalty discounts.

## Table of Contents
- [Happy Path Flow](#happy-path-flow)
- [How to Run](#how-to-run)
- [How to Test](#how-to-test)
- [Assumptions](#assumptions)
- [Design Decisions](#design-decisions)

## Happy Path Flow

To successfully retrieve a bill, follow these steps in order:

1. **Onboard a patient** — `POST /patients`
2. **Onboard a doctor** — `POST /doctors`
3. **Book an appointment** — `POST /appointments` using the patient and doctor IDs from steps 1 & 2. Appointment is created in `SCHEDULED` state.
4. **Complete the appointment** — `PATCH /appointments/{id}/status` with status `COMPLETED`.
5. **Get the bill** — `GET /appointments/{id}/bill`

> **Note:** Step 4 is required. Calling the bill endpoint on a `SCHEDULED` or `CANCELLED` appointment returns `400 Bad Request`.

Checkout ./Flow.md for more details

use ./seed.sh to generate mock data

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

### Seed sample data 
```
./seed.sh
```
Note: This requires micronaut server up and running

This would create sample patients, doctors and appointments, we can then directly call `GET /appointments/1/bill`

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

### Storage
- **No database** — all data is stored in-memory using `MutableMap`. Data is lost on restart.

### Security
- **No authentication** — all endpoints are publicly accessible. In production, role-based access control would be required.

### API Behavior
- **No pagination** — `GET /patients`, `GET /doctors`, `GET /appointments` return all records.
- **Date format** — all dates (DOB, practice start date) use `dd/MM/yyyy`.
- **Age field** — age is accepted as part of the patient request and stored as-is. It is not derived from DOB.

### Business Rules
- **NPI uniqueness** — each doctor must have a unique NPI number. Duplicate registration returns `409 Conflict`.
- **Specialties with fees** — only `GENERAL`, `ORTHOPEDICS`, and `CARDIOLOGY` have defined fees. Other specialties will throw an error at billing time.
- **Cancelled appointments are final** — once an appointment is CANCELLED, its status cannot be updated.

### Billing
- **Bill on demand** — the bill is calculated fresh every time `GET /appointments/{id}/bill` is called. It is not stored.
- **Bill only for COMPLETED appointments** — calling `GET /appointments/{id}/bill` on a SCHEDULED or CANCELLED appointment returns `400 Bad Request`.
- **Discount counts prior visits only** — the current appointment is excluded from the prior completed count when calculating the discount.

---

## Design Decisions

### Layered architecture
Controller → Service → Repository, with no layer skipping. Each layer has a single responsibility.

### Domain Entities
`Patient`,`Doctor`,`Appointment`,`Insurance`,`Bill`
They have their respective DTOs

### Repository interface pattern
`PatientRepository`, `DoctorRepository`, `AppointmentRepository` are interfaces. In-memory implementations are swappable with a real DB implementation without touching the service layer.

### `FeeResolver<K>` generic interface
Fee lookup is abstracted behind `FeeResolver<ConsultationFeeKey>`. `BillingService` depends on the interface, not the implementation. Fees can be swapped to a DB-backed resolver without changing any service code.

### `ExperienceTier` enum
Experience ranges (JUNIOR / MID / SENIOR) are modelled as an enum with `minYears` and `maxYears`. Adding a new tier requires only a new enum entry — no `when` branches scattered across the codebase.

### Bill calculated on demand
The bill is not stored on the appointment. `GET /appointments/{id}/bill` always computes a fresh bill from current data.
