# Booking Platform

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-brightgreen.svg)](https://spring.io/projects/spring-boot)

Backend for a booking platform built with Spring Boot. It supports hotel browsing, bookings, guest management, admin operations, JWT authentication, and Stripe payments.

## Table of contents

- [Overview](#overview)
- [Tech stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Configuration](#configuration)
- [Run locally](#run-locally)
- [API overview](#api-overview)
- [Security](#security)
- [Documentation](#documentation)

## Overview

This application provides REST APIs for:

- hotel search and hotel details
- booking creation, guest assignment, cancellation, and payment flow
- user profile and saved guests
- admin hotel, room, inventory, and reporting operations
- Stripe webhook handling for payment confirmation

## Tech stack

| Layer | Technology |
| --- | --- |
| Language | Java 21 |
| Framework | Spring Boot |
| Web | Spring Web MVC |
| Persistence | Spring Data JPA |
| Security | Spring Security, JWT |
| Database | PostgreSQL |
| Payments | Stripe |
| API docs | Springdoc OpenAPI |

## Prerequisites

- Java 21
- Maven
- PostgreSQL
- Stripe secret key and webhook secret

## Configuration

Configuration lives in `src/main/resources/application.properties`.

| Setting | Purpose |
| --- | --- |
| `spring.datasource.*` | PostgreSQL connection details |
| `jwt.secret-key` | JWT signing key |
| `frontend.url` | Frontend origin used by the app |
| `stripe.secret.key` | Stripe API key from `STRIPE_SECRET_KEY` |
| `stripe.webhook.secret` | Stripe webhook secret from `STRIPE_WEBHOOK_SECRET` |
| `server.servlet.context-path` | API base path (`/api/v1`) |

Example environment variables:

```bash
export STRIPE_SECRET_KEY=your_secret_key
export STRIPE_WEBHOOK_SECRET=your_webhook_secret
```

## Run locally

1. Create a PostgreSQL database named `AirBnB`.
2. Update `application.properties` with your local database credentials.
3. Set the Stripe environment variables.
4. Start the application:

```bash
mvn spring-boot:run
```

## API overview

### Auth

| Method | Endpoint |
| --- | --- |
| POST | `/api/v1/auth/signup` |
| POST | `/api/v1/auth/login` |
| POST | `/api/v1/auth/refresh` |

### Public hotel browsing

| Method | Endpoint |
| --- | --- |
| GET | `/api/v1/hotels/search` |
| GET | `/api/v1/hotels/{hotelId}/info` |

### User APIs

| Method | Endpoint |
| --- | --- |
| PATCH | `/api/v1/users/profile` |
| GET | `/api/v1/users/profile` |
| GET | `/api/v1/users/myBookings` |
| GET | `/api/v1/users/guests` |
| POST | `/api/v1/users/guests` |
| PUT | `/api/v1/users/guests/{guestId}` |
| DELETE | `/api/v1/users/guests/{guestId}` |

### Booking and payment

| Method | Endpoint |
| --- | --- |
| POST | `/api/v1/bookings/init` |
| POST | `/api/v1/bookings/{bookingId}/addGuests` |
| POST | `/api/v1/bookings/{bookingId}/payment` |
| POST | `/api/v1/bookings/{bookingId}/cancel` |
| GET | `/api/v1/bookings/{bookingId}/status` |
| POST | `/api/v1/webhook/payment` |

### Admin APIs

| Method | Endpoint |
| --- | --- |
| POST | `/api/v1/admin/hotels` |
| GET | `/api/v1/admin/hotels` |
| GET | `/api/v1/admin/hotels/{hotelId}` |
| PUT | `/api/v1/admin/hotels/{hotelId}` |
| DELETE | `/api/v1/admin/hotels/{hotelId}` |
| PATCH | `/api/v1/admin/hotels/{hotelId}` |
| GET | `/api/v1/admin/hotels/{hotelId}/bookings` |
| GET | `/api/v1/admin/hotels/{hotelId}/reports` |
| POST | `/api/v1/admin/hotels/{hotelId}/rooms` |
| GET | `/api/v1/admin/hotels/{hotelId}/rooms` |
| GET | `/api/v1/admin/hotels/{hotelId}/rooms/{roomId}` |
| PUT | `/api/v1/admin/hotels/{hotelId}/rooms/{roomId}` |
| DELETE | `/api/v1/admin/hotels/{hotelId}/rooms/{roomId}` |
| GET | `/api/v1/admin/inventory/rooms/{roomId}` |
| PATCH | `/api/v1/admin/inventory/rooms/{roomId}` |

## Security

- Stateless JWT authentication
- Refresh token stored in an `HttpOnly` cookie
- `/admin/**` requires the `HOTEL_MANAGER` role
- `/users/**` and `/bookings/**` require authentication

## Documentation

Springdoc OpenAPI is included, so Swagger UI is available when the app is running.
