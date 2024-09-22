#!/bin/bash

# OCI 설정 파일 생성
echo "[DEFAULT]" > /root/.oci/config
echo "user=${OCI_USER}" >> /root/.oci/config
echo "fingerprint=${OCI_FINGERPRINT}" >> /root/.oci/config
echo "${OCI_API_KEY}" > /root/.oci/private_key.pem
chmod 600 /root/.oci/private_key.pem
echo "tenancy=${OCI_TENANCY}" >> /root/.oci/config
echo "region=${OCI_REGION}" >> /root/.oci/config

# fluent.conf 파일 생성
echo "<source>" > /fluentd/etc/fluent.conf
echo "  @type forward" >> /fluentd/etc/fluent.conf
echo "  port 24224" >> /fluentd/etc/fluent.conf
echo "  bind 0.0.0.0" >> /fluentd/etc/fluent.conf
echo "</source>" >> /fluentd/etc/fluent.conf

echo "<match **>" >> /fluentd/etc/fluent.conf
echo "  @type oci_logging" >> /fluentd/etc/fluent.conf
echo "  compartment_id ${COMPARTMENT_ID}" >> /fluentd/etc/fluent.conf
echo "  log_id ${LOG_ID}" >> /fluentd/etc/fluent.conf
echo "  oci_config_file /root/.oci/config" >> /fluentd/etc/fluent.conf
echo "</match>" >> /fluentd/etc/fluent.conf

# Fluentd 실행
fluentd -c /fluentd/etc/fluent.conf
