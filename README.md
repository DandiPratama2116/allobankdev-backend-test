# Split Bill REST API

A production-grade Spring Boot service for managing shared group expenses, supporting multiple split strategies, debt settlements, and optimized balance simplification.

---

## 1. Candidate Personalization

- **GitHub Username:** `dandipratama`
- **Calculation Formula:**
  - Lowercase ASCII values:
    `d (100) + a (97) + n (110) + d (100) + i (105) + p (112) + r (114) + a (97) + t (116) + a (97) + m (109) + a (97) = 1254`
  - `service_charge_pct = 1254 % 10 = 4` (**4%**)
- **Computed Value in Code:**
  - Evaluated dynamically in `PersonalizationService` via `app.personalization.github-username=dandipratama`.
  - Applied to group total expenses: `service_charge_amount = (totalExpenses * 4) / 100`.

---

## 2. Submission Question & Architectural Trade-off

> **"What was the hardest design decision you made while building this, and what trade-off did you accept?"**

The most critical design decision was balancing between preserving bilateral debtor-creditor expense history versus generating an optimized, minimized cash-flow settlement. While a greedy two-pointer / min-cash-flow algorithm significantly reduces social friction by condensing complex debt webs down to at most \(N-1\) transactions, it severs direct auditability back to individual receipts (e.g., Alice might be asked to pay Charlie directly even though Alice never shared an expense with Charlie, solely because Bob was an intermediary debtor). I accepted this trade-off by decoupling the raw immutable expense and payment records from the computed settlement view, presenting both granular per-participant balance summaries alongside the recommended minimized transaction plan so transparency is never lost.

---

## 3. Key Features

- **Split Strategies:**
  - `EQUAL`: Splits amounts evenly across participants with deterministic remainder cent distribution, ensuring \(\sum \text{splits} = \text{total}\).
  - `EXACT`: Explicit amounts per participant with total amount reconciliation.
  - `PERCENTAGE`: Percentage allocation per participant strictly validating 100.00% sum.
- **Settlement Optimization:**
  - Greedy Min-Cash-Flow engine that condenses multi-party group debts into the minimal number of direct settlement transfers.
- **Payment Recording:**
  - Record payments between participants that immediately update outstanding balances.
- **Categorization & Auditing:**
  - Tag expenses (`FOOD_AND_DRINK`, `TRANSPORTATION`, `ACCOMMODATION`, `ENTERTAINMENT`, `SHOPPING`, `UTILITIES`, `OTHER`) with aggregated category summaries.
- **Financial Precision:**
  - Strictly `BigDecimal` with 2 decimal places and `RoundingMode.HALF_UP` / `RoundingMode.DOWN` for remainder pennies.

---

## 4. How to Build and Run

### Prerequisites
- Java 17+
- Docker (optional for containerized execution)

### Option A: Running with Maven Wrapper
```bash
# Build and run tests
./mvnw clean test

# Run application locally on port 4110
./mvnw spring-boot:run
```

### Option B: Running with Docker
```bash
# Build multi-stage Docker image
docker build -t split-bill-api:latest .

# Run container on port 4110
docker run -p 4110:4110 split-bill-api:latest
```

The application starts on `http://localhost:4110`.
- H2 Console is accessible at: `http://localhost:4110/h2-console` (JDBC URL: `jdbc:h2:mem:splitbilldb`, Username: `sa`, Password: blank)

---

## 5. API Endpoints & Example cURL Commands

### 1. Create a Bill Group
```bash
curl -X POST http://localhost:4110/api/v1/groups \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Weekend Trip",
    "description": "Cabin getaway with friends",
    "currency": "IDR",
    "participants": ["Alice", "Bob", "Charlie", "David"]
  }'
```

*Example Response:*
```json
{
  "id": "e969966b-84a1-4200-a6ad-e0d0617300c8",
  "name": "Weekend Trip",
  "description": "Cabin getaway with friends",
  "currency": "IDR",
  "participants": [
    {"id": "c1f1b0a8-3486-4444-a0f5-6da13d523ec3", "name": "Alice", "email": null, "createdAt": "2026-11-24T14:40:00Z"},
    {"id": "d2f2c1b9-4597-4555-b1a6-7eb24e634fd4", "name": "Bob", "email": null, "createdAt": "2026-11-24T14:40:00Z"},
    {"id": "e3a3d2ca-5608-4666-c2b7-8fc35f745ae5", "name": "Charlie", "email": null, "createdAt": "2026-11-24T14:40:00Z"},
    {"id": "f4b4e3db-6719-4777-d3c8-9ad46a856bf6", "name": "David", "email": null, "createdAt": "2026-11-24T14:40:00Z"}
  ],
  "totalExpensesCount": 0,
  "createdAt": "2026-11-24T14:40:00Z",
  "updatedAt": "2026-11-24T14:40:00Z"
}
```

---

### 2. Add an Additional Participant
```bash
curl -X POST http://localhost:4110/api/v1/groups/e969966b-84a1-4200-a6ad-e0d0617300c8/participants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Emma",
    "email": "emma@example.com"
  }'
```

---

### 3. Add Expense - Equal Split (with Remainder Cent Allocation)
```bash
curl -X POST http://localhost:4110/api/v1/groups/e969966b-84a1-4200-a6ad-e0d0617300c8/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Villa Accommodation",
    "category": "ACCOMMODATION",
    "amount": 100.00,
    "paidBy": "Alice",
    "splitType": "EQUAL"
  }'
```

---

### 4. Add Expense - Exact Split
```bash
curl -X POST http://localhost:4110/api/v1/groups/e969966b-84a1-4200-a6ad-e0d0617300c8/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "description": "BBQ Dinner",
    "category": "FOOD_AND_DRINK",
    "amount": 120.00,
    "paidBy": "Bob",
    "splitType": "EXACT",
    "splits": [
      {"participantName": "Alice", "amount": 40.00},
      {"participantName": "Bob", "amount": 30.00},
      {"participantName": "Charlie", "amount": 50.00}
    ]
  }'
```

---

### 5. Add Expense - Percentage Split
```bash
curl -X POST http://localhost:4110/api/v1/groups/e969966b-84a1-4200-a6ad-e0d0617300c8/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "description": "Fuel & Road Tolls",
    "category": "TRANSPORTATION",
    "amount": 50.00,
    "paidBy": "Charlie",
    "splitType": "PERCENTAGE",
    "splits": [
      {"participantName": "Alice", "percentage": 25.00},
      {"participantName": "Bob", "percentage": 25.00},
      {"participantName": "Charlie", "percentage": 50.00}
    ]
  }'
```

---

### 6. Retrieve Settlement Summary
```bash
curl -X GET http://localhost:4110/api/v1/groups/e969966b-84a1-4200-a6ad-e0d0617300c8/settlement
```

*Example Response:*
```json
{
  "groupId": "e969966b-84a1-4200-a6ad-e0d0617300c8",
  "groupName": "Weekend Trip",
  "currency": "IDR",
  "totalExpenses": 270.00,
  "service_charge_pct": 4,
  "service_charge_amount": 10.80,
  "settled": false,
  "participantBalances": [
    {
      "participantName": "Alice",
      "totalPaid": 100.00,
      "totalShareOwed": 77.50,
      "netBalance": 22.50,
      "status": "IS_OWED"
    },
    {
      "participantName": "Bob",
      "totalPaid": 120.00,
      "totalShareOwed": 67.50,
      "netBalance": 52.50,
      "status": "IS_OWED"
    },
    {
      "participantName": "Charlie",
      "totalPaid": 50.00,
      "totalShareOwed": 100.00,
      "netBalance": -50.00,
      "status": "OWES"
    },
    {
      "participantName": "David",
      "totalPaid": 0.00,
      "totalShareOwed": 25.00,
      "netBalance": -25.00,
      "status": "OWES"
    }
  ],
  "settlements": [
    {
      "from": "Charlie",
      "to": "Bob",
      "amount": 50.00
    },
    {
      "from": "David",
      "to": "Alice",
      "amount": 22.50
    },
    {
      "from": "David",
      "to": "Bob",
      "amount": 2.50
    }
  ],
  "categorySummaries": [
    {
      "category": "ACCOMMODATION",
      "totalAmount": 100.00,
      "expenseCount": 1
    },
    {
      "category": "FOOD_AND_DRINK",
      "totalAmount": 120.00,
      "expenseCount": 1
    },
    {
      "category": "TRANSPORTATION",
      "totalAmount": 50.00,
      "expenseCount": 1
    }
  ]
}
```

---

### 7. Record a Debt Settlement Payment
```bash
curl -X POST http://localhost:4110/api/v1/groups/e969966b-84a1-4200-a6ad-e0d0617300c8/payments \
  -H "Content-Type: application/json" \
  -d '{
    "payer": "Charlie",
    "payee": "Bob",
    "amount": 50.00,
    "notes": "Bank transfer for BBQ and Fuel"
  }'
```

*Example Response:*
```json
{
  "id": "78b4a2d1-9210-4bc2-81ec-43e62fbb1234",
  "payer": "Charlie",
  "payee": "Bob",
  "amount": 50.00,
  "notes": "Bank transfer for BBQ and Fuel",
  "createdAt": "2026-11-24T14:45:00Z"
}
```
