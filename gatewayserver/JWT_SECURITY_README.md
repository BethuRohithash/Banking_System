# JWT Security Implementation for Gateway Server

## Overview
This implementation provides a simple JWT-based authentication and authorization system for the banking microservices gateway.

## Features
- JWT token-based authentication
- Role-based access control (RBAC)
- Three user roles: ADMIN, MANAGER, USER
- Pre-configured demo users
- Token validation and expiration handling

## User Roles and Access Control

### ADMIN Role
- **Access**: Full access to all endpoints
- **Capabilities**:
  - Complete CRUD operations on customers
  - Complete CRUD operations on accounts
  - Complete CRUD operations on transactions
  - User management
  - System configuration

### MANAGER Role
- **Access**: Customer and Account management
- **Capabilities**:
  - CRUD operations on customers
  - CRUD operations on accounts
  - View transactions (read-only)
  - Generate reports

### USER Role
- **Access**: Limited to own data
- **Capabilities**:
  - View own customer information
  - View own accounts
  - View own transaction history (read-only)

## Demo Users

| Username | Password    | Role    | Description                    |
|----------|-------------|---------|--------------------------------|
| admin    | admin123    | ADMIN   | System administrator           |
| manager  | manager123  | MANAGER | Bank manager                   |
| sarah    | sarah123    | MANAGER | Additional manager user        |
| user     | user123     | USER    | Regular bank customer          |
| john     | john123     | USER    | Additional customer user       |

## API Endpoints

### Authentication
- `POST /auth/login` - Login and get JWT token
- `GET /auth/validate` - Validate token
- `GET /auth/demo-users` - Get demo user information

### Protected Routes
- `/customers/**` - Customer management (role-dependent access)
- `/accounts/**` - Account management (role-dependent access)
- `/transactions/**` - Transaction management (ADMIN only)

## Usage Instructions

### 1. Login to Get Token
```bash
curl -X POST http://localhost:8073/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "username": "admin",
  "role": "ADMIN",
  "message": "Login successful"
}
```

### 2. Use Token for Protected Requests
```bash
curl -X GET http://localhost:8073/customers/all \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### 3. Access Control Examples

#### ADMIN User
```bash
# Can access everything
GET /customers/**     ✅ Allowed
POST /customers/**    ✅ Allowed
PUT /accounts/**      ✅ Allowed
DELETE /transactions/** ✅ Allowed
```

#### MANAGER User
```bash
# Can manage customers and accounts
GET /customers/**     ✅ Allowed
POST /customers/**    ✅ Allowed
GET /accounts/**      ✅ Allowed
POST /transactions/** ❌ Forbidden
```

#### USER Role
```bash
# Read-only access to own data
GET /customers/view   ✅ Allowed
GET /accounts/get     ✅ Allowed
POST /customers/**    ❌ Forbidden
DELETE /accounts/**   ❌ Forbidden
```

## Error Responses

### Authentication Errors
```json
{
  "error": "Missing Authorization Header",
  "status": 401
}
```

### Authorization Errors
```json
{
  "error": "Access Denied - Insufficient Permissions",
  "status": 403
}
```

## Security Features
- Password encryption using BCrypt
- JWT token with 24-hour expiration
- Role-based endpoint protection
- CORS configuration for cross-origin requests
- Secure token validation

## Development Notes
- Tokens are valid for 24 hours
- Users are stored in-memory (suitable for demo/development)
- Passwords are automatically encrypted on startup
- All routes except `/auth/**` require authentication

## Testing the Implementation

1. Start the gateway server
2. Get demo user info: `GET /auth/demo-users`
3. Login with any demo user: `POST /auth/login`
4. Test protected endpoints with the returned token
5. Try accessing restricted resources with different roles

## Next Steps for Production
- Replace in-memory user storage with database
- Implement user registration/management
- Add refresh token mechanism
- Implement password reset functionality
- Add audit logging for security events
