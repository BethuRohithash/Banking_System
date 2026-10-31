# Gateway Server JWT Authentication API

## Authentication Endpoints

### Login
**POST** `/auth/login`

Request Body:
```json
{
  "username": "string",
  "password": "string"
}
```

Response (Success - 200):
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiQURNSU4iLCJzdWIiOiJhZG1pbiIsImlhdCI6MTY5...",
  "username": "admin",
  "role": "ADMIN",
  "message": "Login successful"
}
```

Response (Error - 401):
```json
{
  "token": null,
  "username": null,
  "role": null,
  "message": "Invalid username or password"
}
```

### Validate Token
**GET** `/auth/validate`

Headers:
```
Authorization: Bearer <jwt-token>
```

Response (Success - 200):
```
Token is valid for user: admin with role: ADMIN
```

Response (Error - 401):
```
Invalid token
```

### Get Demo Users
**GET** `/auth/demo-users`

Response (200):
```
Demo Users Available:

1. Admin User:
   - Username: admin
   - Password: admin123
   - Role: ADMIN
   - Access: Full access to all endpoints

2. Manager User:
   - Username: manager / sarah
   - Password: manager123 / sarah123
   - Role: MANAGER
   - Access: Customers and Accounts management

3. Regular Users:
   - Username: user / john
   - Password: user123 / john123
   - Role: USER
   - Access: Limited read-only access to own data
```

## Protected Routes

All protected routes require the `Authorization: Bearer <token>` header.

### Customer Service Routes
- **GET** `/customers/**` - Access based on role
- **POST** `/customers/**` - ADMIN and MANAGER only
- **PUT** `/customers/**` - ADMIN and MANAGER only
- **DELETE** `/customers/**` - ADMIN only

### Account Service Routes
- **GET** `/accounts/**` - Access based on role
- **POST** `/accounts/**` - ADMIN and MANAGER only
- **PUT** `/accounts/**` - ADMIN and MANAGER only
- **DELETE** `/accounts/**` - ADMIN only

### Transaction Service Routes
- **GET** `/transactions/**` - ADMIN only
- **POST** `/transactions/**` - ADMIN only
- **PUT** `/transactions/**` - ADMIN only
- **DELETE** `/transactions/**` - ADMIN only

## Role-Based Access Matrix

| Endpoint | USER | MANAGER | ADMIN |
|----------|------|---------|-------|
| GET /customers/view | ✅ | ✅ | ✅ |
| GET /customers/all | ❌ | ✅ | ✅ |
| POST /customers/create | ❌ | ✅ | ✅ |
| PUT /customers/update | ❌ | ✅ | ✅ |
| DELETE /customers/delete | ❌ | ❌ | ✅ |
| GET /accounts/view | ✅ | ✅ | ✅ |
| GET /accounts/all | ❌ | ✅ | ✅ |
| POST /accounts/create | ❌ | ✅ | ✅ |
| PUT /accounts/update | ❌ | ✅ | ✅ |
| DELETE /accounts/delete | ❌ | ❌ | ✅ |
| GET /transactions/** | ❌ | ❌ | ✅ |
| POST /transactions/** | ❌ | ❌ | ✅ |
| PUT /transactions/** | ❌ | ❌ | ✅ |
| DELETE /transactions/** | ❌ | ❌ | ✅ |

## Error Responses

### 401 Unauthorized
```json
{
  "error": "Missing Authorization Header",
  "status": 401
}
```

```json
{
  "error": "Invalid Authorization Header",
  "status": 401
}
```

```json
{
  "error": "Invalid or Expired Token",
  "status": 401
}
```

### 403 Forbidden
```json
{
  "error": "Access Denied - Insufficient Permissions",
  "status": 403
}
```

## Usage Examples

### 1. Login as Admin
```bash
curl -X POST http://localhost:8073/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

### 2. Access Customer Data (with token)
```bash
curl -X GET http://localhost:8073/customers/all \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### 3. Test Different Roles
```bash
# Login as Manager
curl -X POST http://localhost:8073/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "manager",
    "password": "manager123"
  }'

# Try to access transactions (should fail)
curl -X GET http://localhost:8073/transactions/all \
  -H "Authorization: Bearer <manager-token>"
```

## Security Headers

The gateway automatically adds these headers to downstream requests:
- `X-User-Name`: Username from the JWT token
- `X-User-Role`: User role from the JWT token

Downstream services can use these headers for additional authorization logic.
