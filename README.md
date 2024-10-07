# Todo App Root

This is a Todo App with a backend built in Java (Spring Boot) and a frontend built with React. The application uses PostgreSQL as its database.

API Documentation
API documentation for the backend service detailing the available endpoints and their usage can be accessed at the following link:
https://documenter.getpostman.com/view/36514897/2sA3rwNaQK


## Prerequisites

- Docker Desktop
    - (https://www.docker.com/products/docker-desktop) installed and running
    - Follow the instructions on the Docker Desktop website to download and install Docker Desktop for your operating system.
 
- Docker Compose (included with Docker Desktop)

**Note:** Ensure that ports **5432**, **8080**, and **5173** are not being used by other applications on your machine.

## Building and Running the Application
### Steps

1. **Clone the repository:**

    ```bash
    git clone https://github.com/rt1900/react_vite_todo_app.git
    cd react_vite_todo_app
    ```

2. **Build and run the application using Docker Compose:**

    ```bash
    docker-compose -f docker-compose.prod.yml up --build
    ```

   This command will:
    - Build the Docker images for the backend, frontend, and database services.
    - Start the containers for each service.

3. **Access the application:**

    - The frontend will be available at `http://localhost:5173`
    - The backend will be available at `http://localhost:8080`
    - The PostgreSQL database will be running on port `5432`

### Docker Images

The Docker images used in this project are:

- Backend: `rt1900/todo-app-backend:v1.0`
- Frontend: `rt1900/todo-app-frontend:v1.0`
- Database: `rt1900/todo-app-database:v1.0`

### Stopping the Application

To stop the application, run:

```bash
docker-compose -f docker-compose.prod.yml down
