## ShopSphere – Microservices E-Commerce Platform

ShopSphere is a modern, scalable microservices-based e-commerce platform built using Spring Boot and Spring Cloud. It demonstrates real-world enterprise architecture by handling complete online shopping workflows including authentication, product management, ordering, payments, and notifications.

 ## Architecture Overview

## ShopSphere follows an event-driven microservices architecture with:

API Gateway for routing & security
Service Discovery using Eureka
Centralized Configuration Server
Independent microservices for each domain
Asynchronous communication using Kafka
Distributed transaction handling using Saga Pattern

## Core Microservices
Auth Service – User authentication & JWT security
User Service – User profile & management
Product Service – Product catalog management
Cart Service – Shopping cart operations
Order Service – Order processing
Payment Service – Payment handling
Inventory Service – Stock management
Shipping Service – Shipment processing
Notification Service – Email/SMS notifications

## Key Features
Scalable and loosely coupled architecture
Event-driven communication using Kafka
Fault tolerance with Circuit Breaker
API Gateway with centralized routing
Distributed tracing (Zipkin)
Centralized logging & monitoring
Secure authentication using JWT
## Tech Stack
Backend: Java, Spring Boot, Spring Cloud
Database: MySQL / MongoDB
Messaging: Apache Kafka
DevOps: Docker
Tools: Eureka, Zipkin, Config Server, API Gateway
## Purpose

## This project is built to:
Demonstrate real-world microservices architecture
Implement scalable and distributed systems
Showcase backend engineering skills for job readiness
 Future Enhancements
Kubernetes deployment
CI/CD pipeline integration
Advanced monitoring with Prometheus & Grafana


## How to Run

### 1. Clone Repository
git clone https://github.com/your-username/shopsphere.git

### 2. Start Services in Order

1. Config Server
2. Eureka Server
3. API Gateway
4. All Microservices

### 3. Start Kafka & Zookeeper

### 4. Access API Gateway
http://localhost:8080

## Order Flow

1. User places an order
2. Order Service creates order
3. Event sent to Kafka
4. Inventory Service updates stock
5. Payment Service processes payment
6. Shipping Service handles delivery
7. Notification Service sends confirmation

##  Challenges & Solutions

- Handling distributed transactions → Solved using Saga Pattern
- Service communication → Implemented Kafka event-driven architecture
- Fault tolerance → Used Circuit Breaker
- Service discovery → Implemented Eureka Server


## 🏗️ Architecture Diagram

<p align="center">
  <img src="./architecture.png" width="800"/>
</p>


## 👨‍💻 Author

Shubham Mungase  
Java Backend Developer  
LinkedIn: https://www.linkedin.com/in/shubham-mungase-b635222a5/
