# Simple Email Client - Backend Services Architecture

## Overview

This is an enterprise-grade email client backend built with Java Spring Boot, designed to handle 2M+ accounts with high availability, security, and scalability.

## Architecture Highlights

### Technology Stack
- **Backend Framework**: Spring Boot 4.0.x (SNAPSHOT)
- **Database**: PostgreSQL with Hibernate/JPA
- **Caching**: Redis
- **Event Streaming**: Apache Kafka
- **Object Storage**: MinIO (S3-compatible)
- **Search**: OpenSearch
- **Authentication**: Keycloak (OIDC)
- **Real-time**: WebSocket
- **Build Tool**: Maven
- **Java Version**: 17

### Design Patterns Implemented

1. **Repository Pattern**: Clean separation between data access and business logic
2. **Service Layer Pattern**: Business logic encapsulation
3. **DTO Pattern**: Data transfer objects for API layer
4. **Transactional Outbox Pattern**: Reliable event publishing with `OutboxEvent` entity
5. **Multi-tenancy**: Tenant isolation at data level
6. **Event-Driven Architecture**: Kafka-based event streaming

## Project Structure

```
simpleemailclient/
├── src/main/java/com/example/projects/simpleemailclient/
│   ├── model/                    # Domain Entities
│   │   ├── User.java
│   │   ├── Mailbox.java
│   │   ├── Message.java
│   │   ├── Attachment.java
│   │   ├── Rule.java
│   │   ├── AuditLog.java
│   │   └── OutboxEvent.java
│   │
│   ├── repository/               # Data Access Layer
│   │   ├── UserRepository.java
│   │   ├── MailboxRepository.java
│   │   ├── MessageRepository.java
│   │   ├── AttachmentRepository.java
│   │   ├── RuleRepository.java
│   │   ├── AuditLogRepository.java
│   │   └── OutboxEventRepository.java
│   │
│   ├── service/                  # Service Interfaces
│   │   ├── UserService.java
│   │   ├── MailboxService.java
│   │   ├── MessageMetadataService.java
│   │   ├── ComposeService.java
│   │   ├── SearchService.java
│   │   ├── RulesService.java
│   │   ├── NotificationService.java
│   │   └── AuditService.java
│   │
│   ├── service/impl/             # Service Implementations
│   │   └── UserServiceImpl.java
│   │
│   ├── controller/               # REST Controllers
│   │   └── UserController.java
│   │
│   ├── dto/                      # Data Transfer Objects
│   │   ├── UserDTO.java
│   │   ├── MessageDTO.java
│   │   └── MailboxDTO.java
│   │
│   ├── event/                    # Domain Events
│   │   ├── BaseEvent.java
│   │   ├── MessageIngestedEvent.java
│   │   └── MessageUpdatedEvent.java
│   │
│   ├── config/                   # Configuration Classes
│   │   ├── RedisConfig.java
│   │   ├── KafkaConfig.java
│   │   ├── MinIOConfig.java
│   │   ├── WebSocketConfig.java
│   │   └── JacksonConfig.java
│   │
│   └── SimpleemailclientApplication.java
│
└── src/main/resources/
    ├── application.properties
    └── db/migration/             # Flyway migrations (to be added)
```

## Core Domain Models

### User
- Multi-tenant support with `tenantId`
- Quota management (`quotaBytes`, `usedBytes`)
- Status tracking (ACTIVE, INACTIVE, SUSPENDED, etc.)
- Optimistic locking with `@Version`

### Mailbox
- Folder hierarchy support
- Automatic count tracking (unread, total)
- Standard folders (INBOX, SENT, DRAFTS, TRASH, SPAM, ARCHIVE)
- Custom folder support

### Message
- Metadata storage (headers, envelope)
- MIME content pointer to MinIO
- Threading support via `threadId`
- Email protocol fields (Message-ID, In-Reply-To, References)
- DKIM/SPF validation results
- Spam scoring

### Attachment
- Blob storage pointer to MinIO
- Virus scan status tracking
- SHA-256 checksum for integrity
- Inline vs. regular attachment support

### Rule
- Email filtering and automation
- Sieve-like execution
- Priority-based ordering
- JSON-based condition and action storage

### AuditLog
- Immutable audit trail
- Security event tracking
- Compliance support
- Append-only pattern

### OutboxEvent
- Transactional outbox pattern
- Reliable event publishing to Kafka
- Retry mechanism with status tracking

## Key Services

### UserService
- User lifecycle management
- Quota enforcement
- Storage tracking
- Multi-tenancy support

### MailboxService
- Folder CRUD operations
- Count management
- Default mailbox creation
- Hierarchy support

### MessageMetadataService
- Message operations
- Threading logic
- Flag management
- Move/delete operations

### ComposeService
- Draft management
- Email sending workflow
- Attachment handling
- Reply/Forward support
- Rate limiting

### SearchService
- OpenSearch integration
- Full-text search
- Advanced query support
- Saved searches

### RulesService
- Email filtering
- Automation rules
- Condition evaluation
- Action execution

### NotificationService
- WebSocket push notifications
- Real-time updates
- Mobile push support

### AuditService
- Audit trail management
- Security logging
- Compliance exports

## Infrastructure Components

### Redis
- Session storage
- Mailbox list caching
- Idempotency keys
- 30-minute TTL default

### Kafka
- Event streaming backbone
- Events: message.ingested, message.updated, rule.applied
- 3x replication
- At-least-once delivery

### MinIO
- S3-compatible object storage
- Separate buckets for messages and attachments
- Erasure coding support
- Cross-region replication ready

### PostgreSQL
- Primary data store
- Logical sharding by user hash (planned)
- Patroni/Stolon for HA (planned)
- Flyway for migrations

### OpenSearch
- Full-text search
- Header search
- <2s P95 latency target

## API Layer

### REST Controllers
- RESTful API design
- DTO-based request/response
- Proper HTTP status codes
- Pagination support

### Example Endpoints (User Controller)
```
POST   /api/v1/users              - Create user
GET    /api/v1/users/{id}         - Get user by ID
GET    /api/v1/users/email/{email} - Get user by email
GET    /api/v1/users/tenant/{id}  - Get users by tenant
PUT    /api/v1/users/{id}         - Update user
DELETE /api/v1/users/{id}         - Delete user
GET    /api/v1/users/quota/near-limit - Get users near quota
```

## Security Features

1. **Multi-tenancy**: Data isolation via `tenantId`
2. **Audit Logging**: Comprehensive audit trail
3. **Keycloak Integration**: OIDC/OAuth2 authentication
4. **Quota Management**: Storage limits enforcement
5. **Rate Limiting**: Sending limits per user
6. **Virus Scanning**: Attachment scanning status

## Scalability Features

1. **Horizontal Scaling**: Stateless service design
2. **Caching**: Redis for hot data
3. **Event-Driven**: Async processing via Kafka
4. **Sharding Ready**: User-based sharding design
5. **Pagination**: All list endpoints support pagination
6. **Batch Operations**: Hibernate batch inserts/updates

## Observability

### Actuator Endpoints
- `/actuator/health` - Health checks
- `/actuator/metrics` - Application metrics
- `/actuator/prometheus` - Prometheus metrics export

### Logging
- Structured logging with SLF4J/Logback
- Debug level for application code
- SQL logging for development

## Next Steps

To fully implement this design, you'll need to:

1. **Implement Remaining Services**: Complete service implementations for all service interfaces
2. **Add Controllers**: Create REST controllers for all services
3. **Create MapStruct Mappers**: Professional DTO mapping
4. **Add Database Migrations**: Flyway scripts for schema
5. **Implement Event Handlers**: Kafka consumers for events
6. **Add Security Configuration**: Keycloak integration
7. **Write Tests**: Unit and integration tests
8. **Add API Documentation**: Swagger/OpenAPI
9. **Implement OpenSearch Client**: Search functionality
10. **Add Error Handling**: Global exception handlers

## Configuration

All configuration is in `application.properties`:
- Database connection
- Redis settings
- Kafka brokers
- MinIO endpoints
- Keycloak realm
- Application-specific settings (quota, limits)

## Development

### Build
```bash
./mvnw clean install
```

### Run
```bash
./mvnw spring-boot:run
```

### Dependencies
All major dependencies are configured:
- Spring Boot starters (Web, Data JPA, Security, WebSocket, Cache, Actuator)
- PostgreSQL driver
- Redis client (Lettuce)
- Kafka client
- MinIO client
- OpenSearch client
- Lombok (boilerplate reduction)
- MapStruct (DTO mapping)
- Micrometer (metrics)

## Best Practices Implemented

1. **SOLID Principles**: Single responsibility, dependency injection
2. **Clean Architecture**: Layered design (Controller → Service → Repository)
3. **Immutability**: Using `@Builder` and Lombok
4. **Defensive Programming**: Input validation, null checks
5. **Transaction Management**: `@Transactional` annotations
6. **Optimistic Locking**: `@Version` for concurrency control
7. **Indexing**: Database indexes on frequently queried fields
8. **Auditing**: Automatic timestamp tracking with `@CreationTimestamp`, `@UpdateTimestamp`

---

**Note**: This is a scaffolded structure. Full implementation of all services, security, and integration points is required for production readiness.
