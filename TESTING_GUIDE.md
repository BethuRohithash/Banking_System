# Banking System Security Testing Guide

## Prerequisites
Make sure all services are running:
- Eureka Server: http://localhost:8761
- API Gateway: http://localhost:8073
- Accounts Service: http://localhost:8080
- Customers Service: http://localhost:8081
- Transactions Service: http://localhost:8082

## Step 1: Check Service Status

### Check Eureka Dashboard
Open: http://localhost:8761

You should see all services registered:
- GATEWAYSERVER
- ACCOUNTS
- CUSTOMERS
- TRANSACTIONS

### Check Gateway Health
```bash
curl http://localhost:8073/actuator/health
```

## Step 2: Test Authentication

### Get JWT Token
```bash
curl -X POST http://localhost:8073/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

Save the token from the response!

### Test Different Users
```bash
# Account Manager
curl -X POST http://localhost:8073/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "accountmanager", "password": "account123"}'

# Customer Manager  
curl -X POST http://localhost:8073/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "customermanager", "password": "customer123"}'

# Transaction Manager
curl -X POST http://localhost:8073/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "transactionmanager", "password": "transaction123"}'
```

## Step 3: Test Protected Endpoints

### Test Without Token (Should Fail)
```bash
curl http://localhost:8073/accounts/api/v1/accounts
# Expected: 401 Unauthorized
```

### Test With Valid Token
```bash
curl http://localhost:8073/accounts/api/v1/accounts \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
# Expected: 200 OK with accounts data
```

### Test Role-Based Access

#### Admin User (Full Access)
```bash
TOKEN="YOUR_ADMIN_TOKEN"

# Should work - Admin has all roles
curl -H "Authorization: Bearer $TOKEN" http://localhost:8073/accounts/api/v1/accounts
curl -H "Authorization: Bearer $TOKEN" http://localhost:8073/customers/api/v1/customers  
curl -H "Authorization: Bearer $TOKEN" http://localhost:8073/transactions/api/v1/transactions
```

#### Account Manager (Accounts Only)
```bash
TOKEN="YOUR_ACCOUNT_MANAGER_TOKEN"

# Should work
curl -H "Authorization: Bearer $TOKEN" http://localhost:8073/accounts/api/v1/accounts

# Should fail with 403 Forbidden
curl -H "Authorization: Bearer $TOKEN" http://localhost:8073/customers/api/v1/customers
curl -H "Authorization: Bearer $TOKEN" http://localhost:8073/transactions/api/v1/transactions
```

## Step 4: Test with Postman

### Import Collection
1. Open Postman
2. Create new collection "Banking System"
3. Add requests:

#### Authentication
- POST http://localhost:8073/auth/login
- Body: {"username": "admin", "password": "admin123"}

#### Accounts (with Bearer Token)
- GET http://localhost:8073/accounts/api/v1/accounts
- Headers: Authorization: Bearer {{token}}

#### Customers (with Bearer Token)  
- GET http://localhost:8073/customers/api/v1/customers
- Headers: Authorization: Bearer {{token}}

#### Transactions (with Bearer Token)
- GET http://localhost:8073/transactions/api/v1/transactions  
- Headers: Authorization: Bearer {{token}}

## Step 5: Check Swagger Documentation

Open: http://localhost:8073/swagger-ui.html

You should see APIs for all services plus the gateway authentication endpoints.

## Expected Results

✅ **Success Scenarios:**
- Login with valid credentials returns JWT token
- Requests with valid token and proper role succeed
- Admin user can access all services
- Role-specific users can only access their designated services

❌ **Failure Scenarios:**
- Requests without token return 401 Unauthorized
- Requests with invalid token return 401 Unauthorized  
- Requests with valid token but wrong role return 403 Forbidden
- Login with invalid credentials returns 401 Unauthorized

## Troubleshooting

### Service Not Found
- Check if service is registered in Eureka dashboard
- Verify service application.yml has correct eureka configuration

### 401 Unauthorized
- Check if JWT token is included in Authorization header
- Verify token format: "Bearer <token>"
- Check if token has expired (24 hour expiry)

### 403 Forbidden  
- Check if user has required role for the endpoint
- Verify role assignments in AuthController

### Connection Refused
- Ensure all services are running on correct ports
- Check if MySQL database is running (for accounts/customers/transactions)
