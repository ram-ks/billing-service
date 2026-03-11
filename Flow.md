
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

### Step by step

**1. Register Doctor**
```json
POST /doctors
{
  "first_name": "Jane",
  "last_name": "Smith",
  "npi_number": "NPI001",
  "specialty": "CARDIOLOGY",
  "practice_start_date": "01/06/2000"
}
```

**2. Register Patient**
```json
POST /patients
{
  "first_name": "John",
  "last_name": "Doe",
  "dob": "15/01/1990",
  "age": 34,
  "insurance": {
    "bin_number": 121,
    "pcn_number": "PCN001",
    "member_id": "MEM001"
  }
}
```

**3. Book Appointment**
```json
POST /appointments
{
  "patient_id": 1,
  "doctor_id": 1
}
→ 201 { "id": 1, "status": "SCHEDULED" }
```

**4. Complete Appointment**
```json
PATCH /appointments/1/status
{
  "status": "COMPLETE"
}
→ 200 { "id": 1, "status": "COMPLETE" }
```

**5. Get Bill**
```json
GET /appointments/1/bill
→ 200 {
    "base_fee": 2000.0,
    "discount_percent": 0.0,
    "discount_amount": 0.0,
    "amount_after_discount": 2000.0,
    "gst_amount": 240.0,
    "total_amount": 2240.0,
    "insurance_amount": 2016.0,
    "co_pay_amount": 224.0
  }
```

---