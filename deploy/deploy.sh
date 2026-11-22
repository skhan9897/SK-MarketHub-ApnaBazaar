#!/bin/bash
# SK-MarketHub Production EC2 Deployment Script

set -e

echo "=== SK-MarketHub Production Deployment ==="

# 1. Update and install Docker if needed
if ! command -v docker &> /dev/null; then
    echo "Installing Docker Engine..."
    sudo apt-get update
    sudo apt-get install -y ca-certificates curl gnupg lsb-release
    sudo mkdir -p /etc/apt/keyrings
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
    echo "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
    sudo apt-get update
    sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin
fi

# 2. Check for .env file
if [ ! -f .env ]; then
    echo "Creating production .env template..."
    cat <<EOT > .env
DOMAIN=skmarkethub.duckdns.org
DUCKDNS_SUBDOMAINS=skmarkethub
DUCKDNS_TOKEN=place_your_duckdns_token_here
MYSQL_DATABASE=sk_markethub_db
MYSQL_USER=skmarket
MYSQL_PASSWORD=skmarket123_prod
MYSQL_ROOT_PASSWORD=root123_prod
JWT_SECRET=9a8b7c6d5e4f3a2b1c0d9e8f7a6b5c4d3e2f1a0b9c8d7e6f5a4b3c2d1e0f9a8b
EOT
    echo "[IMPORTANT] Please edit .env with your actual DuckDNS token and passwords before running docker compose."
fi

# 3. Pull / Build & Launch Containers
echo "Building and starting production containers..."
docker compose -f docker-compose.prod.yml up -d --build

echo "=== Deployment Successful ==="
echo "Status check:"
docker compose -f docker-compose.prod.yml ps
