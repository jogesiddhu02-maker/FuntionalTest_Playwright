package Com.DemoQATesting;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Demo{
    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    public static void main(String[] args) {
        new Demo().run();
    }

    public void run() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        js = (JavascriptExecutor) driver;

        try {
            driver.get("https://demoqa.com/");
            System.out.println("Title: " + driver.getTitle());

            testTextBox();
            testCheckBox();
            testRadioButton();
            testPracticeForm();
            testButtons();
            testAlerts();
            testDynamicProperties();

            System.out.println("\nAll DemoQA functionalities completed successfully.");
        } finally {
            driver.quit();
        }
    }

    // ELEMENTS -> Text Box
    private void testTextBox() {
        driver.get("https://demoqa.com/text-box");
        System.out.println("URL: " + driver.getCurrentUrl());
        System.out.println(driver.findElement(By.tagName("body")).getText());

        driver.findElement(By.id("userName")).sendKeys("Siddhu Joge");
        driver.findElement(By.id("userEmail")).sendKeys("jogesiddhu02@gmail.com");
        driver.findElement(By.id("currentAddress")).sendKeys("Pune");
        driver.findElement(By.id("permanentAddress")).sendKeys("Solapur");

        click(By.id("submit"));

        String result = driver.findElement(By.id("output")).getText();
        System.out.println("\nText Box Result:\n" + result);
    }
    private void testCheckBox() {
        driver.get("https://demoqa.com/checkbox");

        By homeCheckBox = By.cssSelector(
                "span.rc-tree-checkbox[aria-label='Select Home']");

        WebElement checkBox = wait.until(
                ExpectedConditions.elementToBeClickable(homeCheckBox));

        scrollTo(checkBox);
        checkBox.click();

        wait.until(ExpectedConditions.attributeToBe(
                homeCheckBox, "aria-checked", "true"));

        System.out.println("\nCheck Box Selected: "
                + driver.findElement(homeCheckBox)
                        .getAttribute("aria-checked"));
    }
    // ELEMENTS -> Radio Button
    private void testRadioButton() {
        driver.get("https://demoqa.com/radio-button");

        By yesRadio = By.id("yesRadio");

        click(By.cssSelector("label[for='yesRadio']"));

        wait.until(d -> Boolean.TRUE.equals(
                ((JavascriptExecutor) d).executeScript(
                        "return arguments[0].checked;",
                        d.findElement(yesRadio))));

        System.out.println("\nRadio Button Selected: Yes");
    }

    // FORMS -> Practice Form
    private void testPracticeForm() {
        driver.get("https://demoqa.com/automation-practice-form");

        driver.findElement(By.id("firstName")).sendKeys("Siddhu");
        driver.findElement(By.id("lastName")).sendKeys("Joge");
        driver.findElement(By.id("userEmail")).sendKeys("jogesiddhu02@gmail.com");
        click(By.cssSelector("label[for='gender-radio-1']"));
        driver.findElement(By.id("userNumber")).sendKeys("9876543210");

        driver.findElement(By.id("subjectsInput")).sendKeys("Maths");
        driver.findElement(By.id("subjectsInput")).sendKeys(org.openqa.selenium.Keys.ENTER);

        click(By.cssSelector("label[for='hobbies-checkbox-1']"));
        driver.findElement(By.id("currentAddress")).sendKeys("Pune, Maharashtra");

        click(By.id("submit"));

        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("modal-content")));

        System.out.println("\nPractice Form submitted: "
                + modal.getText().contains("Thanks for submitting the form"));

        click(By.id("closeLargeModal"));
    }

    // ELEMENTS -> Buttons
    private void testButtons() {
        driver.get("https://demoqa.com/buttons");

        Actions actions = new Actions(driver);

        WebElement doubleClickButton = driver.findElement(By.id("doubleClickBtn"));
        scrollTo(doubleClickButton);
        actions.doubleClick(doubleClickButton).perform();
        System.out.println("\nDouble Click: "
                + driver.findElement(By.id("doubleClickMessage")).getText());

        WebElement rightClickButton = driver.findElement(By.id("rightClickBtn"));
        actions.contextClick(rightClickButton).perform();
        System.out.println("Right Click: "
                + driver.findElement(By.id("rightClickMessage")).getText());

        click(By.xpath("//button[text()='Click Me']"));
        System.out.println("Normal Click: "
                + driver.findElement(By.id("dynamicClickMessage")).getText());
    }

    // ALERTS, FRAME & WINDOWS -> Alerts
    private void testAlerts() {
        driver.get("https://demoqa.com/alerts");

        click(By.id("alertButton"));
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        System.out.println("\nSimple Alert: " + alert.getText());
        alert.accept();

        click(By.id("timerAlertButton"));
        alert = wait.until(ExpectedConditions.alertIsPresent());
        System.out.println("Delayed Alert: " + alert.getText());
        alert.accept();

        click(By.id("confirmButton"));
        alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.dismiss();
        System.out.println("Confirm Alert: "
                + driver.findElement(By.id("confirmResult")).getText());

        click(By.id("promtButton"));
        alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.sendKeys("Siddhu Joge");
        alert.accept();
        System.out.println("Prompt Alert: "
                + driver.findElement(By.id("promptResult")).getText());
    }

    // ELEMENTS -> Dynamic Properties
    private void testDynamicProperties() {
        driver.get("https://demoqa.com/dynamic-properties");

        // Capture this immediately—before the five-second dynamic change.
        By colorChange = By.id("colorChange");
        String initialClass = driver.findElement(colorChange)
                .getAttribute("class");

        wait.until(ExpectedConditions.elementToBeClickable(By.id("enableAfter")));
        System.out.println("\nEnabled Button: "
                + driver.findElement(By.id("enableAfter")).isEnabled());

        wait.until(d -> !d.findElement(colorChange)
                .getAttribute("class").equals(initialClass));

        System.out.println("Colour Changed Button: "
                + driver.findElement(colorChange).getAttribute("class"));

        By visibleAfter = By.id("visibleAfter");
        wait.until(ExpectedConditions.visibilityOfElementLocated(visibleAfter));

        System.out.println("Visible Button: "
                + driver.findElement(visibleAfter).isDisplayed());
    }

    private void click(By locator) {
        WebElement element = wait.until(
                ExpectedConditions.elementToBeClickable(locator));

        scrollTo(element);

        try {
            element.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", element);
        }
    }

    private void scrollTo(WebElement element) {
        js.executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", element);
    }
}