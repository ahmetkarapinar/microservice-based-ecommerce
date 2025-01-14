# **Microservice-Based E-Commerce Application**

This project implements a robust and scalable e-commerce platform built with a modern microservices architecture. Each service is designed to operate independently, ensuring modularity, scalability, and ease of maintenance. The platform includes user, product, order, and notification services, all seamlessly integrated via an API Gateway and Service Registry.
## **Detailed Documentation**
This project is accompanied by **extensive documentation** that provides in-depth reasoning behind the architectural and design choices.
- You can access the documentation here: [Architectural Design Documentation](./Architectural_Documentation.pdf)
- API documentation: [API Doc](https://documenter.getpostman.com/view/30216129/2sAYQXpDfG)
## **Key Features**
- **Microservices Architecture**: Independently deployed services for modular functionality and scalability.
- **Dockerized Deployment**: Services are containerized and launched effortlessly using `docker-compose`.
- **Caching and Messaging**: Optimized performance with Redis caching and asynchronous messaging via RabbitMQ.
- **Dynamic Load Balancing**: Traffic is efficiently distributed via load balancing mechanisms to improve performance and reliability.
- **Comprehensive Testing and Error Handling**: Unit tests and exception handling ensure reliability and robustness.
## **Getting Started**
1. Ensure Docker and Docker Compose are installed.
2. Start the application:
   ```bash
   docker-compose up --build -d
3. Access the API Gateway at http://localhost:4000.
## **Technology Stack**
This project utilizes the following technologies:
- **Spring Boot**: For building microservices and RESTful APIs.
- **PostgreSQL**: As the relational database management system.
- **Redis**: For high-performance caching.
- **RabbitMQ**: For asynchronous messaging between services.
- **Netflix Eureka**: For service discovery and dynamic scaling.
- **Docker**: For containerization of services.