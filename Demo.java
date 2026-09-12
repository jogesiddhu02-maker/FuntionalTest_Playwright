package Com.DemoQATesting;

import java.time.Duration;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class DemoQATest {

    private WebDriver driver;
    private WebDriverWait wait;
    private JavascriptExecutor js;

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));
        js = (JavascriptExecutor) driver;

        driver.get("https://demoqa.com/");
        System.out.println("Title: " + driver.getTitle());
    }

    // ELEMENTS -> Text Box
    @Test(priority = 1)
    public void testTextBox() {
        driver.get("https://demoqa.com/text-box");
        System.out.println("URL: " + driver.getCurrentUrl());

        driver.findElement(By.id("userName")).sendKeys("Siddhu Joge");
        driver.findElement(By.id("userEmail")).sendKeys("jogesiddhu02@gmail.com");
        driver.findElement(By.id("currentAddress")).sendKeys("Pune");
        driver.findElement(By.id("permanentAddress")).sendKeys("Solapur");

        click(By.id("submit"));

        String result = driver.findElement(By.id("output")).getText();
        System.out.println("\nText Box Result:\n" + result);

        Assert.assertTrue(result.contains("Siddhu Joge"),
                "Text Box output did not contain the submitted name");
    }

    // ELEMENTS -> Check Box
    @Test(priority = 2)
    public void testCheckBox() {
        driver.get("https://demoqa.com/checkbox");

        By homeCheckBox = By.cssSelector(
                "span.rc-tree-checkbox[aria-label='Select Home']");

        WebElement checkBox = wait.until(
                ExpectedConditions.elementToBeClickable(homeCheckBox));

        scrollTo(checkBox);
        checkBox.click();

        wait.until(ExpectedConditions.attributeToBe(
                homeCheckBox, "aria-checked", "true"));

        String checkedState = driver.findElement(homeCheckBox).getAttribute("aria-checked");
        System.out.println("\nCheck Box Selected: " + checkedState);

        Assert.assertEquals(checkedState, "true", "Home checkbox was not checked");
    }

    // ELEMENTS -> Radio Button
    @Test(priority = 3)
    public void testRadioButton() {
        driver.get("https://demoqa.com/radio-button");

        By yesRadio = By.id("yesRadio");

        click(By.cssSelector("label[for='yesRadio']"));

        wait.until(d -> Boolean.TRUE.equals(
                ((JavascriptExecutor) d).executeScript(
                        "return arguments[0].checked;",
                        d.findElement(yesRadio))));

        System.out.println("\nRadio Button Selected: Yes");

        boolean isChecked = (Boolean) js.executeScript(
                "return arguments[0].checked;", driver.findElement(yesRadio));
        Assert.assertTrue(isChecked, "Yes radio button was not selected");
    }

    // FORMS -> Practice Form
    @Test(priority = 4)
    public void testPracticeForm() {
        driver.get("https://demoqa.com/automation-practice-form");

        driver.findElement(By.id("firstName")).sendKeys("Siddhu");
        driver.findElement(By.id("lastName")).sendKeys("Joge");
        driver.findElement(By.id("userEmail")).sendKeys("jogesiddhu02@gmail.com");
        click(By.cssSelector("label[for='gender-radio-1']"));
        driver.findElement(By.id("userNumber")).sendKeys("9876543210");

        driver.findElement(By.id("subjectsInput")).sendKeys("Maths");
        driver.findElement(By.id("subjectsInput")).sendKeys(Keys.ENTER);

        click(By.cssSelector("label[for='hobbies-checkbox-1']"));
        driver.findElement(By.id("currentAddress")).sendKeys("Pune, Maharashtra");

        click(By.id("submit"));

        WebElement modal = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.className("modal-content")));

        boolean submitted = modal.getText().contains("Thanks for submitting the form");
        System.out.println("\nPractice Form submitted: " + submitted);

        click(By.id("closeLargeModal"));

        Assert.assertTrue(submitted, "Practice form submission confirmation not shown");
    }

    // ELEMENTS -> Buttons
    @Test(priority = 5)
    public void testButtons() {
        driver.get("https://demoqa.com/buttons");

        Actions actions = new Actions(driver);

        WebElement doubleClickButton = driver.findElement(By.id("doubleClickBtn"));
        scrollTo(doubleClickButton);
        actions.doubleClick(doubleClickButton).perform();
        String doubleClickMsg = driver.findElement(By.id("doubleClickMessage")).getText();
        System.out.println("\nDouble Click: " + doubleClickMsg);

        WebElement rightClickButton = driver.findElement(By.id("rightClickBtn"));
        actions.contextClick(rightClickButton).perform();
        String rightClickMsg = driver.findElement(By.id("rightClickMessage")).getText();
        System.out.println("Right Click: " + rightClickMsg);

        click(By.xpath("//button[text()='Click Me']"));
        String normalClickMsg = driver.findElement(By.id("dynamicClickMessage")).getText();
        System.out.println("Normal Click: " + normalClickMsg);

        Assert.assertTrue(doubleClickMsg.contains("double click"), "Double click message missing");
        Assert.assertTrue(rightClickMsg.contains("right click"), "Right click message missing");
        Assert.assertTrue(normalClickMsg.contains("click"), "Normal click message missing");
    }

    // ALERTS, FRAME & WINDOWS -> Alerts
    @Test(priority = 6)
    public void testAlerts() {
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
        String confirmResult = driver.findElement(By.id("confirmResult")).getText();
        System.out.println("Confirm Alert: " + confirmResult);

        click(By.id("promtButton"));
        alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.sendKeys("Siddhu Joge");
        alert.accept();
        String promptResult = driver.findElement(By.id("promptResult")).getText();
        System.out.println("Prompt Alert: " + promptResult);

        Assert.assertTrue(confirmResult.contains("Cancel"), "Confirm dismiss result not as expected");
        Assert.assertTrue(promptResult.contains("Siddhu Joge"), "Prompt result did not contain entered text");
    }

    // ELEMENTS -> Dynamic Properties
    @Test(priority = 7)
    public void testDynamicProperties() {
        driver.get("https://demoqa.com/dynamic-properties");

        // Capture this immediately—before the five-second dynamic change.
        By colorChange = By.id("colorChange");
        String initialClass = driver.findElement(colorChange).getAttribute("class");

        wait.until(ExpectedConditions.elementToBeClickable(By.id("enableAfter")));
        boolean enabled = driver.findElement(By.id("enableAfter")).isEnabled();
        System.out.println("\nEnabled Button: " + enabled);

        wait.until(d -> !d.findElement(colorChange)
                .getAttribute("class").equals(initialClass));

        System.out.println("Colour Changed Button: "
                + driver.findElement(colorChange).getAttribute("class"));

        By visibleAfter = By.id("visibleAfter");
        wait.until(ExpectedConditions.visibilityOfElementLocated(visibleAfter));

        boolean visible = driver.findElement(visibleAfter).isDisplayed();
        System.out.println("Visible Button: " + visible);

        Assert.assertTrue(enabled, "Button did not become enabled");
        Assert.assertTrue(visible, "Button did not become visible");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
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
