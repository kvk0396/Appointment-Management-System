# 🏥 Healthcare Appointment Management System (Backend - Microservices Architecture)

This project is a **Spring Boot Microservices-based Healthcare Appointment Management System** designed to streamline appointment scheduling, consultations, notifications, and secure access using **JWT-based authentication**.

---

## 🧩 Key Features

- **User Registration & Login** (JWT authentication)

- **Role-based Access** for Patients and Doctors

- **Appointment Management**  

  - Book, cancel, update appointments (Patient)

  - View appointments (Doctor & Patient)

- **Doctor Availability Management**

- **Consultation Records**  

  - Add consultations (Doctor)  

  - View consultation history (Patient)

- **Email Notifications** (on registration and appointments)

- **API Gateway** with token validation and routing

- **Centralized Configuration** using Spring Cloud Config

- **Token validation on downstream services** using custom filters

---

## 🔧 Tech Stack

| Layer | Technology |

|-------|------------|

| Backend | Java, Spring Boot |

| Microservices | Spring Cloud (Eureka, Config Server, Gateway) |

| Security | Spring Security, JWT |

| Data Persistence | JPA, Hibernate, MySQL |

| Communication | Feign Clients, REST |

| Notifications | Email (JavaMailSender) |

| Others | Lombok, MapStruct, Maven |

---

## 🧱 Microservices Overview

| Service | Description |

|---------|-------------|

| **API Gateway** | Routes incoming requests and validates JWT tokens |

| **Config Server** | Centralized configuration for all services |

| **User Service** | Manages Patients and Doctors |

| **Patient Service** | Handles patient-specific operations |

| **Doctor Service** | Handles doctor-specific operations |

| **Appointment Service** | Manages appointments (create, cancel, update) |

| **Availability Service** | Allows doctors to add their available slots |

| **Consultation Service** | Allows doctors to add consultation records |

| **Notification Service** | Sends email notifications for key actions |

---

## 🔐 Security Overview

- **JWT Token-Based Authentication**

- **Role-based access control (Patient/Doctor)**

- **Token passed via Authorization header**

- **Each microservice validates the token and sets SecurityContext**

---

## 🧪 How It Works

1. **User Registration/Login** → JWT token issued

2. **API Gateway** validates the token and forwards request

3. Each microservice checks user roles & authorizations

4. **Patients** can book/cancel/update appointments and view consultations

5. **Doctors** can add availability and consultation records

6. **Notification Service** sends emails on important actions

---
