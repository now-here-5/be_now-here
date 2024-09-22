#!/bin/bash

if systemctl is-active --quiet postgresql@14-main; then
  echo "PostgreSQL is already running."
else
  echo "PostgreSQL is not running. Starting PostgreSQL..."
  sudo systemctl start postgresql@14-main
fi
