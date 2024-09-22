#!/bin/bash

# 환경 변수 확인
if [ -z "$OCI_USER" ] || [ -z "$OCI_FINGERPRINT" ] || [ -z "$OCI_API_KEY" ] || \
   [ -z "$OCI_TENANCY" ] || [ -z "$OCI_REGION" ] || [ -z "$COMPARTMENT_ID" ] || \
   [ -z "$LOG_ID" ] || [ -z "$OCI_CONFIG_FILE" ]; then
  echo "Required environment variables are missing."
  exit 1
fi

# 네트워크 생성 (이미 존재할 경우 에러 무시)
sudo docker network create log-network || true

# custom-fluentd 컨테이너 확인
CUSTOM_FLUENTD_CONTAINER=$(sudo docker ps -q -f name=custom-fluentd)
if [ -n "$CUSTOM_FLUENTD_CONTAINER" ]; then
  echo "Custom Fluentd container is already running. Skipping creation."
else
  echo "Custom Fluentd container not found. Running new container..."
  sudo docker run -d --name custom-fluentd --network log-network -p 24224:24224 fluentd-custom:latest
  sudo docker run --network log-network -d \
    -e OCI_USER="$OCI_USER" \
    -e OCI_FINGERPRINT="$OCI_FINGERPRINT" \
    -e OCI_API_KEY="$OCI_API_KEY" \
    -e OCI_TENANCY="$OCI_TENANCY" \
    -e OCI_REGION="$OCI_REGION" \
    -e COMPARTMENT_ID="$COMPARTMENT_ID" \
    -e LOG_ID="$LOG_ID" \
    -e OCI_CONFIG_FILE="$OCI_CONFIG_FILE" \
    fluentd-image
fi
