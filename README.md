# Selenium POM Framework 🚀

![Java](https://img.shields.io/badge/Java-17-orange)
![Selenium](https://img.shields.io/badge/Selenium-4.16-green)
![TestNG](https://img.shields.io/badge/TestNG-7.9-blue)
![Docker](https://img.shields.io/badge/Docker-Enabled-2496ED)
![Allure](https://img.shields.io/badge/Allure-Report-yellow)

## 📌 Overview
This repository contains a robust **Test Automation Framework** built using the **Page Object Model (POM)** design pattern. It is engineered to support scalable, cross-browser execution on a **Selenium Grid (Dockerized)** and includes advanced features like thread-safe driver management, automated retries, and Slack notifications.

The framework is designed to demonstrate **modern QA architecture** principles suitable for enterprise-grade applications.

## 🏗️ Architecture
The framework follows a modular structure to separate concerns:
* **Test Layer:** TestNG classes (`src/test/java`) containing assertions and business logic.
* **Page Layer:** Page Object classes (`src/main/java`) encapsulating web elements and interactions.
* **Driver Layer:** Thread-safe `DriverManager` using `ThreadLocal` for parallel execution.
* **Utility Layer:** Helpers for configuration, listeners, and retry logic.
* **Infrastructure:** `docker-compose.yaml` for spinning up Selenium Hub and Nodes.

## 🛠️ Tech Stack
| Component | Technology | Usage |
| :--- | :--- | :--- |
| **Language** | Java 17 | Core programming language |
| **Web Driver** | Selenium 4 | Browser automation |
| **Runner** | TestNG | Test execution and assertion |
| **Design Pattern** | Page Object Model | Code maintainability and reuse |
| **Reporting** | Allure | Rich HTML reports with screenshots |
| **Infrastructure** | Docker | Containerized Selenium Grid |
| **CI/CD** | GitHub Actions | Automated build pipeline |
| **Notifications** | Slack Webhook | Real-time test status alerts |

## 🚀 Getting Started

### Prerequisites
* Java JDK 17+
* Maven 3.8+
* Docker Desktop (for Grid execution)

### Installation
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/SDETBMan/selenium-pom-framework.git](https://github.com/yourusername/selenium-pom-framework.git)
    ```
2.  **Navigate to the project directory:**
    ```bash
    cd selenium-pom-framework
    ```

### 🏃 Running Tests

1. **Start the Docker Grid**
Before running tests in `grid` mode, ensure the infrastructure is up:
    ```bash
    docker-compose up -d
    ```

2. **Execute via Maven**
Run the full regression suite defined in testng.xml:
    ```bash
    mvn clean test -DsuiteXmlFile=testng.xml
    ```

3. **View Reports**
Generate and open the Allure report in your default browser:
    ```bash
    mvn allure:serve
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

⚙️ CI/CD Pipeline
This project includes a GitHub Actions workflow that triggers on every push to main.

Sets up Java 17 environment.

Spins up the Selenium Grid services.

Executes tests in Headless mode.

Publishes the Allure Report artifact.

---

### 3. The GitHub Action Workflow
This file will automatically run your tests every time you push code to GitHub. It even spins up the Docker containers inside the GitHub runner!

**File Location:** `.github/workflows/maven.yml` (Create these folders if they don't exist)

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