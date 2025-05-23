package com.obilet.steps;

import com.obilet.Methods.ConfigReader;
import com.obilet.driver.DriverFactory;
import com.thoughtworks.gauge.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

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
        logger.info("Creating WebElement with giving template" + xpathTemplate);
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
        WebElement generatedElement = createWebElement("xpath." + inputXpath);

        generatedElement.click();
        clearAndSendKeys(generatedElement, location);
        generatedElement.sendKeys(Keys.ENTER);

        String locationPath = "xpath." + "//*[contains(text() , " + elementName + ")]/..//div[@class = \"results\"]//span[@class = \"location\"]//span[contains(text(),'" + location + "')]";

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
        waitSeconds(2); // Wait for the calendar to close Because if the calendar is not closed, the next step will fail.
    }


    @Step("Select First <way> Flight and Select Flight Package Row Number <PackageRowNo>")
    public void selectFirstFlightAndPackageRow(Direction way, String packageRowNo) {
        // Select first flight
        String idSet = way == Direction.Outbound ? "outbound-journeys" : "return-journeys";
        String firstFlight = "xpath." + "//ul[@id='" + idSet + "']//li[1]";
        createWebElement(firstFlight).click();
        logger.info("Clicked on the first flight");

        // Select flight package row number
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
        WebElement generatedElement = createWebElement("xpath." + path);
        clearAndSendKeys(generatedElement, text);
        logger.info("This element written in a relative area: %s".formatted(text));
    }


    @Step("wait <second> seconds")
    private void waitSeconds(int second) {
        try {
            Thread.sleep(second * 1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void clearAndSendKeys(WebElement element, String text) {
        if (!element.getText().isEmpty()) {
            element.clear();
        }
        element.click();
        element.sendKeys(text);
    }
}