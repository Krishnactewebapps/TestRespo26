# Product Service

A Spring Boot microservice for managing products, built with Oracle Database, JWT security, Logback logging, and Jackson for JSON serialization. This service provides a secure, robust, and extensible backend for product management in a microservices architecture.

## Features

- **CRUD Operations**: Create, read, update, and delete products.
- **Advanced Search**: Search products by name, price range, and stock.
- **Oracle Database**: Uses Oracle as the persistent data store.
- **JWT Security**: Secures endpoints with JSON Web Tokens and role-based access control.
- **Logging**: Application and audit logging using Logback, with separate audit logs for product additions.
- **Validation**: Input validation using JSR-303 annotations and global exception handling.
- **DTOs**: Clean separation of request/response models.
- **Swagger**: API documentation via Swagger UI.
- **Unit & Integration Tests**: JUnit-based tests for service and controller layers.

## API Endpoints

All endpoints are prefixed with `/api/products` and require JWT authentication.

| Method | Endpoint                        | Description                                 | Roles Allowed      |
|--------|----------------------------------|---------------------------------------------|--------------------|
| GET    | `/api/products`                 | List all products                           | USER, ADMIN        |
| GET    | `/api/products/{id}`            | Get product by ID                           | USER, ADMIN        |
| POST   | `/api/products`                 | Create a new product                        | ADMIN              |
| PUT    | `/api/products/{id}`            | Update an existing product                  | ADMIN              |
| DELETE | `/api/products/{id}`            | Delete a product                            | ADMIN              |
| GET    | `/api/products/search?name=...` | Search products by name                     | USER, ADMIN        |
| GET    | `/api/products/price/min`       | Products with price >= value                | USER, ADMIN        |
| GET    | `/api/products/stock/max`       | Products with stock < value                 | USER, ADMIN        |
| GET    | `/api/products/price/range`     | Products in price range                     | USER, ADMIN        |
| GET    | `/api/products/search/stock`    | Products by name and stock > value          | USER, ADMIN        |

### Example Request/Response

#### Create Product (POST `/api/products`)

**Request Body:**
```json
{
  "name": "Sample Product",
  "description": "A sample product.",
  "price": 19.99,
  "stock": 100
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Sample Product",
  "description": "A sample product.",
  "price": 19.99,
  "stock": 100
}
```

#### Error Response (Validation)
```json
{
  "name": "Product name is required",
  "price": "Price must be greater than 0"
}
```

## Setup Instructions

### Prerequisites
- Java 11 or higher
- Maven
- Oracle Database (running and accessible)

### Configuration

Edit `src/main/resources/application.properties`:

```
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:ORCL
spring.datasource.username=your_oracle_username
spring.datasource.password=your_oracle_password
jwt.secret=your_jwt_secret_key
```

### Build & Run

```
mvn clean install
mvn spring-boot:run
```

### API Documentation

Once running, access Swagger UI at:

```
http://localhost:8080/swagger-ui.html
```

### Running Tests

```
mvn test
```

## Security
- All endpoints require a valid JWT in the `Authorization: Bearer <token>` header.
- Only users with `ROLE_ADMIN` can create, update, or delete products.
- Users with `ROLE_USER` can view and search products.

## Logging
- Application logs: `logs/productservice.log`
- Audit logs (product additions): `logs/audit.log`

## License

This project is provided as-is for demonstration and educational purposes.