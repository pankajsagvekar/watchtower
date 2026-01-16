# Watchtower - AI-Driven API Monitoring

Watchtower is a conceptual backend system designed to monitor API latency, detect statistical anomalies in near real-time, and leverage LLMs (Groq) to provide root-cause analysis and explanations.

## 🚀 Features

- **Automated Metric Collection**: Intercepts every API request using a specialized `OncePerRequestFilter` to record execution time, status, and method.
- **Statistical Anomaly Detection**: A scheduled background job runs every minute to analyze recent traffic (last 5 minutes). It uses rolling average and standard deviation to identify outliers (Threshold = Avg + 2 * StdDev).
- **AI-Powered Analysis**: When an anomaly is detected, the system sends the metric data (Observed Latency vs Baseline) to the **Groq API** (`llama-3.3-70b-versatile`) to generate a human-readable root cause analysis.
- **Traffic Simulation**: Built-in endpoints to simulate fast, slow (variable latency), and error scenarios for testing.

## 🛠 Tech Stack

- **Java 17**
- **Spring Boot 3.2.1** (Web, Data JPA, Actuator, Quartz)
- **PostgreSQL** (Persistence)
- **Groq API** (LLM Integration)
- **Maven** (Build Tool)

## 📋 Prerequisites

1.  **PostgreSQL**: Ensure you have a running PostgreSQL instance.
    - Database Name: `watchtower`
    - Default Config assumes `postgres` user and `root` password (configurable via `.env`).
2.  **Groq API Key**: Get a free API key from [Groq Console](https://console.groq.com/).

## ⚙️ Configuration

1.  **Environment Variables**: Create a `.env` file in the project root (do not commit this file):
    ```env
    GROQ_API_KEY=your_actual_api_key_here
    SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/watchtower
    SPRING_DATASOURCE_USERNAME=postgres
    SPRING_DATASOURCE_PASSWORD=your_db_password
    ```

## ▶️ How to Run

1.  **Load Environment & Start**:
    Since we use a `.env` file, export variables before running Maven:
    ```bash
    export $(cat .env | xargs)
    ./mvnw spring-boot:run
    ```

## 🧪 Verification & Testing

### 1. Automated Traffic Script
We have included a script to generate baseline traffic and then trigger an anomaly.
```bash
./generate_traffic.sh
```
*This script sends 20 fast requests to establish a low baseline, then 1 slow request (2000ms delay) to trigger the detector.*

### 2. Manual Testing
You can manually hit the test endpoints:
- **Baseline**: `curl http://localhost:8080/test/fast` (Run ~10-20 times)
- **Trigger Anomaly**: `curl "http://localhost:8080/test/fast?delay=2000"` (Run 1 time)
- **View Report**: `curl http://localhost:8080/monitoring/anomalies`

### 3. Postman
Import the included `watchtower_postman_collection.json` into Postman for a GUI-based testing workflow.

## 📡 API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/test/fast` | Returns a fast response (or delayed via `?delay=ms`). |
| `GET` | `/test/slow` | Returns a hard-coded slow response (deprecated for anomaly testing). |
| `GET` | `/test/error` | Returns a 500 error. |
| `GET` | `/monitoring/anomalies` | Returns a list of all detected anomalies with AI analysis. |
| `GET` | `/monitoring/latest` | Returns the most recent anomaly. |
| `GET` | `/actuator/health` | Health check. |

## 📝 License
This project is for educational purposes.
