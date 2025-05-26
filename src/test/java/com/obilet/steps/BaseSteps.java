package com.obilet.steps;

import com.obilet.Methods.ConfigReader;
import com.obilet.driver.DriverFactory;
import com.thoughtworks.gauge.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import com.obilet.Methods.RandomDataGenerator;

import java.time.Duration;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

public class BaseSteps {

    private final WebDriver driver = DriverFactory.getDriver();
    Logger logger = Logger.getLogger(String.valueOf(BaseSteps.class));
    Duration waitTime = Duration.ofSeconds(Long.parseLong(ConfigReader.getProperty("TIMEOUT" , "25")));
    WebDriverWait wait = new WebDriverWait(driver, waitTime);

    @Step("Check element existence <ElementName>")
    public WebElement createWebElement(String ElementName) {
        String xpathTemplate;
        if (ElementName.toLowerCase().contains("xpath.")) {
            xpathTemplate = ElementName.replaceAll("xpath.", "");
        } else {
            xpathTemplate = "//*[normalize-space(.)='" + ElementName + "']";
        }
        logger.info("Creating WebElement with giving template: " + xpathTemplate);
        WebElement mainElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpathTemplate)));
        Assert.assertNotNull(mainElement, "Element is not found: %s".formatted(ElementName));
        logger.info("Element is found: %s".formatted(ElementName));
        return mainElement;
    }

    @Step("Check element existence <ElementName> must be <status>")
    public void verifyElementVisibility(String ElementName, Visibility status) {
        boolean shouldBeVisible = status == Visibility.visible;
        boolean isVisible;
        try {
            WebDriverWait waitVisibility = new WebDriverWait(driver, Duration.ofSeconds(5));
            isVisible = waitVisibility.until(driver -> {
                try {
                    WebElement element = createWebElement(ElementName);
                    return element.isDisplayed();
                } catch (NoSuchElementException e) {
                    return false;
                }
            });
        } catch (TimeoutException e) {
            isVisible = false;
        }
        Assert.assertEquals(isVisible, shouldBeVisible, "Element visibility mismatch: %s".formatted(ElementName));
    }

    public enum Visibility {
        visible,
        hidden
    }

    @Step("Click button <ElementName>")
    public void clickButton(String ElementName) {
        String path = "xpath." + "//a[normalize-space(text()) = '" + ElementName + "'] | //button[normalize-space(text()) = '" + ElementName + "']";
        createWebElement(path).click();
        logger.info("Clicked on the button: %s".formatted(ElementName));
    }

    @Step("Select Destination <ElementName> and Select Location <Location>")
    public void SelectLocation(String elementName, String location) {
        String inputXpath = "//*[text() = '" + elementName + "']/..//input";
        By inputLocator = By.xpath(inputXpath);
        clearAndSendKeys(inputLocator, location);
        driver.findElement(inputLocator).sendKeys(Keys.ENTER);

        String locationPath = "xpath." + "//*[contains(text() , '" + elementName + "')]/..//div[@class = \"results\"]//span[@class = \"location\"]//span[contains(text(),'" + location + "')]";

        try {
            WebElement locationElement = createWebElement(locationPath);
            locationElement.click();
            logger.info("Successfully selected location: '%s' for destination: '%s'".formatted(location, elementName));
        } catch (TimeoutException e) {
            Assert.fail("Failed to find location '%s' under destination '%s'.".formatted(location, elementName));
        }
    }

    @Step("Select Direction on Destination <Destination> and Select Date <Date>")
    public void selectDestinationDate(String Destination, String Date) {
        String path = "xpath." + "//*[text() = '" + Destination + "']/..";
        WebElement generatedElement = createWebElement(path);
        generatedElement.click();

        String datePath = "//button[@data-date='" + Date + "']";
        WebElement dateElement = createWebElement("xpath." + datePath);
        dateElement.click();
        logger.info("Clicked on the button: %s".formatted(Destination));
        waitSeconds(2); // calendar kapanmadan sonraki adımlar bozulmasın
    }

    @Step("Select First <way> Flight and Select Flight Package Row Number <PackageRowNo>")
    public void selectFirstFlightAndPackageRow(Direction way, String packageRowNo) {
        String idSet = way == Direction.Outbound ? "outbound-journeys" : "return-journeys";
        String firstFlight = "xpath." + "//ul[@id='" + idSet + "']//li[1]";
        createWebElement(firstFlight).click();
        logger.info("Clicked on the first flight");

        String packageXPath = "//ul[@id='" + idSet + "']//li[@class='flight']//li[" + packageRowNo + "]";
        try {
            WebElement packageElement = createWebElement("xpath." + packageXPath);
            packageElement.click();
            logger.info("Selected the package row number: %s for the %s flight".formatted(packageRowNo, way.name().toLowerCase()));
        } catch (TimeoutException e) {
            Assert.fail("Failed to select package row number '%s' for the %s flight.".formatted(packageRowNo, way.name().toLowerCase()));
        }
    }

    enum Direction {
        Outbound,
        Return,
    }

    @Step("Click <ElementName> Radio button")
    public void selectRadio(String ElementName) {
        String path = "//span[text() = '" + ElementName + "']/..//input[@type = 'radio']";
        createWebElement("xpath." + path).click();
        logger.info("Clicked on the radio button: %s".formatted(ElementName));
    }

    @Step("Select <vehicleName> Transport Type")
    public void selectTransportType(String vehicleName) {
        String path = "//*[@data-event-action='" + vehicleName + "']";
        createWebElement("xpath." + path).click();
        logger.info("Clicked on the button: %s".formatted(vehicleName));
    }

    @Step("Write <text> to Element <element>")
    public void writeTextToInputArea(String text, String ElementName) {
        String path = "//*[text() = '" + ElementName + "'] | //*[contains(@placeholder, '" + ElementName + "')]";
        By locator = By.xpath(path);
        clearAndSendKeys(locator, text);
        logger.info("Wrote to input '%s': %s".formatted(ElementName, text));
    }

    private void waitSeconds(int second) {
        try {
            Thread.sleep(second * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void clearAndSendKeys(By locator, String text) {
        int attempts = 2;
        while (attempts > 0) {
            try {
                WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
                element.click();
                element.clear();
                element.sendKeys(text);
                return;
            } catch (StaleElementReferenceException e) {
                logger.warning("StaleElementReferenceException encountered, retrying: " + locator);
                attempts--;
            }
        }
        throw new RuntimeException("Failed to send keys to element after retries: " + locator);
    }

    @Step("Search flight with random information")
    public void searchFlightWithRandomInfo() {
        String originCity = RandomDataGenerator.generateRandomOriginCity();
        SelectLocation("Nereden", originCity);
        String destinationCity = RandomDataGenerator.generateRandomDestinationCity();
        SelectLocation("Nereye", destinationCity);
        String departureDate = RandomDataGenerator.generateRandomFutureDate(1, 5);
        selectDestinationDate("Gidiş Tarihi", departureDate);
        String returnDate = RandomDataGenerator.generateRandomFutureDate(5, 10);
        selectDestinationDate("Dönüş Tarihi", returnDate);
        clickButton("Uçuş Ara");
    }

    @Step("Register with random user information")
    public void registerWithRandomUser() {
        writeTextToInputArea(RandomDataGenerator.generateRandomEmail(), "E-posta");
        writeTextToInputArea(RandomDataGenerator.generateRandomPassword(), "Şifre");
    }

    @Step("Search Random Flight Dates")
    public void searchRandomFlightDates() {
        String departureDate = RandomDataGenerator.generateRandomFutureDate(1, 5);
        selectDestinationDate("Gidiş Tarihi", departureDate);
        String returnDate = RandomDataGenerator.generateRandomFutureDate(5, 10);
        selectDestinationDate("Dönüş Tarihi", returnDate);
    }
}