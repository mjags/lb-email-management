# LB Email Management Platform

A Spring Boot application for managing email-based customer support cases in call center environments. This platform provides case management, work queue distribution, and SLA tracking for email communications.

## 🚀 Features

- **Email Processing**: Integration with Exchange Server for email ingestion and sending
- **Case Management**: Automatic case creation from incoming emails with full lifecycle tracking
- **Work Queue System**: Intelligent email distribution to available agents based on skills and workload
- **SLA Tracking**: First response time and resolution time monitoring with breach alerts
- **Agent Management**: Workload balancing and availability tracking
- **Multi-Queue Support**: Separate queues for different email types (General Inquiry, Billing Support)

## 🏗️ Architecture

### Current Implementation (POC)
- **Backend**: Spring Boot 3.2 with Java 21
- **Database**: H2 (development) / DynamoDB (production)
- **Email**: Microsoft Graph API for Exchange Server integration
- **Cloud**: AWS services (DynamoDB, potentially SQS/SNS for notifications)

### Scaling Considerations (300K emails/month, 450 agents)
- **Microservices Architecture**: Split into separate services
- **Event-Driven Design**: Use AWS SQS/SNS for async processing
- **Caching Layer**: Redis for frequently accessed data
- **Load Balancing**: Multiple application instances
- **Database Optimization**: DynamoDB with proper GSI design

## 📋 Prerequisites

- Java 21
- Maven 3.6+
- AWS CLI (for production deployment)
- Docker (for LocalStack development)

## 🚀 Quick Start

### Development Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd lb-email-management
   ```

2. **Run with H2 Database (Development)**
   ```bash
   mvn spring-boot:run
   ```

3. **Run with LocalStack (AWS Simulation)**
   ```bash
   # Start LocalStack
   docker run -d -p 4566:4566 localstack/localstack
   
   # Run application with dev profile
   mvn spring-boot:run -Dspring.profiles.active=dev
   ```

### Configuration

Update `application.yml` with your environment-specific values:

```yaml
# Email Configuration
spring:
  mail:
    username: your-exchange-username
    password: your-exchange-password

# AWS Configuration
aws:
  region: your-aws-region
```

## 📊 API Endpoints

### Agent Operations
- `POST /api/agents/{agentId}/status` - Update agent availability
- `GET /api/agents/{agentId}/next-case` - Get next case from queue
- `GET /api/agents/{agentId}/cases` - Get assigned cases
- `POST /api/agents/{agentId}/cases/{caseNumber}/respond` - Send email response
- `POST /api/agents/{agentId}/cases/{caseNumber}/resolve` - Resolve case

### Queue Management
- `GET /api/queues/{queueType}/depth` - Get queue depth
- `GET /api/queues/{queueType}/metrics` - Get queue performance metrics
- `POST /api/queues/redistribute` - Manually redistribute cases

### SLA Monitoring
- `GET /api/sla/metrics` - Get SLA compliance metrics
- `GET /api/sla/approaching-breach` - Get cases approaching SLA breach
- `GET /api/sla/breached` - Get SLA-breached cases

## 🏢 Work Queue Types

1. **GENERAL_INQUIRY**: General customer inquiries and support requests
2. **BILLING_SUPPORT**: Billing-related questions and payment issues

## 📈 Scaling to Production (300K emails/month)

### 1. Microservices Architecture

**Recommended Service Split:**

```
├── email-ingestion-service      # Email processing and parsing
├── case-management-service      # Case lifecycle management  
├── work-queue-service          # Queue distribution and assignment
├── sla-tracking-service        # SLA monitoring and reporting
├── notification-service        # Agent notifications and alerts
├── agent-management-service    # Agent status and workload
└── api-gateway                 # Single entry point and routing
```

### 2. Infrastructure Recommendations

**AWS Services:**
- **API Gateway**: Request routing and rate limiting
- **ECS/EKS**: Container orchestration for microservices
- **DynamoDB**: Primary data store with auto-scaling
- **SQS**: Message queuing for async processing
- **SNS**: Push notifications to agents
- **Lambda**: Serverless functions for lightweight processing
- **ElastiCache (Redis)**: Caching layer for performance
- **CloudWatch**: Monitoring and alerting

### 3. Database Design (DynamoDB)

**Table Structure:**
```
Cases Table:
- PK: CASE#{caseNumber}
- SK: METADATA
- GSI1: STATUS#{status}
- GSI2: AGENT#{agentId}

Emails Table:
- PK: EMAIL#{messageId}
- SK: TIMESTAMP#{receivedDate}
- GSI1: CASE#{caseNumber}

WorkQueue Table:
- PK: QUEUE#{queueType}
- SK: PRIORITY#{score}#TIMESTAMP#{addedTime}
```

### 4. Performance Optimizations

**Email Processing:**
- Batch processing with SQS for high throughput
- Parallel processing of distribution lists
- Intelligent email classification using ML

**Queue Management:**
- Predictive agent assignment based on historical data
- Dynamic priority scoring
- Load balancing across agent skills

**SLA Monitoring:**
- Real-time SLA calculation with DynamoDB Streams
- Proactive breach notifications
- Automated escalation workflows

### 5. Monitoring & Observability

**Key Metrics:**
- Email processing throughput (emails/hour)
- Queue depth and wait times
- Agent utilization rates
- SLA compliance percentages
- System response times

**Tools:**
- AWS CloudWatch for application metrics
- AWS X-Ray for distributed tracing
- Custom dashboards for business metrics

## 🔧 Configuration Management

### Environment Variables

```bash
# Database
DATABASE_URL=your-production-db-url
DATABASE_USERNAME=your-db-username
DATABASE_PASSWORD=your-db-password

# AWS
AWS_REGION=us-east-1
DYNAMODB_ENDPOINT=https://dynamodb.us-east-1.amazonaws.com

# Email
MAIL_USERNAME=your-exchange-username
MAIL_PASSWORD=your-exchange-password

# Application
SPRING_PROFILES_ACTIVE=prod
```

## 🧪 Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn test -Dtest=**/*IntegrationTest

# Run with coverage
mvn test jacoco:report
```

## 📦 Deployment

### Docker Deployment
```bash
# Build image
docker build -t lb-email-management .

# Run container
docker run -p 8080:8080 lb-email-management
```

### AWS Deployment
```bash
# Deploy with AWS CDK/CloudFormation
aws cloudformation deploy --template-file infrastructure.yaml --stack-name lb-email-management
```

## 🔒 Security Considerations

- **Authentication**: Integration with corporate SSO
- **Authorization**: Role-based access control for agents
- **Data Encryption**: TLS in transit, encryption at rest
- **Audit Logging**: Complete audit trail for compliance
- **Rate Limiting**: API throttling to prevent abuse

## 📝 Future Enhancements

1. **AI/ML Integration**: 
   - Automatic email classification
   - Sentiment analysis for priority scoring
   - Response suggestion engine

2. **Advanced Routing**:
   - Customer tier-based routing
   - Language-based agent assignment
   - Skill-based routing algorithms

3. **Analytics Dashboard**:
   - Real-time performance metrics
   - Predictive analytics for capacity planning
   - Customer satisfaction tracking

4. **Integration Capabilities**:
   - CRM system integration
   - Knowledge base integration
   - Telephony system integration

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For support and questions:
- Create an issue in this repository
- Contact the development team at dev-team@company.com

---

**Note**: This is a POC implementation. Production deployment requires additional security, monitoring, and scalability considerations as outlined in the scaling section above.