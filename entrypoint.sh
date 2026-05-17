#!/bin/bash
set -e

# Wait for PostgreSQL to be ready
echo "Waiting for PostgreSQL..."
while ! pg_isready -h ${DB_HOST:-db} -p ${DB_PORT:-5432} -U ${DB_USER:-skill_user} -q; do
    sleep 1
done
echo "PostgreSQL is ready"

# Wait for Redis to be ready (if configured)
if [ -n "$REDIS_URL" ]; then
    echo "Waiting for Redis..."
    until redis-cli -h ${REDIS_HOST:-redis} -p ${REDIS_PORT:-6379} ping > /dev/null 2>&1; do
        sleep 1
    done
    echo "Redis is ready"
fi

# Run database migrations if needed
if [ "$RUN_MIGRATIONS" = "true" ]; then
    echo "Running database migrations..."
    flask db upgrade || true
fi

# Execute the main command
exec "$@"
