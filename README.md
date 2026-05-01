# Event Management System

A distributed **microservices-based event management platform** built with Spring Boot and Spring Cloud. The system enables users to create, discover, and attend events with a scalable, cloud-native architecture.

## 🏗️ Architecture Overview

The project follows a **Microservices Architecture** with service discovery, API gateway pattern, and polyglot persistence:

```
┌─────────────────────────────────────────────────────────────┐
│                     Clients (Web/Mobile)                    │
└────────────────────────┬────────────────────────────────────┘
                         │
┌────────────────────────▼────────────────────────────────────┐
│        API Gateway (Spring Cloud Gateway)                   │
│        Port: 8080                                           │
│        - JWT Authentication Filter                         │
│        - Dynamic Route Discovery                           │
│        - Path-based Routing                                │
└────────┬───────────┬──────────────┬───────────┬────────────┘
         │           │              │           │
    ┌────▼─┐    ┌───▼──┐      ┌────▼─┐   ┌───▼──┐
    │User  │    │Event │      │Attend│   │Eureka│
    │Mgmt  │    │Mgmt  │      │ees   │   │Server│
    │(JWT) │    │(NoSQL)│     │(SQL) │   │Port: │
    └──┬───┘    └───┬──┘      └──┬───┘   │8761  │
       │            │            │       └──────┘
    ┌──▼───────┐ ┌──▼──────┐ ┌──▼───────┐
    │PostgreSQL│ │MongoDB  │ │PostgreSQL│
    │Port:5432 │ │Port:27  │ │Port:5432 │
    │event_udb │ │017      │ │event_proj│
    └──────────┘ └─────────┘ └──────────┘
```

## 📦 Technology Stack

| Component | Technology |
|-----------|------------|
| **Language** | Java 17 |
| **Framework** | Spring Boot 3.3.0 |
| **Cloud** | Spring Cloud 2023.0.2 |
| **Service Discovery** | Netflix Eureka Server |
| **API Gateway** | Spring Cloud Gateway |
| **Async Communication** | Spring WebFlux, WebClient |
| **Security** | Spring Security (WebFlux), JWT (JJWT 0.11.5) |
| **Databases** | PostgreSQL 12+, MongoDB 4.4+ |
| **Database Migrations** | Flyway (SQL), MongoDB scripts |
| **Build Tool** | Maven 3.6+ |
| **Testing** | TestContainers 1.18.3 |
| **Utilities** | Project Lombok |

## 📋 Prerequisites

- **Java 17+**
- **Maven 3.6+**
- **PostgreSQL 12+** (2 instances required)
- **MongoDB 4.4+**
- **Docker** (optional, for TestContainers)

## 🚀 Getting Started

### 1. Clone Repository
```bash
git clone <repository-url>
cd parentContainer
```

### 2. Start Infrastructure Services

#### Start PostgreSQL
```bash
# Database 1: event_proj (Attendees Service)
createdb event_proj

# Database 2: event_userdb (User Management Service)
createdb event_userdb
```

#### Start MongoDB
```bash
mongod --dbpath /path/to/mongodb/data
```

### 3. Build All Modules
```bash
mvn clean install
```

### 4. Start Services (in order)

**Terminal 1 - Service Discovery (Eureka)**
```bash
cd servicediscovery
mvn spring-boot:run
# Accessible at http://localhost:8761
```

**Terminal 2 - User Management**
```bash
cd usermanagement
mvn spring-boot:run
# Random port (register with Eureka)
```

**Terminal 3 - Event Management**
```bash
cd eventmanagement
mvn spring-boot:run
# Random port (register with Eureka)
```

**Terminal 4 - Attendees Service**
```bash
cd attendees
mvn spring-boot:run
# Random port (register with Eureka)
```

**Terminal 5 - API Gateway**
```bash
cd eventGateWay
mvn spring-boot:run
# Port: 8080 (Entry point for all requests)
```

## 📁 Project Structure

```
parentContainer/
├── servicediscovery/          # Eureka Server (Port 8761)
│   └── src/main/java/com/microserv/servicediscovery/
├── eventGateWay/              # API Gateway (Port 8080)
│   └── src/main/java/com/microserv/apigateway/
│       ├── filter/
│       │   └── AuthFilter.java          # JWT validation
│       └── Config/
│           └── SecurityConfig.java      # WebFlux security
├── usermanagement/            # User & Auth Service
│   └── src/main/java/com/party/
│       ├── userManagement/
│       │   └── controller/UserController.java
│       └── authManagement/
│           ├── controller/AuthController.java
│           └── service/
│               ├── AuthService.java
│               └── JwtService.java
├── eventmanagement/           # Event Management Service
│   └── src/main/java/com/party/eventmanagement/
│       ├── controller/EventController.java
│       ├── service/EventService.java
│       └── repository/EventRepository.java
├── attendees/                 # Attendees & RSVP Service
│   └── src/main/java/com/party/attendees/
│       ├── controller/AttendeesController.java
│       └── service/AttendService.java
└── pom.xml                    # Parent POM (Manages all modules)
```

## 🔑 Key Services

### 1. **Service Discovery (servicediscovery)**
- **Port**: 8761
- **Role**: Eureka Server for service registration & discovery
- **Config**: `eureka.client.register-with-eureka=false`

### 2. **API Gateway (eventGateWay)**
- **Port**: 8080
- **Role**: Single entry point, routes to microservices
- **Features**:
  - JWT token validation
  - Dynamic service discovery
  - Path-based routing
  - CORS security
- **Public Paths**: `/usermanagement/auth/**`, `/usermanagement/user/**`, `/eureka/**`, `/api/**`

### 3. **User Management (usermanagement)**
- **Database**: PostgreSQL (event_userdb)
- **Role**: User authentication & profile management
- **Features**:
  - User registration & profile management
  - JWT token generation (15-min access, 24-hr refresh)
  - Token refresh mechanism
  - User activation status

### 4. **Event Management (eventmanagement)**
- **Database**: MongoDB (event-service)
- **Role**: Event CRUD operations
- **Features**:
  - Create, read, update, delete events
  - Public & private (invitation-only) events
  - Filter events not attended by user
  - Query events user is attending
  - Calls Attendees service for attendance verification

### 5. **Attendees Service (attendees)**
- **Database**: PostgreSQL (event_proj)
- **Role**: Event registration & RSVP management
- **Features**:
  - Register users for events
  - Track RSVP status (pending/accepted/declined)
  - Get events not attended by user
  - Get events being attended by user

## 🔌 API Endpoints

### Authentication
```
POST   /usermanagement/auth/register     - Register new user
POST   /usermanagement/auth/login        - Authenticate user
POST   /usermanagement/auth/refresh      - Refresh access token
POST   /usermanagement/auth/logout       - Logout user
```

### User Management
```
GET    /usermanagement/user/{userId}     - Get user profile
POST   /usermanagement/user              - Create user
PUT    /usermanagement/user              - Update user profile
```

### Event Management
```
GET    /eventmanagement/events           - Get all events
POST   /eventmanagement/events           - Create event
GET    /eventmanagement/events/{id}      - Get event by ID
PUT    /eventmanagement/events/{id}      - Update event
DELETE /eventmanagement/events/{id}      - Delete event
GET    /eventmanagement/events/user/{userId}/not-attended
GET    /eventmanagement/events/user/{userId}/attending
```

### Attendees
```
POST   /attendees/register               - Register for event
GET    /attendees/getAllEventsNotAttended/{userId}
GET    /attendees/getAllAttendingEvents/{userId}
PUT    /attendees/updateRsvp             - Update RSVP status
```

## 🔐 Authentication Flow

1. **User Registration**: POST to `/usermanagement/auth/register`
2. **User Login**: POST to `/usermanagement/auth/login` → Returns JWT token
3. **Token in Requests**: Include `Authorization: Bearer <token>` header
4. **API Gateway**: AuthFilter validates JWT before routing
5. **Token Refresh**: POST to `/usermanagement/auth/refresh` before expiry
6. **Logout**: POST to `/usermanagement/auth/logout`

**Token Configuration**:
- Access Token TTL: 15 minutes
- Refresh Token TTL: 24 hours
- Algorithm: HS256 (HMAC with SHA-256)

## 🔄 Inter-Service Communication

Services communicate via **Spring WebClient** (non-blocking):

- **Event Management** calls **Attendees Service** for attendance data
- Example: `http://attendees/attendees/getAllEventsNotAttended/{userId}`
- Service-to-service calls use service names (resolved via Eureka)
- Load balancing handled by Eureka client-side discovery

## 🛠️ Configuration

### Database Configuration
Update `application.properties` in each service:

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/database_name
spring.datasource.username=postgres
spring.datasource.password=password

# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/database_name
```

### Eureka Configuration
```properties
eureka.client.service-url.defaultZone=http://localhost:8761/eureka
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
```

### JWT Configuration
Update `application.properties`:
```properties
jwt.secret=your_secret_key
jwt.expiration=900000         # 15 minutes
jwt.refreshExpiration=86400000 # 24 hours
```

## 📊 Database Schema

### PostgreSQL - User Management (event_userdb)
```
UserInfo:
  - userId (UUID, Primary Key)
  - userName (String)
  - firstName (String)
  - lastName (String)
  - email (String)
  - mobile (String)
  - password (String)
  - isUserActive (Boolean)
  - createDate (Timestamp)

AuthTable:
  - userId (Foreign Key)
  - refreshToken (String)
  - tokenExpiryDate (Timestamp)
```

### PostgreSQL - Attendees (event_proj)
```
EVENT_ATTENDEES_DETAILS:
  - eventId (String, Composite PK)
  - userId (String, Composite PK)
  - userName (String)
  - rsvpStatus (Enum: PENDING, ACCEPTED, DECLINED)
  - userComments (String)
```

### MongoDB - Event Management (event-service)
```
Event Document:
  - _id (ObjectId)
  - eventId (String)
  - eventName (String)
  - description (String)
  - location (String)
  - startDate (Date)
  - endDate (Date)
  - userId (String)
  - isPrivateInvite (Boolean)
```

## 🧪 Testing

Run unit tests:
```bash
mvn test
```

Run integration tests with TestContainers:
```bash
mvn verify
```

## 📝 Environment Variables

Create a `.env` file in the root:
```
POSTGRES_USER=postgres
POSTGRES_PASSWORD=password
POSTGRES_DB_USERDB=event_userdb
POSTGRES_DB_PROJ=event_proj
MONGODB_URI=mongodb://localhost:27017
JWT_SECRET=your_jwt_secret_key
```

## 🤝 Contributing

1. Create a feature branch: `git checkout -b feature/your-feature`
2. Commit changes: `git commit -m 'Add your feature'`
3. Push to branch: `git push origin feature/your-feature`
4. Open a Pull Request

## 📚 Design Patterns Used

- **Microservices Pattern**: Independent, deployable services
- **Service Discovery Pattern**: Netflix Eureka for dynamic discovery
- **API Gateway Pattern**: Central routing with Spring Cloud Gateway
- **Database per Service**: Polyglot persistence (SQL + NoSQL)
- **JWT Authentication**: Stateless security tokens
- **Non-blocking I/O**: Spring WebFlux for async operations
- **Circuit Breaker**: WebClient with timeout configuration

## 🚨 Common Issues & Solutions

### Services Not Registering with Eureka
- Ensure Eureka Server is running on port 8761
- Check `eureka.client.service-url.defaultZone` configuration
- Verify network connectivity between services

### Database Connection Errors
- Verify PostgreSQL/MongoDB are running
- Check database credentials in `application.properties`
- Ensure databases and tables/collections exist

### JWT Token Validation Fails
- Ensure token includes `Bearer` prefix
- Check JWT secret matches across services
- Verify token hasn't expired

### Port Already in Use
- Change port in `application.properties`
- Or kill existing process: `lsof -ti:port | xargs kill -9`

## 📖 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Netflix Eureka](https://github.com/Netflix/eureka)
- [Spring Cloud Gateway](https://spring.io/projects/spring-cloud-gateway)

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 👤 Author

Created as a distributed event management system showcasing modern microservices architecture patterns.

---

**Happy Coding! 🚀**
