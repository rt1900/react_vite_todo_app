# Use the official PostgreSQL image from the Docker Hub
FROM postgres:14

# Set environment variables for the PostgreSQL image
ENV POSTGRES_DB=tododb
ENV POSTGRES_USER=todo_user
ENV POSTGRES_PASSWORD=user_3000

# Copy initialization scripts, if any (e.g., SQL scripts for database setup)
# COPY ./initdb/*.sql /docker-entrypoint-initdb.d/

EXPOSE 5432
