# Obilet Automation Project

This project is a test automation project containing automated test scenarios for the Obilet website. It is developed using Selenium WebDriver and Gauge framework.

## Features

- Multi-browser support (Chrome, Firefox, Edge, Safari)
- BDD (Behavior Driven Development) approach with Gauge framework
- Thread-safe WebDriver management
- Dynamic element location strategies

##  Requirements

- Java JDK 11 or higher
- Maven
- Gauge
- WebDriver (ChromeDriver, GeckoDriver, etc.)

##  Installation

1. Clone the project:
```bash
git clone https://github.com/your-username/obilet-automation.git
cd obilet-automation
```

2. Install dependencies:
```bash
mvn clean install
```

3. Install Gauge:
```bash
npm install -g @getgauge/cli
```

4. Install Gauge Java plugin:
```bash
gauge install java
```

## Configuration

Project settings are located in configuration files under the `src/test/resources/env` directory:
- `default/config.properties`: Default settings
Important configuration parameters:
- `TEST_URL`: Website URL to be tested
- `BROWSER`: Browser to be used (chrome, firefox, edge, safari)
- `TIMEOUT`: Default wait time (seconds)

##  Running Tests

1. To run all tests:
```bash
mvn gauge:execute
```

2. To run tests in a specific environment:
```bash
mvn gauge:execute -Dgauge.env=staging
```

3. To run a specific scenario:
```bash
mvn gauge:execute -DspecsDir=specs/your-spec.spec
```

## Project Structure

```
obilet-automation/
├── src/
│   ├── test/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── obilet/
│   │   │           ├── driver/
│   │   │           │   ├── BaseDriver.java
│   │   │           │   └── DriverFactory.java
│   │   │           ├── Methods/
│   │   │           │   └── ConfigReader.java
│   │   │           └── steps/
│   │   │               └── BaseSteps.java
│   │   └── resources/
│   │       └── env/
│   │           ├── default/
│   └── specs/
└── pom.xml
```
