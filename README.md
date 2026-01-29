# 📦 E-Commerce Web Application - Genshin Inspired

A full-stack **e-commerce web application** developed as a **course term project**.  
The system allows users to register accounts, browse + add products, manage a shopping cart, and complete checkout transactions.  
The application is fully **Dockerized** for easy setup and deployment.

---

## 🛠️ Tech Stack

### Backend
- Java 17
- Spring Boot
- Maven
- MySQL
- Docker

### Frontend
- Angular
- TypeScript
- HTML / CSS
- Nginx (Docker)

---

## ✨ Features

- User registration and login
- Product management
- Shopping cart with quantity updates
- Checkout and order processing
- RESTful API integration
- Dockerized services

---

## 📂 Project Structure

```yaml
├── ecommerce/ # Spring Boot backend
│ ├── Dockerfile
│ ├── docker-compose.yml
│ └── src/
│ └── main/
│ ├── java/
│ └── resources/
│ └── application.yml
│
├── product/ # Angular frontend
│ ├── Dockerfile
│ ├── docker-compose.yml
│ └── src/
│ ├── app/
│ └── assets/
│ └── products/
│
└── README.md
```

---

## ⚙️ Setup & Configuration Guide

This project uses **Docker** to simplify setup.  

## 🔐 Backend Configuration

Open the following file: 

```yaml
ecommerce/src/main/resources/application.yml
```

Update the database credentials:

```yaml
spring:
  datasource:
    url: jdbc:mysql://db:3306/ecom
    username: root
    password: your_database_password_here
```

⚠️ The password must match the value defined in docker-compose.yml.

## 🐳 Docker Database Configuration

Open:

```yaml
ecommerce/docker-compose.yml
```

Verify or update the database service:

```yaml
environment:
  MYSQL_DATABASE: ecom
  MYSQL_ROOT_PASSWORD: your_database_password_here
```

## 🚀 Running the Application
1️⃣ Start the Backend (Spring Boot + MySQL)

From the project root:

```yaml
cd ecommerce
docker-compose up --build
```

Backend will run at:
```yaml
http://localhost:8080
```

2️⃣ Start the Frontend (Angular + Nginx)

Open a new terminal window:

```yaml
cd product
docker-compose up --build
```


Frontend will run at:
```yaml
http://localhost
```

## 🖼️ Product Images

Product images are stored in the frontend assets directory:

```yaml
product/src/assets/products/
```

Only the image filename is stored in the database.

Images must exist in this folder to be displayed correctly.

## 🧪 API Testing (Postman)

Example endpoints: (see postman_tests.txt)

```yaml
Login
POST /api/auth/login

Add product
PUT /api/product

View products
GET /api/product

View customers
GET /api/customers
```

## 📌 Notes

- Docker Desktop must be running before starting the application.
- Backend must be started before the frontend.
- Database credentials must match between configuration files.
- This project is for educational purposes.
