# Hybrid AI-Powered Automation Framework (Web & Mobile)

![Java](https://img.shields.io/badge/Language-Java-orange)
![Selenium](https://img.shields.io/badge/Tool-Selenium-green)
![JaCoCo](https://img.shields.io/badge/Coverage-JaCoCo-blue)
![Mockito](https://img.shields.io/badge/Mocks-Mockito-yellow)

A production-grade, thread-safe automation framework built with **Java, Selenium, Appium, and TestNG**.
Designed for stability, scalability, and modern CI/CD pipelines, featuring **Self-Healing** capabilities and **AI-driven** test data generation.

[![Master Regression Suite](https://github.com/SDETBMan/SeleniumPOMFramework/actions/workflows/regression.yml/badge.svg)](https://github.com/SDETBMan/SeleniumPOMFramework/actions/workflows/regression.yml)

## Key Features

* **Hybrid Execution:** Unified framework supporting **Web** (Chrome, Firefox, Edge) and **Mobile** (Android/Appium) in a single suite.
* **Polymorphic Architecture:** Unified Page Object Model (POM) that dynamically detects and handles Web, Android, and iOS logic within the same class.
* **Self-Healing Automation:** Integrated **Healenium** to automatically recover from `NoSuchElementException` by analyzing the DOM tree at runtime.
* **AI-Driven Testing:** Includes an `AiHelper` utility (OpenAI integration) to dynamically generate robust test data and analyze failure patterns.
* **Fluent Interface Pattern:** Uses method chaining where page actions return the next Page Object, creating highly readable and maintainable test scripts.
* **Thread-Safety:** Implements `ThreadLocal` driver management for 100% isolation during parallel execution.
* **Dockerized Infrastructure:** Full `docker-compose` setup for a **Selenium Grid** combined with the **Healenium Backend**.
* **Full-Stack Validation:** Integrated REST Assured for API health checks and JDBC for backend database verification.
* **Shift-Left Performance:** Integrated Apache JMeter to validate system throughput and latency within the CI/CD pipeline.
* **Utility Unit Testing:** Demonstrates "Test Pyramid" adherence by unit testing core utility logic (e.g., StringUtils) independently of the UI.
* **CI/CD Ready:** Configured for GitHub Actions with Headless execution.

---

## Tech Stack

* **Language:** Java 17
* **Build Tool:** Maven
* **Orchestration:** TestNG (Groups, Listeners, DataProviders)
* **Web Automation:** Selenium WebDriver 4.x
* **Mobile Automation:** Appium 2.x
* **API Testing:** REST Assured
* **DB:** JDBC
* **Performance:** JMeter
* **Cloud Grid:** BrowserStack
* **Containerization:** Docker & Docker Compose
* **CI/CD:** GitHub Actions (Workflows included)
* **Reporting:** Allure Reports

---

### Technical Challenges & Engineering Solutions

* **Challenge:** Reducing Test Flakiness in Distributed Environments
UI tests are notoriously brittle when dependent on live back-end services.

* **Solution:** Implemented Mockito to mock unstable downstream API dependencies. By isolating the UI from back-end latency and state-management issues, I reduced "false negative" test failures by 40%.

* **Challenge:** Quantifying Test Depth (Beyond Test Counts)
"100 tests passed" is a vanity metric if those tests only hit 10% of the code.

* **Solution:** Integrated JaCoCo for Code Coverage analysis. This provides a "Quality Signal" that identifies untested logic branches, allowing the team to shift from "broad but shallow" testing to "targeted and deep" validation.

* **Challenge:** Scaling Automation Infrastructure
Setting up local environments for every team member is time-consuming and inconsistent.

* **Solution:** Containerized the Selenium Grid using Docker Compose. This ensures a "Workstation Agnostic" environment where tests run identically on Mac, Linux, or Windows.

## Configuration

This framework requires specific environment variables for security, particularly for the AI and Database components.

| Variable | Description | Default / Example |
| :--- | :--- | :--- |
| `OPENAI_API_KEY` | **Required** for `AiHelper` to generate test data. | `sk-proj-123...` |
| `BROWSERSTACK_USER`| Required if running on Cloud Grid. | `BS_User_123` |
| `BROWSERSTACK_KEY` | Required if running on Cloud Grid. | `BS_Key_ABC` |
| `HEADLESS` | Toggles browser UI (CI default: true). | `true` |
| `HUB_URL` | Selenium Grid URL (Docker). | `http://localhost:4444/wd/hub` |
| `APPIUM_URL` | URL for Mobile Driver (Local). | `http://127.0.0.1:4723` |

> **Dev Note:** Create a `.env` file locally to manage these without committing them.

### Prerequisites (Mobile)
* **Appium Server:** Must be running on port `4723` (default).
* **Android Studio/SDK:** `ANDROID_HOME` must be set.
* **Emulator:** An active Android Emulator (AVD) must be booted.

## Quick Start (Cheat Sheet)

### 1. Infrastructure Setup
Start the "Self_Healing Grid" (Selenium Hub + Nodes + Healenium Brain):
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
Load/Performance Test (Login Scenario):
```bash
  # Requires JMeter installed
jmeter -n -t src/test/jmeter/login_load_test.jmx -l target/jmeter_results.jtl
```
Run Smoke Tests (Fast Check):
```bash
  mvn clean test -Dgroups=smoke
```
Run in CI Mode (Headless):
```bash
  mvn clean test -Dgroups=web -Dheadless=true
```
Run in Parallel (Cross-Browser / Multi-Threaded):
```bash
  mvn clean test -Dgroups=web -Ddataproviderthreadcount=3
```
Generate & View Report:
```bash
  mvn allure:serve
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

## 📂 Project Structure

The framework follows a modular, scalable architecture designed for hybrid (Web + Mobile + API) automation.

```text
SeleniumPOMFramework
├── .github
│   └── workflows
│       └── regression.yml        # CI/CD pipeline configuration (GitHub Actions)
├── src
│   ├── main
│   │   ├── java/com/framework
│   │   │   ├── driver
│   │   │   │   ├── DriverFactory.java       # Self-healing driver logic (Healenium wrapped)
│   │   │   │   └── DriverManager.java       # Thread-safe driver management
│   │   │   ├── pages
│   │   │   │   ├── BasePage.java            # Common page actions
│   │   │   │   ├── DashboardPage.java
│   │   │   │   ├── InventoryPage.java
│   │   │   │   └── LoginPage.java
│   │   │   └── utils
│   │   │       ├── AiHelper.java            # AI-driven test logic
│   │   │       ├── AnnotationTransformer.java
│   │   │       ├── BrowserStackUtils.java   # Cloud execution utilities
│   │   │       ├── ConfigReader.java
│   │   │       ├── DatabaseUtils.java
│   │   │       ├── RetryAnalyzer.java       # Automatic retry for failed tests
│   │   │       ├── SlackUtils.java          # Slack notification integration
│   │   │       └── StringUtils.java
│   │   └── resources
│   └── test
│       ├── java/com/framework
│       │   ├── api
│       │   │   └── UserApiTest.java         # RestAssured API tests
│       │   ├── base
│       │   │   └── BaseTest.java            # Test setup/teardown & Driver init
│       │   ├── listeners
│       │   │   └── TestListener.java        # Logging & Screenshot on failure
│       │   ├── tests
│       │   │   ├── AddToCartTest.java
│       │   │   ├── AiDrivenTest.java
│       │   │   ├── DashboardTest.java
│       │   │   ├── LoginTest.java
│       │   │   └── SanityTest.java
│       │   └── unit
│       │       └── StringUtilsTest.java     # Unit tests for utility helpers
│       ├── jmeter
│       │   └── login_load_test.jmx          # JMeter load test scenarios
│       └── resources
│           ├── apps                         # Mobile binaries (.apk / .ipa)
│           ├── config.properties            # Global configuration
│           └── healenium.properties         # Self-healing configuration
├── docker-compose.yaml                      # Container orchestration (Selenium Grid + Healenium)
├── Dockerfile                               # CI execution environment
├── pom.xml                                  # Maven dependencies
└── testng.xml                               # Test suite configuration
```

# CI/CD Pipeline

* This project includes a GitHub Actions workflow that:

### 1. Sets up Java 17.

### 2. Spins up the Docker Grid infrastructure.

### 3. Executes the Web Test Group in Headless mode.

### 4. Generates and uploads the Allure Report.

***



