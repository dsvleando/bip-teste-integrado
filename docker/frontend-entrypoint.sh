#!/bin/sh
set -e

: "${BACKEND_API_HOST:=backend}"
: "${BACKEND_API_PORT:=8080}"

TEMPLATE_PATH=/etc/nginx/conf.d/default.conf.template
CONFIG_PATH=/etc/nginx/conf.d/default.conf

envsubst '$BACKEND_API_HOST $BACKEND_API_PORT' < "$TEMPLATE_PATH" > "$CONFIG_PATH"

exec nginx -g 'daemon off;'
