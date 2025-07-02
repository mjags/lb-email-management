# LB Email Management - Project Structure

## 📁 Project Organization

```
lb-email-management/
├── README.md                    # Main project documentation
├── TESTING.md                   # Local testing instructions
├── PROJECT_STRUCTURE.md         # This file
├── .gitignore                   # Git ignore rules
├── pom.xml                      # Maven configuration
└── src/
    └── main/
        ├── java/
        │   └── com/callcenter/emailmanagement/
        │       ├── EmailManagementApplication.java  # Spring Boot main class
        │       ├── controller/                      # REST API controllers
        │       │   ├── AgentController.java         # Agent operations
        │       │   ├── QueueController.java         # Queue management
        │       │   ├── SlaController.java           # SLA monitoring
        │       │   └── TestController.java          # Testing endpoints
        │       ├── domain/model/                    # Domain entities
        │       │   ├── Agent.java                   # Agent entity
        │       │   ├── Case.java                    # Case entity
        │       │   ├── Email.java                   # Email entity
        │       │   ├── SlaTracking.java             # SLA tracking entity
        │       │   ├── WorkQueue.java               # Work queue entity
        │       │   └── WorkQueueType.java           # Queue type enum
        │       ├── repository/                      # Data access layer
        │       │   ├── AgentRepository.java         # Agent data access
        │       │   ├── CaseRepository.java          # Case data access
        │       │   ├── EmailRepository.java         # Email data access
        │       │   ├── SlaTrackingRepository.java   # SLA data access
        │       │   └── WorkQueueRepository.java     # Queue data access
        │       └── service/                         # Business logic
        │           ├── AgentRotationService.java    # Agent availability rotation
        │           ├── CaseManagementService.java   # Case lifecycle management
        │           ├── EmailPollingService.java     # Email polling scheduler
        │           ├── EmailService.java            # Email processing
        │           ├── OutlookEmailService.java     # Outlook integration
        │           ├── SlaTrackingService.java      # SLA monitoring
        │           └── WorkQueueService.java        # Queue management
        └── resources/
            └── application.yml                      # Spring Boot configuration
```

## 🚀 Getting Started

### Prerequisites
- Java 21
- Maven 3.6+
- Outlook/Exchange email credentials

### Quick Start
1. **Navigate to project directory:**
   ```bash
   cd /Users/jmunukoti/source/lb-email-management
   ```

2. **Set email credentials:**
   ```bash
   export MAIL_USERNAME="your-email@company.com"
   export MAIL_PASSWORD="your-app-password"
   ```

3. **Compile and run:**
   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

4. **Test the system:**
   ```bash
   curl -X POST "http://localhost:8080/api/test/simulate-email"
   ```

## 📊 Key Features

✅ **Email Processing**: Real Outlook integration with JavaMail  
✅ **Case Management**: Automatic case creation and lifecycle tracking  
✅ **Work Queues**: Two queue types (General Inquiry, Billing Support)  
✅ **Agent Management**: Auto-rotation every 60 seconds  
✅ **SLA Tracking**: 24h first response, 48h resolution monitoring  
✅ **H2 Database**: In-memory database for local testing  
✅ **REST APIs**: Complete API set for all operations  
✅ **Console Logging**: Emoji-based logging for easy tracking  

## 🧪 Testing

See `TESTING.md` for comprehensive testing instructions including:
- Email simulation endpoints
- Agent workflow testing
- SLA monitoring
- Queue management
- Real email integration setup

## 📈 Scaling

See `README.md` for detailed scaling recommendations for production deployment with 300K emails/month and 450 agents.

## 🏗️ Architecture

- **Spring Boot 3.2** with Java 21
- **H2 Database** for local development  
- **JPA Repositories** for data access
- **Scheduled Services** for automation
- **RESTful APIs** for all operations
- **JavaMail** for email integration

This is a complete, self-contained project ready for POC testing and production scaling!