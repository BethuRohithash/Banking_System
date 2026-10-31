# Banking System Microservices - OpenAPI Documentation

This document provides an overview of the OpenAPI documentation endpoints for each microservice in the banking system.

## OpenAPI Documentation Endpoints

### 1. Customer Management Service
- **Port**: 8082
- **OpenAPI JSON**: http://localhost:8082/v1/api-docs
- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **Base API Path**: /api

**Available Operations:**
- `GET /api/fetch/all` - Get all customers
- `POST /api/customer/create` - Register new customer  
- `GET /api/{customerId}` - Get customer by ID
- `PUT /api/update/{customerId}` - Update customer profile
- `DELETE /api/delete/{customerId}` - Remove customer profile

### 2. Account Management Service  
- **Port**: 8080
- **OpenAPI JSON**: http://localhost:8080/v1/api-docs
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Base API Path**: /api

**Available Operations:**
- `POST /api/create` - Open a new account
- `GET /api/fetch/all` - Get all accounts
- `GET /api/{accountId}` - Retrieve account details
- `PUT /api/update/{accountId}` - Update account information and status
- `PUT /api/status/{accountId}?status={ACTIVE|INACTIVE}` - Update account status only
- `PUT /api/{accountId}/{balance}` - Update account balance
- `DELETE /api/close/{accountId}` - Close or remove an account
- `PUT /api/update/{id}/{balance}` - Update account balance
- `DELETE /api/close/{accountId}` - Close or remove an account

### 3. Transaction Management Service
- **Port**: 8800  
- **OpenAPI JSON**: http://localhost:8800/v1/api-docs
- **Swagger UI**: http://localhost:8800/swagger-ui.html
- **Base API Path**: /api

**Available Operations:**
- `POST /api/create` - Initiate a new transaction
- `GET /api/reference/{transactionReferenceId}` - Retrieve transaction by reference
- `GET /api/account/{accountId}` - Retrieve transaction history by account
- `PUT /api/update/{transactionReferenceId}` - Amend transaction details
- `DELETE /api/cancel/{transactionId}` - Cancel a scheduled transaction

## Field Validations Implemented

### Customer Service Validations
- **firstName**: Required, max 100 characters
- **lastName**: Required, max 100 characters  
- **email**: Required, valid email format, max 100 characters, unique
- **phoneNo**: Required, valid phone format (+31 690000000), max 20 characters, unique
- **address**: Required, max 250 characters

### Account Service Validations
- **customerId**: Required, must be valid existing customer
- **accountType**: Required (SAVINGS, CHECKING, CREDIT)
- **initialDeposit**: Cannot be negative
- **accountNumber**: Unique constraint
- **balance**: Cannot be negative

### Transaction Service Validations
- **accountId**: Required, must be valid existing account
- **type**: Required (DEPOSIT, WITHDRAWAL)
- **amount**: Required, must be positive (minimum 0.01)
- **transactionDate**: Optional, proper ISO 8601 date/time formatting
- Account must be ACTIVE for transaction processing
- Sufficient funds validation for withdrawals

## Error Handling

All services implement comprehensive error handling with:
- **400 Bad Request**: Validation failures with detailed field-level error messages
- **404 Not Found**: Resource not found errors
- **409 Conflict**: Duplicate resource errors (email, phone, account number)
- **500 Internal Server Error**: Unexpected server errors

Error responses include:
- Error code
- Descriptive error message  
- Timestamp
- Request path information
- Validation error details (for 400 errors)

## Testing the APIs

### Using Swagger UI
1. Start the microservice
2. Navigate to the Swagger UI URL for the service
3. Use the interactive interface to test endpoints

### Using curl or Postman
Access the OpenAPI JSON specification at `/v1/api-docs` for each service to import into your preferred API testing tool.

### Example Customer Creation Request
```json
POST http://localhost:8082/api/customer/create
Content-Type: application/json

{
  "firstName": "Jordan",
  "lastName": "Lee", 
  "email": "jordan.lee@bank.com",
  "phoneNo": "+31 690000000",
  "address": "10 Bank Avenue, Rotterdam"
}
```

### Example Account Creation Request  
```json
POST http://localhost:8080/api/create
Content-Type: application/json

{
  "customerId": 1001,
  "accountType": "SAVINGS",
  "initialDeposit": 5000.00
}
```

### Example Transaction Creation Request
```json
POST http://localhost:8800/api/create  
Content-Type: application/json

{
  "accountId": 2002,
  "type": "DEPOSIT", 
  "amount": 750.00,
  "transactionDate": "2025-07-28T11:30:00"
}
```

## Security Considerations

- All validation errors do not expose internal system details
- Database passwords should be externalized and secured
- API endpoints are ready for authentication/authorization integration
- Input validation prevents SQL injection and other common attacks

## Data Schemas

### Customer Service Schemas

#### CustomersRequestDto
```json
{
  "firstName": "Jordan",
  "lastName": "Lee", 
  "email": "jordan.lee@bank.com",
  "phoneNo": "+31 690000000",
  "address": "10 Bank Avenue, Rotterdam"
}
```

#### CustomersResponseDto
```json
{
  "customerId": 1001,
  "firstName": "Jordan",
  "lastName": "Lee",
  "email": "jordan.lee@bank.com", 
  "phoneNo": "+31 690000000",
  "address": "10 Bank Avenue, Rotterdam",
  "createdAt": "2025-07-28T10:15:00",
  "status": "ACTIVE"
}
```

### Account Service Schemas

#### AccountRequestDto
```json
{
  "customerId": 1001,
  "accountType": "SAVINGS",
  "initialDeposit": 5000.00
}
```

#### AccountResponseDto
```json
{
  "accountId": 2002,
  "accountNumber": "CTS91ABNA0417164300",
  "customerId": 1001,
  "accountType": "SAVINGS",
  "balance": 5000.00,
  "createdAt": "2025-07-28T10:00:00",
  "isActive": "ACTIVE"
}
```

### Transaction Service Schemas

#### TransactionRequestDto
```json
{
  "accountId": 2002,
  "type": "DEPOSIT",
  "amount": 750.00,
  "transactionDate": "2025-07-28T11:30:00"
}
```

#### TransactionResponseDto
```json
{
  "transactionReference": "TRX123456789ABC",
  "accountId": 2002,
  "type": "DEPOSIT", 
  "amount": 750.00,
  "status": "COMPLETED",
  "transactionDate": "2025-07-28T11:30:00"
}
```

### Common Response Schemas

#### ResponseDto (Success)
```json
{
  "statusCode": "200",
  "statusMsg": "Request processed successfully"
}
```

#### ErrorResponseDto (Error)
```json
{
  "apiPath": "/api/customer/create",
  "errorCode": "BAD_REQUEST", 
  "errorMessage": "Validation failed for one or more fields",
  "errorTime": "2025-08-01T10:15:30"
}
```

## Schema Validation Rules

### Customer Service
- **firstName/lastName**: Required, max 100 characters
- **email**: Required, valid email format, unique, max 100 characters  
- **phoneNo**: Required, valid international format (+31 690000000), max 20 characters
- **address**: Required, max 250 characters

### Account Service
- **customerId**: Required, must exist in customer service
- **accountType**: Required, one of: SAVINGS, CHECKING, CREDIT
- **initialDeposit**: Optional, must be non-negative (≥ 0.0)
- **balance**: Cannot be negative during operations

### Transaction Service  
- **accountId**: Required, must exist and be active
- **type**: Required, one of: DEPOSIT, WITHDRAWAL
- **amount**: Required, must be positive (≥ 0.01)
- **transactionDate**: Optional, ISO 8601 format, future dates create scheduled transactions

## Enum Values

### CustomerStatus
- `ACTIVE` - Customer can perform operations
- `INACTIVE` - Customer account is disabled  
- `PENDING` - Customer registration pending approval

### AccountStatus
- `ACTIVE` - Account can be used for transactions
- `INACTIVE` - Account is closed/suspended

### AccountType
- `SAVINGS` - Savings account
- `CHECKING` - Checking/current account
- `CREDIT` - Credit account

### TransactionType
- `DEPOSIT` - Money deposit into account
- `WITHDRAWAL` - Money withdrawal from account

### TransactionStatus
- `COMPLETED` - Transaction processed successfully
- `SCHEDULED` - Transaction scheduled for future execution
- `CANCELLED` - Transaction was cancelled
- `FAILED` - Transaction failed due to error
