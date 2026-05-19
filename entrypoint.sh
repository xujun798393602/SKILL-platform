#!/bin/bash
set -e

# ------------------------------------------------------------------
# 1. Wait for PostgreSQL
# ------------------------------------------------------------------
echo "==> Waiting for PostgreSQL at postgres:5432 ..."
MAX_RETRIES=30
RETRY_COUNT=0
until python -c "
import socket, sys
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
try:
    s.connect(('postgres', 5432))
    s.close()
    sys.exit(0)
except Exception:
    sys.exit(1)
" 2>/dev/null; do
    RETRY_COUNT=$((RETRY_COUNT + 1))
    if [ "$RETRY_COUNT" -ge "$MAX_RETRIES" ]; then
        echo "ERROR: PostgreSQL not reachable after $MAX_RETRIES attempts."
        exit 1
    fi
    echo "    PostgreSQL not ready yet. Retrying ($RETRY_COUNT/$MAX_RETRIES)..."
    sleep 2
done
echo "==> PostgreSQL is ready."

# ------------------------------------------------------------------
# 2. Wait for Redis
# ------------------------------------------------------------------
echo "==> Waiting for Redis at redis:6379 ..."
RETRY_COUNT=0
until python -c "
import socket, sys
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
try:
    s.connect(('redis', 6379))
    s.close()
    sys.exit(0)
except Exception:
    sys.exit(1)
" 2>/dev/null; do
    RETRY_COUNT=$((RETRY_COUNT + 1))
    if [ "$RETRY_COUNT" -ge "$MAX_RETRIES" ]; then
        echo "ERROR: Redis not reachable after $MAX_RETRIES attempts."
        exit 1
    fi
    echo "    Redis not ready yet. Retrying ($RETRY_COUNT/$MAX_RETRIES)..."
    sleep 2
done
echo "==> Redis is ready."

# ------------------------------------------------------------------
# 3. Database migrations
# ------------------------------------------------------------------
if [ -d "migrations" ]; then
    echo "==> Running database migrations ..."
    flask db upgrade
    echo "==> Migrations applied."
else
    echo "==> WARNING: No 'migrations/' directory found."
    echo "    Creating all tables via db.create_all() ..."
    python -c "
from app import create_app, db
app = create_app('production')
with app.app_context():
    db.create_all()
    print('    All tables created.')
"
    echo "    Run 'flask db init && flask db migrate' for proper migrations."
fi

# ------------------------------------------------------------------
# 4. Seed default data (roles + admin user)
# ------------------------------------------------------------------
echo "==> Initializing default data ..."
export FLASK_APP=wsgi.py
flask init-db

# ------------------------------------------------------------------
# 4. Hand off to CMD (gunicorn)
# ------------------------------------------------------------------
echo "==> Starting application: $@"
exec "$@"
