# Testing Guide - LB Email Management Platform

## 🚀 Quick Start for Local Testing

### Prerequisites
1. **Java 21** installed
2. **Maven 3.6+** installed
3. **Outlook/Exchange credentials** (for email integration)

### Setup Instructions

1. **Configure Email Credentials**
   
   Set environment variables for your Outlook credentials:
   ```bash
   export MAIL_USERNAME="your-email@company.com"
   export MAIL_PASSWORD="your-app-password"
   ```
   
   Or update `src/main/resources/application.yml`:
   ```yaml
   app:
     email:
       username: your-email@company.com
       password: your-app-password
   ```

2. **Start the Application**
   ```bash
   mvn spring-boot:run
   ```

3. **Verify Startup**
   
   You should see these logs indicating successful initialization:
   ```
   🚀 Initializing test agents...
   ✅ Initialized 2 test agents
   👤 Agent 1: John Smith (AGENT001) - Skills: [GENERAL_INQUIRY]
   👤 Agent 2: Jane Doe (AGENT002) - Skills: [BILLING_SUPPORT]
   ```

## 📧 Testing Email Flow

### Option 1: Simulate Email (No Real Email Required)

**Simulate an incoming email:**
```bash
curl -X POST "http://localhost:8080/api/test/simulate-email?fromEmail=customer@test.com&subject=Need%20Help&content=I%20need%20assistance"
```

**Expected Console Output:**
```
🎬 Simulating incoming email from: customer@test.com
📧 Test email saved: TEST-1234567890
📋 Creating case from email: Need Help
📋 Case persisted: CASE-2024-ABCD1234 with email: TEST-1234567890
📊 SLA tracking initialized for case: CASE-2024-ABCD1234 - Target: 24h first response, 48h resolution
🎯 Case CASE-2024-ABCD1234 added to GENERAL_INQUIRY queue with priority 75
✅ Case added to queue successfully
✅ Case created successfully: CASE-2024-ABCD1234
```

### Option 2: Real Email Integration (Advanced)

**Note**: For real email integration, you need to:
1. Set up an App Password in your Outlook account
2. Update the credentials in application.yml
3. Send an email to your configured email address

The system will poll for new emails every 30 seconds and process them automatically.

## 🎯 Testing Agent Workflow

### Check Agent Status
```bash
curl "http://localhost:8080/api/test/agent-status"
```

**Sample Response:**
```
Agent AGENT001 (John Smith): AVAILABLE - Cases: 0/3
Agent AGENT002 (Jane Doe): AVAILABLE - Cases: 0/5
```

### Check Queue Status
```bash
curl "http://localhost:8080/api/test/queue-status"
```

**Sample Response:**
```
Queue GENERAL_INQUIRY: 1 pending cases
Queue BILLING_SUPPORT: 0 pending cases
```

### Get Next Case for Agent
```bash
curl "http://localhost:8080/api/agents/AGENT001/next-case?queueType=GENERAL_INQUIRY"
```

**Expected Console Output:**
```
🔄 Getting next case for agent AGENT001 from queue GENERAL_INQUIRY
🎯 Assigned case CASE-2024-ABCD1234 to agent AGENT001
```

### Agent Responds to Customer
```bash
curl -X POST "http://localhost:8080/api/agents/AGENT001/cases/CASE-2024-ABCD1234/respond" \
  -H "Content-Type: application/json" \
  -d '{
    "toAddress": "customer@test.com",
    "subject": "Re: Need Help",
    "content": "Thank you for contacting us. We are looking into your request.",
    "priority": "NORMAL"
  }'
```

**Expected Console Output:**
```
📤 Sending email: Re: Need Help to customer@test.com
✅ Email sent successfully: Re: Need Help
⏱️ First response recorded for case CASE-2024-ABCD1234: 15 minutes
✅ Response sent successfully
```

## 🕐 Agent Rotation (60-second cycle)

The system automatically rotates agent availability every 60 seconds:

**Expected Console Output (every 60 seconds):**
```
🔄 Rotating agent availability...
🔴 Agent AGENT001 is now BUSY
🟢 Agent AGENT002 is now AVAILABLE
📊 Current Agent Status:
   👤 John Smith (AGENT001): BUSY - Cases: 1/3 - Skills: [GENERAL_INQUIRY]
   👤 Jane Doe (AGENT002): AVAILABLE - Cases: 0/5 - Skills: [BILLING_SUPPORT]
```

## 📊 Monitoring SLA Metrics

### Get SLA Status for Cases Approaching Breach
```bash
curl "http://localhost:8080/api/sla/approaching-breach"
```

### Get SLA Metrics for Date Range
```bash
curl "http://localhost:8080/api/sla/metrics?startDate=2024-01-01T00:00:00&endDate=2024-12-31T23:59:59"
```

## 📱 Available API Endpoints

### Test Endpoints
- `POST /api/test/simulate-email` - Create test email
- `GET /api/test/agent-status` - View agent status
- `GET /api/test/queue-status` - View queue depths

### Agent Operations
- `GET /api/agents/{agentId}/next-case?queueType=GENERAL_INQUIRY` - Get next case
- `POST /api/agents/{agentId}/cases/{caseNumber}/respond` - Send email response
- `GET /api/agents/{agentId}/cases` - View assigned cases

### Queue Management
- `GET /api/queues/{queueType}/depth` - Get queue depth
- `GET /api/queues/{queueType}/metrics` - Get queue metrics

### SLA Monitoring
- `GET /api/sla/approaching-breach` - Cases approaching SLA breach
- `GET /api/sla/breached` - SLA-breached cases
- `GET /api/sla/metrics` - SLA compliance metrics

## 🎯 Complete Test Scenario

1. **Start Application**: `mvn spring-boot:run`

2. **Create Test Email**: 
   ```bash
   curl -X POST "http://localhost:8080/api/test/simulate-email"
   ```

3. **Wait for Agent Rotation** (or check immediately if agent is available)

4. **Agent Gets Case**:
   ```bash
   curl "http://localhost:8080/api/agents/AGENT001/next-case?queueType=GENERAL_INQUIRY"
   ```

5. **Agent Responds**:
   ```bash
   curl -X POST "http://localhost:8080/api/agents/AGENT001/cases/CASE-2024-ABCD1234/respond" \
     -H "Content-Type: application/json" \
     -d '{"toAddress": "customer@test.com", "subject": "Re: Test", "content": "We received your request"}'
   ```

6. **Monitor Logs** - You should see:
   - ✅ Email received and case created
   - 🎯 Case assigned to available agent
   - 📤 Response email sent
   - ⏱️ SLA first response recorded
   - 📊 SLA tracking updated

## 🔧 Troubleshooting

### Email Authentication Issues
- Ensure you're using an **App Password**, not your regular password
- Enable 2FA on your Outlook account first
- Check firewall settings for SMTP/IMAP access

### Database Issues
- H2 database runs in-memory - data resets on restart
- Access H2 console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`, User: `sa`, Password: `password`

### Compilation Issues
- Ensure Java 21 is being used: `java -version`
- Clean rebuild: `mvn clean compile`

## 📈 Expected Performance Logs

```
📧 Found 1 new emails to process
📨 Processing new email: 'Customer Support Request' from customer@test.com
💾 Email saved to database: TEST-1234567890
📋 Case created: CASE-2024-ABCD1234 for email: Customer Support Request
🎯 Case CASE-2024-ABCD1234 added to GENERAL_INQUIRY queue with priority 75
📊 SLA tracking initialized for case: CASE-2024-ABCD1234
🔄 Rotating agent availability...
🟢 Agent AGENT001 is now AVAILABLE
🎯 Getting next case for agent AGENT001 from queue GENERAL_INQUIRY
🎯 Assigned case CASE-2024-ABCD1234 to agent AGENT001
📤 Sending email: Re: Customer Support Request to customer@test.com
✅ Email sent successfully: Re: Customer Support Request
⏱️ First response recorded for case CASE-2024-ABCD1234: 5 minutes
📋 Case updated successfully: CASE-2024-ABCD1234
```

This setup provides a complete email management workflow without requiring AWS infrastructure, perfect for POC testing!