package Day1;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.MouseButton;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.WaitUntilState;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class DemoQATest {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeClass
    public void setUp() {
        playwright = Playwright.create();

        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setArgs(List.of("--start-maximized"))
        );

        // setViewportSize(null) lets the page use the real maximized window size
        context = browser.newContext(
                new Browser.NewContextOptions().setViewportSize(null));

        // Block ad / tracking requests that slow the site down and cover buttons
        context.route(
                "**/*",
                route -> {
                    String url = route.request().url();
                    if (url.contains("googlesyndication")
                            || url.contains("doubleclick")
                            || url.contains("googletagmanager")
                            || url.contains("google-analytics")
                            || url.contains("adsbygoogle")
                            || url.contains("adplus")) {
                        route.abort();
                    } else {
                        route.resume();
                    }
                });

        page = context.newPage();
        page.setDefaultTimeout(15_000);

        open("https://demoqa.com/");
        System.out.println("Title: " + page.title());
    }

    // ELEMENTS -> Text Box
    @Test(priority = 1)
    public void testTextBox() {
        open("https://demoqa.com/text-box");
        System.out.println("URL: " + page.url());

        page.locator("#userName").fill("Siddhu Joge");
        page.locator("#userEmail").fill("jogesiddhu02@gmail.com");
        page.locator("#currentAddress").fill("Pune");
        page.locator("#permanentAddress").fill("Solapur");

        click("#submit");

        String result = page.locator("#output").innerText();
        System.out.println("\nText Box Result:\n" + result);

        Assert.assertTrue(result.contains("Siddhu Joge"),
                "Text Box output did not contain the submitted name");
    }

    // ELEMENTS -> Check Box
 // ELEMENTS -> Check Box
    @Test(priority = 2)
    public void testCheckBox() {
        open("https://demoqa.com/checkbox");

        Locator homeCheckBox =
                page.locator("span.rc-tree-checkbox[aria-label='Select Home']");

        homeCheckBox.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));
        homeCheckBox.scrollIntoViewIfNeeded();
        homeCheckBox.click();

        String checkedState = homeCheckBox.getAttribute("aria-checked");
        String classes = homeCheckBox.getAttribute("class");
        System.out.println("\nCheck Box Selected: " + checkedState + " | class: " + classes);

        boolean checked = "true".equals(checkedState)
                || (classes != null && classes.contains("rc-tree-checkbox-checked"));

        Assert.assertTrue(checked, "Home checkbox was not checked");
    }

    // ELEMENTS -> Radio Button
    @Test(priority = 3)
    public void testRadioButton() {
        open("https://demoqa.com/radio-button");

        click("label[for='yesRadio']");

        boolean selected = page.locator("#yesRadio").isChecked();
        System.out.println("\nRadio Button Selected (Yes): " + selected);

        Assert.assertTrue(selected, "Yes radio button was not selected");
        Assert.assertTrue(page.locator(".text-success").innerText().contains("Yes"),
                "Success message did not show 'Yes'");
    }

    // FORMS -> Practice Form
    @Test(priority = 4)
    public void testPracticeForm() {
        open("https://demoqa.com/automation-practice-form");

        page.locator("#firstName").fill("Siddhu");
        page.locator("#lastName").fill("Joge");
        page.locator("#userEmail").fill("jogesiddhu02@gmail.com");
        click("label[for='gender-radio-1']");
        page.locator("#userNumber").fill("9876543210");

        page.locator("#subjectsInput").fill("Maths");
        page.locator("#subjectsInput").press("Enter");

        click("label[for='hobbies-checkbox-1']");
        page.locator("#currentAddress").fill("Pune, Maharashtra");

        click("#submit");

        Locator modal = page.locator(".modal-content");
        modal.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));

        boolean submitted =
                modal.innerText().contains("Thanks for submitting the form");

        System.out.println("\nPractice Form submitted: " + submitted);

        // force-click: the close button sits inside an animated modal
        page.locator("#closeLargeModal").click(
                new Locator.ClickOptions().setForce(true));

        Assert.assertTrue(submitted,
                "Practice form submission confirmation not shown");
    }

    // ELEMENTS -> Buttons
    @Test(priority = 5)
    public void testButtons() {
        open("https://demoqa.com/buttons");

        Locator doubleClickButton = page.locator("#doubleClickBtn");
        doubleClickButton.scrollIntoViewIfNeeded();
        doubleClickButton.dblclick();

        String doubleClickMsg = page.locator("#doubleClickMessage").innerText();
        System.out.println("\nDouble Click: " + doubleClickMsg);

        page.locator("#rightClickBtn").click(
                new Locator.ClickOptions().setButton(MouseButton.RIGHT)
        );

        String rightClickMsg = page.locator("#rightClickMessage").innerText();
        System.out.println("Right Click: " + rightClickMsg);

        click("//button[text()='Click Me']");

        String normalClickMsg = page.locator("#dynamicClickMessage").innerText();
        System.out.println("Normal Click: " + normalClickMsg);

        Assert.assertTrue(doubleClickMsg.contains("double click"),
                "Double click message missing");
        Assert.assertTrue(rightClickMsg.contains("right click"),
                "Right click message missing");
        Assert.assertTrue(normalClickMsg.contains("dynamic click"),
                "Normal click message missing");
    }

    // ALERTS, FRAME & WINDOWS -> Alerts
    @Test(priority = 6)
    public void testAlerts() {
        open("https://demoqa.com/alerts");

        // 1. Simple alert
        AtomicReference<String> simpleMsg = new AtomicReference<>();
        page.onceDialog(dialog -> {
            simpleMsg.set(dialog.message());
            dialog.accept();
        });
        click("#alertButton");
        System.out.println("\nSimple Alert: " + simpleMsg.get());

        // 2. Alert that appears after 5 seconds
        AtomicReference<String> delayedMsg = new AtomicReference<>();
        page.onceDialog(dialog -> {
            delayedMsg.set(dialog.message());
            dialog.accept();
        });
        click("#timerAlertButton");
        page.waitForTimeout(6_000); // lets Playwright process the dialog event
        System.out.println("Delayed Alert: " + delayedMsg.get());

        // 3. Confirm box -> Cancel
        page.onceDialog(dialog -> dialog.dismiss());
        click("#confirmButton");
        String confirmResult = page.locator("#confirmResult").innerText();
        System.out.println("Confirm Alert: " + confirmResult);

        // 4. Prompt box -> enter text
        page.onceDialog(dialog -> dialog.accept("Siddhu Joge"));
        click("#promtButton");
        String promptResult = page.locator("#promptResult").innerText();
        System.out.println("Prompt Alert: " + promptResult);

        Assert.assertNotNull(simpleMsg.get(), "Simple alert did not appear");
        Assert.assertNotNull(delayedMsg.get(), "Delayed alert did not appear");
        Assert.assertTrue(confirmResult.contains("Cancel"),
                "Confirm dismiss result not as expected");
        Assert.assertTrue(promptResult.contains("Siddhu Joge"),
                "Prompt result did not contain entered text");
    }

    // ELEMENTS -> Dynamic Properties
    @Test(priority = 7)
    public void testDynamicProperties() {
        open("https://demoqa.com/dynamic-properties");

        Locator colorChange = page.locator("#colorChange");
        String initialClass = colorChange.getAttribute("class");

        page.waitForFunction(
                "() => document.querySelector('#enableAfter')?.disabled === false"
        );

        boolean enabled = page.locator("#enableAfter").isEnabled();
        System.out.println("\nEnabled Button: " + enabled);

        page.waitForFunction(
                "(initialClass) => document.querySelector('#colorChange')"
                        + "?.getAttribute('class') !== initialClass",
                initialClass
        );

        System.out.println("Colour Changed Button: "
                + colorChange.getAttribute("class"));

        Locator visibleAfter = page.locator("#visibleAfter");
        visibleAfter.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));

        boolean visible = visibleAfter.isVisible();
        System.out.println("Visible Button: " + visible);

        Assert.assertTrue(enabled, "Button did not become enabled");
        Assert.assertTrue(visible, "Button did not become visible");
    }

    @AfterClass
    public void tearDown() {
        if (context != null) {
            context.close();
        }
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    // ---------- helpers ----------

    /** Navigates without waiting for slow ads, then removes overlays. */
    private void open(String url) {
        page.navigate(url,
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));
        removeOverlays();
    }

    /** Removes the fixed banner / footer that can intercept clicks. */
    private void removeOverlays() {
        page.evaluate("() => {"
                + "document.querySelectorAll('#fixedban, footer, iframe[id^=\"google_ads\"], .adsbygoogle')"
                + ".forEach(e => e.remove());"
                + "}");
    }

    private void click(String selector) {
        Locator element = page.locator(selector);
        element.scrollIntoViewIfNeeded();
        element.click();
    }
}
