#!/bin/bash

# OCI 설정 파일 생성
echo "[DEFAULT]" > /root/.oci/config
echo "user=${OCI_USER}" >> /root/.oci/config
echo "fingerprint=${OCI_FINGERPRINT}" >> /root/.oci/config
echo "${OCI_API_KEY}" > /root/.oci/private_key.pem
chmod 600 /root/.oci/private_key.pem
echo "key_file=/root/.oci/private_key.pem" >> /root/.oci/config  # 키 파일 경로 추가
echo "tenancy=${OCI_TENANCY}" >> /root/.oci/config
echo "region=${OCI_REGION}" >> /root/.oci/config

# fluent.conf 파일 생성
echo "<source>" > /fluentd/etc/fluent.conf
echo "  @type tcp" >> /fluentd/etc/fluent.conf
echo "  port 24224" >> /fluentd/etc/fluent.conf
echo "  bind 0.0.0.0" >> /fluentd/etc/fluent.conf
echo "  format json" >> /fluentd/etc/fluent.conf
echo "  tag now-here-logs" >> /fluentd/etc/fluent.conf
echo "</source>" >> /fluentd/etc/fluent.conf

echo "<match now-here-logs>" >> /fluentd/etc/fluent.conf
echo "  @type oci_logging" >> /fluentd/etc/fluent.conf
echo "  compartment_id ${COMPARTMENT_ID}" >> /fluentd/etc/fluent.conf
echo "  log_id ${LOG_ID}" >> /fluentd/etc/fluent.conf
echo "  log_object_id ${LOG_ID}" >> /fluentd/etc/fluent.conf
echo "  oci_config_file /root/.oci/config" >> /fluentd/etc/fluent.conf
echo "  signer_type config" >> /fluentd/etc/fluent.conf

# 버퍼 설정 추가
echo "  <buffer>" >> /fluentd/etc/fluent.conf
echo "    @type file" >> /fluentd/etc/fluent.conf
echo "    path /var/log/fluentd-buffer/now-here-logs" >> /fluentd/etc/fluent.conf
echo "    flush_interval 10s" >> /fluentd/etc/fluent.conf  # 10초마다 버퍼 플러시
echo "    chunk_limit_size 8MB" >> /fluentd/etc/fluent.conf  # 각 청크 사이즈 설정
echo "    total_limit_size 10MB" >> /fluentd/etc/fluent.conf  # 전체 버퍼 제한 크기
echo "    retry_forever true" >> /fluentd/etc/fluent.conf  # 실패 시 무한 재시도
echo "    compress gzip" >> /fluentd/etc/fluent.conf  # 압축 형식 설정
echo "  </buffer>" >> /fluentd/etc/fluent.conf
echo "</match>" >> /fluentd/etc/fluent.conf

# Fluentd 자체 로그를 처리하는 부분 (선택 사항)
echo "<label @FLUENT_LOG>" >> /fluentd/etc/fluent.conf
echo "  <match fluent.**>" >> /fluentd/etc/fluent.conf
echo "    @type stdout" >> /fluentd/etc/fluent.conf
echo "  </match>" >> /fluentd/etc/fluent.conf
echo "</label>" >> /fluentd/etc/fluent.conf

# Fluentd 실행
fluentd -c /fluentd/etc/fluent.conf
