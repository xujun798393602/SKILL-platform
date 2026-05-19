"""Gunicorn configuration for skill-platform."""
import multiprocessing
import os

# Server socket
bind = "0.0.0.0:5000"

# Worker processes
workers = int(os.getenv("GUNICORN_WORKERS", min(multiprocessing.cpu_count() * 2 + 1, 4)))
worker_class = "sync"
timeout = 120
keepalive = 5
max_requests = 1000
max_requests_jitter = 50

# Logging to stdout/stderr for Docker
accesslog = "-"
errorlog = "-"
loglevel = os.getenv("LOG_LEVEL", "info")

# Process
proc_name = "skill-platform"
