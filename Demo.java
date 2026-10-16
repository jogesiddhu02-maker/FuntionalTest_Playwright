
package Day1;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Route;
import com.microsoft.playwright.options.WaitUntilState;

public class PromptAlert {

    public static void main(String[] args) {

        Playwright playwright = Playwright.create();
        Browser browser = null;

        try {
            browser = playwright.chromium()
                    .launch(new BrowserType.LaunchOptions()
                    .setHeadless(false));

            BrowserContext context = browser.newContext();

            // Block requests to common advertising services
            context.route("**/*", route -> {
                String url = route.request().url().toLowerCase();

                if (url.contains("doubleclick.net")
                        || url.contains("googlesyndication.com")
                        || url.contains("googleadservices.com")
                        || url.contains("googletagservices.com")
                        || url.contains("adservice.google.com")) {

                    System.out.println("Blocked ad: " + url);
                    route.abort();

                } else {
                    route.resume();
                }
            });

            Page page = context.newPage();
            page.setDefaultTimeout(15000);

            // Close unexpected new tabs, such as advertisement popups
            page.onPopup(popup -> {
                System.out.println(
                        "Unexpected popup detected: " + popup.url()
                );
                popup.close();
            });

            // Open the website
            page.navigate(
                    "https://demo.automationtesting.in/Alerts.html",
                    new Page.NavigateOptions()
                            .setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
            );

            System.out.println("Current page: " + page.url());
            System.out.println("Website navigation completed");

            // Select the Textbox alert tab
            page.locator("a[href='#Textbox']").click();

            // Register the prompt handler before clicking its button
            page.onDialog(dialog -> {
                System.out.println(
                        "Dialog message: " + dialog.message()
                );

                dialog.accept("Siddhu");
            });

            // Trigger the prompt
            page.locator("#Textbox button.btn-info").click();

            // Read the result
            String result = page.locator("#demo1").innerText();

            System.out.println("Result: " + result);

            if (!result.contains("Siddhu")) {
                throw new AssertionError(
                        "Expected name was not displayed: " + result
                );
            }

            System.out.println("TEST PASSED");

        } catch (Exception e) {
            System.out.println("TEST FAILED");
            e.printStackTrace();

        } finally {
            if (browser != null) {
                browser.close();
            }

            playwright.close();
        }
    }
}
