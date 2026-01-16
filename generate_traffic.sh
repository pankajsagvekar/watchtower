#!/bin/bash

BASE_URL="http://localhost:8080"

echo "Running Watchtower Verification Script..."
echo "========================================="

# 1. Check Health
echo "[1] Checking Health..."
curl -s "$BASE_URL/actuator/health" | grep "UP" > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ Application is UP"
else
    echo "❌ Application is DOWN or not accessible. Make sure it's running!"
    exit 1
fi

# 2. Generate Baseline Traffic (20 fast requests)
echo ""
echo "[2] Generating Baseline Traffic (20 fast requests to /test/fast)..."
for i in {1..20}; do
    curl -s "$BASE_URL/test/fast" > /dev/null
    echo -n "."
    # No sleep required for fast generation, but small delay is polite
    sleep 0.05
done
echo " Done!"

# 3. Generate Anomaly (1 slow request on SAME endpoint)
echo ""
echo "[3] Simulating Anomaly (1 slow request to /test/fast?delay=2000)..."
curl -s "$BASE_URL/test/fast?delay=2000"
echo ""

echo ""
echo "========================================="
echo "Traffic generation complete."
echo "Please wait ~60s for the scheduler to run."
echo "Then check logs or run: curl $BASE_URL/monitoring/anomalies"
