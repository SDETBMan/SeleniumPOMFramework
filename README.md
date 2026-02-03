# Selenium POM Framework (TestNG + Docker + CI/CD)

A robust, scalable Test Automation Framework built using **Java**, **Selenium WebDriver**, and **TestNG**. This project implements the **Page Object Model (POM)** design pattern and supports **Cross-Browser Testing** (Chrome, Firefox, Edge) on both local machines and a **Dockerized Selenium Grid**.

[![Selenium POM CI](https://github.com/SDETBMan/SeleniumPOMFramework/actions/workflows/maven.yml/badge.svg)](https://github.com/SDETBMan/SeleniumPOMFramework/actions/workflows/maven.yml)

## Key Features

* **Page Object Model (POM):** Clean separation between test logic and page locators.
* **Hybrid Execution:** Run tests locally or on a remote Docker Grid via `DriverFactory`.
* **Test Pyramid Coverage:** Supports Unit, Integration (API), and UI (E2E) tests.
* **Cross-Browser Support:** Chrome, Firefox, Microsoft Edge.
* **Parallel Execution:** configured via `testng.xml` (Thread-safe `ThreadLocal` driver management).
* **CI/CD Ready:** Integrated with GitHub Actions for automatic execution on push.
* **Reporting:** Allure Reports integration.

---

## Tech Stack

* **Language:** Java 17
* **Build Tool:** Maven
* **Test Runner:** TestNG
* **Web Automation:** Selenium WebDriver 4.x
* **API Automation:** RestAssured
* **Containerization:** Docker & Docker Compose

---

## Getting Started

### Prerequisites
* Java JDK 17+
* Maven 3.8+
* Docker Desktop (optional, for Grid execution)

### Installation
1.  Clone the repository:
    ```bash
    git clone [https://github.com/SDETBMan/SeleniumPOMFramework.git](https://github.com/SDETBMan/SeleniumPOMFramework.git)
    ```
2.  Install dependencies:
    ```bash
    mvn clean install -DskipTests
    ```

---

## Running Tests

### 1. Run All Tests (via testng.xml)

This executes the full suite defined in `testng.xml`, including Unit, Integration, and Cross-Browser UI tests.
```bash
  mvn clean test -DsuiteXmlFile=testng.xml
```
### 2. Execute via Maven

Run the full regression suite defined in testng.xml:
```bash
  mvn clean test -DsuiteXmlFile=testng.xml
```

### 3. Run Specific Groups
You can filter tests by their @Test(groups = "...") tag.

* Smoke Tests (Critical Paths on Chrome):
```bash
  mvn clean test -Dgroups=smoke
```
* Regression Tests (Deep dive on Firefox/Edge):
```bash
  mvn clean test -Dgroups=regression
```  
* Unit Tests (Fast, no browser):
```bash
  mvn clean test -Dgroups=unit
```
### 4. Execution Modes (Local vs. Grid)
   The framework controls where the browser launches using the execution_mode property.
* Run Locally (Default): Launches the browser on your machine.
```bash
  mvn clean test -Dexecution_mode=local
```
* Run on Docker Grid: Connects to the Selenium Hub running in Docker.
```bash
  mvn clean test -Dexecution_mode=grid
```
* Headless Mode:
```bash
  mvn clean test -Dheadless=true
```
### 5. View Reports
Generate and open the Allure report in your default browser:
```bash
  mvn allure:serve
```
## Docker Setup (Selenium Grid)

This framework includes a docker-compose.yaml to spin up a local Selenium Grid with Chrome, Firefox, and Edge nodes.

### 1. Start the Grid:
```bash
  docker compose up -d
```
### 2. Check Status: Open your browser and go to http://localhost:4444 to see the Selenium Grid Dashboard.

### 3. Run Tests on Grid:
```bash
mvn clean test -Dexecution_mode=grid
```
### 4. Stop the Grid:
```bash
  docker compose down
```

📂 Project Structure
```
src
├── main
│   └── java/com/framework
│       ├── driver       # DriverFactory and ThreadLocal Manager
│       ├── pages        # Page Object Classes
│       └── utils        # ConfigReader, SlackUtils
├── test
│   └── java/com/framework
│       ├── listeners    # TestListener (Screenshots, Logs)
│       └── tests        # Actual Test Classes (LoginTest, etc.)
└── docker-compose.yaml  # Selenium Grid Configuration
```

### CI/CD Pipeline
This project includes a GitHub Actions workflow that triggers on every push to main.

* Sets up Java 17 environment.

* Spins up the Selenium Grid services.

* Executes tests in Headless mode.

* Publishes the Allure Report artifact.

---

### 3. The GitHub Action Workflow
This file will automatically run the tests every time code is pushed to GitHub. It also spins up the Docker containers inside the GitHub runner.

**File Location:** `.github/workflows/maven.yml`

```yaml
name: Selenium POM CI

on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    # 1. Checkout Code
    - uses: actions/checkout@v3

    # 2. Set up Java
    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'
        cache: maven

    # 3. Spin up Docker Grid (Background Service)
    - name: Start Docker Grid
      run: docker-compose up -d

    # 4. Wait for Grid to be Ready (Health Check)
    - name: Wait for Selenium Hub
      run: |
        echo "Waiting for Selenium Grid..."
        sleep 15

    # 5. Run Tests
    - name: Run Tests with Maven
      run: mvn clean test -DsuiteXmlFile=testng.xml

    # 6. Generate Report (Optional Artifact)
    - name: Upload Allure Report
      if: always()
      uses: actions/upload-artifact@v3
      with:
        name: allure-results
        path: allure-results