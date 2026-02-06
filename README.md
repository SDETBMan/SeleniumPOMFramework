# Hybrid AI-Powered Automation Framework (Web & Mobile)

A production-grade, thread-safe automation framework built with **Java, Selenium, Appium, and TestNG**.
Designed for stability, scalability, and modern CI/CD pipelines, featuring **Self-Healing** capabilities and **AI-driven** test data generation.

[![Selenium POM CI](https://github.com/SDETBMan/SeleniumPOMFramework/actions/workflows/maven.yml/badge.svg)](https://github.com/SDETBMan/SeleniumPOMFramework/actions/workflows/maven.yml)

## Key Features

* **Hybrid Execution:** Unified framework supporting **Web** (Chrome, Firefox, Edge) and **Mobile** (Android/Appium) in a single suite.
* **Self-Healing Automation:** Integrated **Healenium** to automatically recover from `NoSuchElementException` by analyzing the DOM tree at runtime.
* **AI-Driven Testing:** Includes an `AiHelper` utility (OpenAI integration) to dynamically generate robust test data and analyze failure patterns.
* **Thread-Safety:** Implements `ThreadLocal` driver management for 100% isolation during parallel execution.
* **Dockerized Infrastructure:** Full `docker-compose` setup for a **Selenium Grid** combined with the **Healenium Backend**.
* **CI/CD Ready:** Configured for GitHub Actions with Headless execution.

---

## Tech Stack

* **Language:** Java 17
* **Build Tool:** Maven
* **Orchestration:** TestNG (Groups, Listeners, DataProviders)
* **Web Automation:** Selenium WebDriver 4.x
* **Mobile Automation:** Appium 2.x
* **Self-Healing:** Healenium (Backend + Web Library)
* **Containerization:** Docker & Docker Compose
* **Reporting:** Allure Reports

---

## ⚡ Quick Start (Cheat Sheet)

### 1. Infrastructure Setup
Start the "Super Grid" (Selenium Hub + Nodes + Healenium Brain):
```bash
  docker-compose up -d
```
* Grid Dashboard: http://localhost:4444

* Healenium Backend: http://localhost:7878
### 2. Execution Commands

Run Web Regression (Chrome, Firefox, Edge):
```bash
  mvn clean test -Dgroups=web
```
Run Mobile Tests (Android): Prerequisite: Ensure Appium Server and Emulator are running.
```bash
  mvn clean test -Dgroups=mobile
```
Run Smoke Tests (Fast Check):
```bash
  mvn clean test -Dgroups=smoke
```
Run in CI Mode (Headless):
```bash
  mvn clean test -Dgroups=web -Dheadless=true
```
# Advanced Capabilities

## Self-Healing (Healenium)

### The DriverFactory wraps the standard WebDriver in a SelfHealingDriver.

### 1. Learn: When a test passes, Healenium stores the locator in its database (PostgreSQL).

### 2. Heal: If a locator changes (e.g., developer changes an ID), Healenium scans the page for the element using neighboring attributes.

### 3. Recover: The test passes automatically, and the new locator is logged for future updates.

## AI-Driven Testing

### The AiHelper class connects to LLMs (like OpenAI) to prevent "Brittle Data" issues.

* ### Usage: AiHelper.generateTestData("Generate a valid username for a fintech app")

* ### Benefit: Tests cover edge cases (long strings, special chars) that hardcoded data misses.

# Project Structure

```
src
├── main
│   └── java/com/framework
│       ├── driver       # DriverFactory (Healenium Wrapped) & DriverManager
│       ├── pages        # Page Object Classes
│       │   ├── mobile   # Android Page Objects
│       │   └── web      # Web Page Objects
│       └── utils        # ConfigReader, AiHelper (AI Logic)
├── test
│   └── java/com/framework
│       ├── listeners    # TestListener (Screenshots, Logs)
│       └── tests        # Test Classes (LoginTest, MobileLoginTest, AiDrivenTest)
├── docker-compose.yaml  # Grid + Healenium Infrastructure
└── Dockerfile           # Container definition for CI execution
```

# CI/CD Pipeline

## This project includes a GitHub Actions workflow that:

### 1. Sets up Java 17.

### 2. Spins up the Docker Grid infrastructure.

### 3. Executes the Web Test Group in Headless mode.

### 4. Generates and uploads the Allure Report.

***

