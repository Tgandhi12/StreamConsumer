# Oracle GoldenGate CDC Stream Consumer with AsyncAPI Validation

## Overview

This project is a real-time Change Data Capture (CDC) consumer built using Java 17 and Oracle GoldenGate Data Streams.

The application consumes CDC events published by Oracle GoldenGate through a WebSocket connection, validates them against an AsyncAPI contract, parses them into domain objects, and processes them through configurable event handlers.

The solution is designed using a contract-first architecture and includes enterprise-grade features such as:

* AsyncAPI-based event validation
* Real-time WebSocket consumption
* CDC event parsing
* Event processing pipeline
* Dead Letter Queue (DLQ)
* Checkpointing and recovery support
* Metrics collection
* Reconnection handling
* Unit testing
* Future compatibility with Oracle GoldenGate 23ai Data Streams Code Generator architecture

---

# Architecture

```text
Oracle Database
       │
       ▼
Oracle GoldenGate
       │
       ▼
GoldenGate Data Streams
       │
       ▼
GoldenGateWebSocketClient
       │
       ▼
AsyncApiValidator
       │
       ▼
CdcEventParser
       │
       ▼
EventProcessor
       │
 ┌─────┼─────────────┐
 ▼     ▼             ▼
Log   Audit       Kafka
Handler Handler  Handler
       │
       ▼
CheckpointStore

Invalid Events
       │
       ▼
DeadLetterQueue
```

---

# Features

## Real-Time CDC Consumption

Consumes INSERT, UPDATE, and DELETE operations from Oracle GoldenGate Data Streams using WebSocket connectivity.

Supported operations:

```text
INSERT
UPDATE
DELETE
```

---

## AsyncAPI Validation

Incoming CDC messages are validated against an AsyncAPI contract before processing.

Validation includes:

* Required fields
* Operation types
* Message structure
* JSON integrity

Example:

```json
{
  "table": "EMPLOYEE",
  "op_type": "INSERT"
}
```

---

## CDC Event Parsing

Raw CDC messages are converted into strongly typed Java objects.

Example:

```java
CdcEvent
```

Fields extracted:

```text
table
operationType
transactionId
position
timestamp
before
after
rawPayload
```

---

## Event Processing Pipeline

The application uses an Event Handler architecture to support multiple processing targets.

Current handlers:

* LoggingEventHandler
* AuditEventHandler
* KafkaEventHandler (placeholder)

New handlers can be added without modifying the core processing pipeline.

---

## Dead Letter Queue (DLQ)

Invalid CDC messages are redirected to a Dead Letter Queue.

Output file:

```text
failed-events.log
```

Benefits:

* Auditing
* Troubleshooting
* Reprocessing

---

## Checkpointing

The application stores processing checkpoints to support recovery.

Checkpoint file:

```text
checkpoint.dat
```

Stores:

```text
Transaction ID
```

Benefits:

* Recovery support
* Future resume-from-offset capability

---

## Metrics Collection

Tracks:

```text
Events Received
Events Processed
Validation Failures
```

Useful for:

* Monitoring
* Performance analysis
* Operational visibility

---

## Automatic Reconnection

Supports configurable reconnect behavior using exponential backoff.

Example:

```text
Attempt 1 → 1 second
Attempt 2 → 2 seconds
Attempt 3 → 4 seconds
Attempt 4 → 8 seconds
```

---

# Project Structure

```text
src
│
├── main
│   ├── java
│   │   └── com.example.stream
│   │
│   ├── config
│   │   └── StreamConfig.java
│   │
│   ├── model
│   │   ├── CdcEvent.java
│   │   └── OperationType.java
│   │
│   ├── parser
│   │   └── CdcEventParser.java
│   │
│   ├── validation
│   │   └── AsyncApiValidator.java
│   │
│   ├── websocket
│   │   ├── GoldenGateWebSocketClient.java
│   │   └── GoldenGateStreamRunner.java
│   │
│   ├── service
│   │   ├── EventProcessor.java
│   │   ├── EventHandler.java
│   │   ├── LoggingEventHandler.java
│   │   ├── AuditEventHandler.java
│   │   └── KafkaEventHandler.java
│   │
│   ├── checkpoint
│   │   └── CheckpointStore.java
│   │
│   ├── dlq
│   │   └── DeadLetterQueue.java
│   │
│   ├── metrics
│   │   └── StreamMetrics.java
│   │
│   ├── transport
│   │   ├── GeneratedStreamClient.java
│   │   ├── StreamMessageConsumer.java
│   │   ├── GoldenGateGeneratedAdapter.java
│   │   └── generated
│   │       └── OracleGeneratedClientWrapper.java
│   │
│   └── StreamConsumerApplication.java
│
├── resources
│   ├── application.properties
│   ├── asyncapi.yaml
│   └── streaming.yaml
│
└── test
    ├── AsyncApiValidatorTest.java
    ├── CdcEventParserTest.java
    ├── CheckpointStoreTest.java
    └── EventProcessorTest.java
```

---

# Configuration

## application.properties

```properties
ogg.stream.url=ws://localhost:7803/services/v2/stream/oggstream?begin=now

ogg.username=oggadmin
ogg.password=password

ogg.reconnect.enabled=true
ogg.reconnect.max-attempts=10

ogg.reconnect.initial-delay-ms=1000
ogg.reconnect.max-delay-ms=30000

ogg.transport=websocket
```

---

# Running the Application

## Build

```bash
mvn clean package
```

---

## Run Tests

```bash
mvn test
```

---

## Start Consumer

```bash
java -jar target/GoldenGateStreamConsumer-1.0.0.jar
```

---

# Example CDC Event

```json
{
  "table": "EMPLOYEE",
  "op_type": "UPDATE",
  "txid": "TX12345",
  "before": {
    "NAME": "John"
  },
  "after": {
    "NAME": "John Smith"
  }
}
```

---

# AsyncAPI Integration

The project follows a contract-first architecture using AsyncAPI.

AsyncAPI defines:

* Channels
* Messages
* Payload Schemas
* Operations

The application validates incoming CDC events against the AsyncAPI definition before processing.

---

# Oracle GoldenGate 23ai Data Streams Alignment

Oracle GoldenGate 23ai introduces Data Streams Code Generator support.

Current Oracle support:

* Python Client Generation
* Node.js Client Generation

Java generation is currently under development.

To support future integration, this project includes:

```text
GeneratedStreamClient
StreamMessageConsumer
GoldenGateGeneratedAdapter
OracleGeneratedClientWrapper
```

These abstraction layers allow generated clients to be integrated without modifying the validation, parsing, or processing logic.

---

# Testing

The project includes JUnit 5 tests covering:

## AsyncApiValidatorTest

Tests:

* Valid CDC events
* Invalid operation types
* Missing required fields
* Invalid JSON

## CdcEventParserTest

Tests:

* CDC event parsing
* Operation extraction
* Transaction metadata extraction

## CheckpointStoreTest

Tests:

* Save checkpoint
* Load checkpoint

## EventProcessorTest

Tests:

* Handler invocation
* Processing pipeline

---

# Future Enhancements

* Kafka integration
* Prometheus metrics
* Grafana dashboards
* Docker deployment
* Database-backed checkpointing
* Resume from GoldenGate offsets
* Oracle-generated Java client support
* OpenTelemetry tracing

---

# Technologies Used

* Java 17
* Maven
* Oracle GoldenGate Data Streams
* Java WebSocket
* Jackson
* SnakeYAML
* AsyncAPI
* SLF4J
* Logback
* JUnit 5

---

# Design Principles

* Contract-First Architecture
* Separation of Concerns
* SOLID Principles
* Event-Driven Design
* Extensibility
* Fault Tolerance
* Future Compatibility with Oracle Data Streams Code Generator

---

