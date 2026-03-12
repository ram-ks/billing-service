#!/bin/bash

BASE_URL="http://localhost:8080"

post() {
    local url=$1
    local body=$2
    curl -s -X POST "$url" \
        -H "Content-Type: application/json" \
        -d "$body"
}

patch() {
    local url=$1
    local body=$2
    curl -s -X PATCH "$url" \
        -H "Content-Type: application/json" \
        -d "$body"
}

extract_id() {
    echo "$1" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*'
}

check_server() {
    echo -e "Checking server is up..."
    response=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/patients")
    if [ "$response" != "200" ]; then
        echo -e "Server not reachable at $BASE_URL (HTTP $response). Is mn:run running?"
        exit 1
    fi
    echo -e "Server is up.\n"
}

declare -a DOCTOR_PAYLOADS=(
    '{"first_name":"Alexander","last_name":"Flemming","npi_number":"NPI1001","speciality":"CARDIOLOGY","practice_start_date":"01/06/1995"}'
    '{"first_name":"Joseph","last_name":"Lister","npi_number":"NPI1002","speciality":"ORTHOPEDICS","practice_start_date":"15/03/2000"}'
    '{"first_name":"Lalji","last_name":"Singh","npi_number":"NPI1003","speciality":"GENERAL","practice_start_date":"20/08/2005"}'
    '{"first_name":"Marie","last_name":"Curie","npi_number":"NPI1004","speciality":"CARDIOLOGY","practice_start_date":"10/01/1998"}'
    '{"first_name":"Louis","last_name":"Pasteur","npi_number":"NPI1005","speciality":"GENERAL","practice_start_date":"05/07/1993"}'
    '{"first_name":"Werner","last_name":"Heisenberg","npi_number":"NPI1006","speciality":"ORTHOPEDICS","practice_start_date":"22/09/2010"}'
    '{"first_name":"Maharishi","last_name":"Shushruth","npi_number":"NPI1007","speciality":"CARDIOLOGY","practice_start_date":"11/11/2003"}'
    '{"first_name":"Galileo","last_name":"Galileo","npi_number":"NPI1008","speciality":"CARDIOLOGY","practice_start_date":"30/04/2007"}'
    '{"first_name":"Nitya","last_name":"Anand","npi_number":"NPI1009","speciality":"GENERAL","practice_start_date":"14/02/2008"}'
    '{"first_name":"Upendra","last_name":"Nath","npi_number":"NPI1010","speciality":"ORTHOPEDICS","practice_start_date":"03/12/1999"}'
)

declare -a PATIENT_PAYLOADS=(
    '{"first_name":"John","last_name":"Doe","dob":"15/01/1990","age":34,"insurance":{"bin_number":1001,"pcn_number":"PCN001","member_id":"MEM001"}}'
    '{"first_name":"Jane","last_name":"Smith","dob":"22/05/1985","age":39,"insurance":{"bin_number":1002,"pcn_number":"PCN002","member_id":"MEM002"}}'
    '{"first_name":"Michael","last_name":"Brown","dob":"08/09/1978","age":46,"insurance":{"bin_number":1003,"pcn_number":"PCN003","member_id":"MEM003"}}'
    '{"first_name":"Emily","last_name":"Davis","dob":"30/11/1995","age":29,"insurance":{"bin_number":1004,"pcn_number":"PCN004","member_id":"MEM004"}}'
    '{"first_name":"David","last_name":"Wilson","dob":"14/03/1970","age":54,"insurance":{"bin_number":1005,"pcn_number":"PCN005","member_id":"MEM005"}}'
    '{"first_name":"Sophia","last_name":"Taylor","dob":"07/07/2000","age":24,"insurance":{"bin_number":1006,"pcn_number":"PCN006","member_id":"MEM006"}}'
    '{"first_name":"James","last_name":"Anderson","dob":"19/04/1988","age":36,"insurance":{"bin_number":1007,"pcn_number":"PCN007","member_id":"MEM007"}}'
    '{"first_name":"Olivia","last_name":"Thomas","dob":"25/12/1993","age":31,"insurance":{"bin_number":1008,"pcn_number":"PCN008","member_id":"MEM008"}}'
    '{"first_name":"William","last_name":"Jackson","dob":"02/06/1965","age":59,"insurance":{"bin_number":1009,"pcn_number":"PCN009","member_id":"MEM009"}}'
    '{"first_name":"Elon","last_name":"Musk","dob":"11/08/2002","age":22,"insurance":{"bin_number":1010,"pcn_number":"PCN010","member_id":"MEM010"}}'
)

check_server

# Step 1 — Register 10 doctors
echo -e "Step 1: Registering 10 doctors..."
declare -a DOCTOR_IDS=()

for i in "${!DOCTOR_PAYLOADS[@]}"; do
    response=$(post "$BASE_URL/doctors" "${DOCTOR_PAYLOADS[$i]}")
    id=$(extract_id "$response")
    DOCTOR_IDS+=("$id")
    echo -e "Doctor $((i+1)) registered → id=$id"
done

echo -e "\nDoctors registered: ${DOCTOR_IDS[*]}\n"

# Step 2 — Register 10 patients
echo -e "Step 2: Registering 10 patients..."
declare -a PATIENT_IDS=()

for i in "${!PATIENT_PAYLOADS[@]}"; do
    response=$(post "$BASE_URL/patients" "${PATIENT_PAYLOADS[$i]}")
    id=$(extract_id "$response")
    PATIENT_IDS+=("$id")
    echo -e "Patient $((i+1)) registered → id=$id"
done

echo -e "\nPatients registered: ${PATIENT_IDS[*]}\n"

# Step 3 — Book 5 appointments per patient (round-robin across doctors)
echo -e "Step 3: Booking 5 appointments per patient..."
declare -a APPOINTMENT_IDS=()
DOCTOR_COUNT=${#DOCTOR_IDS[@]}

for patient_id in "${PATIENT_IDS[@]}"; do
    for appt_num in $(seq 1 5); do
        doctor_index=$(( (appt_num - 1) % DOCTOR_COUNT ))
        doctor_id=${DOCTOR_IDS[$doctor_index]}

        response=$(post "$BASE_URL/appointments" \
            "{\"patient_id\":$patient_id,\"doctor_id\":$doctor_id}")
        id=$(extract_id "$response")
        APPOINTMENT_IDS+=("$id")
        echo -e "Appointment booked → id=$id (patient=$patient_id, doctor=$doctor_id)"
    done
done

echo -e "\nAppointments booked: ${#APPOINTMENT_IDS[@]} total\n"

# Step 3b — Extra appointments for specific patients to hit target totals
echo -e "Step 3b: Booking extra appointments for Patient 1, 2 and 3..."

declare -A EXTRA_APPOINTMENTS=(
    [0]=7   # Patient 1 index → 7 more
    [1]=5   # Patient 2 index → 5 more
    [2]=2   # Patient 3 index → 2 more
)

for patient_index in "${!EXTRA_APPOINTMENTS[@]}"; do
    patient_id=${PATIENT_IDS[$patient_index]}
    extra_count=${EXTRA_APPOINTMENTS[$patient_index]}
    target=$(( 5 + extra_count ))
    echo -e "\n Patient $((patient_index + 1)) (id=$patient_id) — adding $extra_count appointments (target: $target total)"

    for appt_num in $(seq 1 "$extra_count"); do
        doctor_index=$(( (5 + appt_num - 1) % DOCTOR_COUNT ))
        doctor_id=${DOCTOR_IDS[$doctor_index]}

        response=$(post "$BASE_URL/appointments" \
            "{\"patient_id\":$patient_id,\"doctor_id\":$doctor_id}")
        id=$(extract_id "$response")
        if [ -z "$id" ]; then
            echo -e " Extra appointment failed (patient=$patient_id, doctor=$doctor_id) → $response"
            exit 1
        fi
        APPOINTMENT_IDS+=("$id")
        echo -e "Extra appointment booked → id=$id (patient=$patient_id, doctor=$doctor_id)"
    done
done

echo -e "\nExtra appointments booked. Running total: ${#APPOINTMENT_IDS[@]}\n"

# Step 4 — Mark all appointments as COMPLETE
echo -e "Step 4: Completing all appointments..."

for appt_id in "${APPOINTMENT_IDS[@]}"; do
    response=$(patch "$BASE_URL/appointments/$appt_id/status" \
        '{"status":"COMPLETED"}')
    echo -e "   Appointment $appt_id → COMPLETED"
done

echo -e "\nAll appointments marked COMPLETED.\n"
echo -e "  Doctors       : ${#DOCTOR_IDS[@]}"
echo -e "  Patients      : ${#PATIENT_IDS[@]}"
echo -e "  Appointments  : ${#APPOINTMENT_IDS[@]}"
echo -e ""
echo -e "To get a bill, run:"
echo -e "  curl http://localhost:8080/appointments/{id}/bill"
echo -e ""
echo -e "Appointment IDs: ${APPOINTMENT_IDS[*]}"
