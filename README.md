Shopfront — Full-Stack E-Commerce Platform

A production-style e-commerce platform built to demonstrate solid full-stack engineering: a layered Spring Boot REST API with JWT authentication and an AI shopping assistant, backed by PostgreSQL, paired with a React + TypeScript storefront.

Tech stack

Backend: Java 21 · Spring Boot 3.5 · Spring Security (JWT) · Spring Data JPA · PostgreSQL · Flyway · Spring AI (tool calling) · springdoc-openapi (Swagger) · Maven · Docker

Features
Auth & authorization — JWT access/refresh tokens, BCrypt password hashing, role-based access control (CUSTOMER / ADMIN) enforced via Spring Security + method-level @PreAuthorize
Product catalog — dynamic search/filter/sort built with JPA Specifications (keyword, category, brand, price range, sort by price/rating/newest) instead of a fixed set of queries
Cart & checkout — server-computed subtotal, discount, and tax; stock validation on every mutation; snapshot pricing on order placement so historical orders don't drift with catalog changes
Order lifecycle — explicit state machine (PENDING → CONFIRMED → SHIPPED → DELIVERED, or CANCELLED) enforced centrally so invalid transitions are rejected at the service layer, not scattered across controllers
AI shopping assistant — Spring AI tool-calling (no RAG/vector DB): natural-language product search, recommendations, product comparison, order-status lookup, and FAQ answers, with tools scoped per-request to the authenticated user so the model can never query another customer's data
Admin dashboard — product/category/order/user management, live stats (revenue, pending orders, low-stock alerts)
Wishlist, profile management, paginated order history
Architecture
React (Vite/TS) ──REST/JWT──▶ Spring Boot Controllers ─▶ Services ─▶ Repositories ─▶ PostgreSQL
                                        │
                                        └─▶ Spring AI ChatClient ─▶ OpenAI (via @Tool-annotated
                                                                     request-scoped tool classes)

Backend follows a strict layered structure per domain module (entity → repository → service → controller, with DTOs/mappers at the boundary), documented further in the backend's own README.
