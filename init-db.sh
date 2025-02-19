#!/bin/bash

# Function to check the OS
os_type=$(uname)

# Function to run the Docker Compose command based on OS
run_docker_compose() {
  echo "Starting Docker Compose..."
  if [[ "$os_type" == "Darwin" || "$os_type" == "Linux" ]]; then
    # For macOS and Linux
    docker-compose up -d --build
  elif [[ "$os_type" == "CYGWIN"* || "$os_type" == "MINGW"* || "$os_type" == "MSYS"* ]]; then
    # For Windows
    docker-compose up -d --build
  else
    echo "Unsupported OS detected: $os_type"
    exit 1
  fi
}

# Function to initialize the database
init_db() {
  echo "Initializing Databases..."

  # Check if the Postgres container is running
  docker exec db-radiant-sispa bash -c "until pg_isready -U admin; do echo Waiting for postgres; sleep 2; done"

  # Create dev and prod databases
  docker exec db-radiant-sispa createdb -U admin sispa_dev
  docker exec db-radiant-sispa createdb -U admin sispa_prod

  echo "Databases created successfully."
}

# Run Docker Compose and initialize databases
run_docker_compose
init_db
