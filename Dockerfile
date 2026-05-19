FROM python:3.11-slim

WORKDIR /app

RUN sed -i 's/deb.debian.org/mirrors.aliyun.com/g' /etc/apt/sources.list.d/debian.sources 2>/dev/null;     apt-get update && apt-get install -y --no-install-recommends     gcc libpq-dev postgresql-client redis-tools curl &&     rm -rf /var/lib/apt/lists/*

COPY requirements.txt .
RUN pip install --no-cache-dir -i https://mirrors.aliyun.com/pypi/simple/ --trusted-host mirrors.aliyun.com -r requirements.txt

COPY . .

RUN mkdir -p /tmp/skill-uploads &&     chmod +x entrypoint.sh

EXPOSE 5000

ENTRYPOINT ["./entrypoint.sh"]

CMD ["gunicorn", "--bind", "0.0.0.0:5000", "--workers", "2", "--threads", "4", "--worker-class", "gthread", "--timeout", "120", "--graceful-timeout", "30", "--keep-alive", "5", "--log-level", "info", "--access-logfile", "-", "--error-logfile", "-", "wsgi:app"]
