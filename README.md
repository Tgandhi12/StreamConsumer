# Oracle GoldenGate Stream Consumer

Enterprise-style Java 17 consumer for Oracle GoldenGate Data Stream Service. The application connects to GoldenGate through WebSocket, receives CDC events, parses JSON safely, filters DML events, and processes `INSERT`, `UPDATE`, and `DELETE` operations.

## What was improved

- Added **AsyncAPI contract** at `docs/asyncapi.yaml`.
- Removed hardcoded production-style credentials from code.
- Added external configuration using `application.properties`, environment variables, and JVM arguments.
- Added structured package layout: `config`, `model`, `parser`, `service`, and `websocket`.
- Replaced `System.out.println()` with **SLF4J + Logback** logging.
- Replaced unsafe `message.contains()` checks with **Jackson JSON parsing**.
- Added reconnect retry logic with exponential backoff.
- Added Maven Shade plugin to generate a runnable fat JAR.

## Project Structure

```text
src/main/java/com/example/stream
├── StreamConsumerApplication.java
├── config
│   └── StreamConfig.java
├── model
│   ├── CdcEvent.java
│   └── OperationType.java
├── parser
│   └── CdcEventParser.java
├── service
│   └── EventProcessor.java
└── websocket
    ├── GoldenGateStreamRunner.java
    └── GoldenGateWebSocketClient.java

src/main/resources
├── application.properties
└── logback.xml

docs
└── asyncapi.yaml
```

## Configuration

Default values are present in `src/main/resources/application.properties`, but credentials should be passed using environment variables.

### Windows PowerShell

```powershell
$env:OGG_STREAM_URL="ws://192.168.56.161:7803/services/v2/stream/oggstream?begin=now"
$env:OGG_USER="oggadmin"
$env:OGG_PASSWORD="your-password"
```

### Linux / macOS

```bash
export OGG_STREAM_URL="ws://192.168.56.161:7803/services/v2/stream/oggstream?begin=now"
export OGG_USER="oggadmin"
export OGG_PASSWORD="your-password"
```

You can also override using JVM arguments:

```bash
java -Dogg.stream.url="ws://host:7803/services/v2/stream/oggstream?begin=now" \
     -Dogg.username="oggadmin" \
     -Dogg.password="your-password" \
     -jar target/GoldenGateStreamConsumer-1.0.0.jar
```

## Build

```bash
mvn clean package
```

## Run

```bash
java -jar target/GoldenGateStreamConsumer-1.0.0.jar
```

## AsyncAPI

The AsyncAPI file documents the asynchronous contract of the GoldenGate CDC stream.

```text
docs/asyncapi.yaml
```

It describes:

- WebSocket server
- Basic authentication
- CDC event channel
- INSERT / UPDATE / DELETE event schema
- Example payloads

## Current Processing Flow

```text
Oracle GoldenGate Data Stream
        ↓
WebSocket Client
        ↓
CDC JSON Parser
        ↓
Event Processor
        ↓
Logs / Future Kafka / Future Database / Future Dashboard
```

## Enterprise Extension Points

The `EventProcessor` class is the best place to add enterprise integrations such as:

- Kafka publishing
- Audit table persistence
- Dead-letter queue
- Monitoring metrics
- Dashboard updates
- Alerting workflow
