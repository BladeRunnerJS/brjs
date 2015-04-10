import org.bladerunnerjs.legacy.testIntegration.WebDriverProvider;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

public class LoginAspectTest {

    private static WebDriver driver;
    private static String baseUrl = WebDriverProvider.getBaseUrl("/it-app");

    public LoginAspectTest() throws Exception {
        driver = new FirefoxDriver();
        driver.get(baseUrl + "/login");
    }

    @AfterClass
    public static void testsFinish() {
        WebDriverProvider.closeDriver(driver);
    }

    private static boolean isLoginPage() {
        try {
            WebElement loginForm = driver.findElement(By.id("formLogin"));
            String submitButtonText = loginForm.findElement(By.tagName("button")).getText();
            int numberOfInputElements = loginForm.findElements(By.tagName("input")).size();
            if (numberOfInputElements != 3 ||
                    !submitButtonText.equals("Login")) {
                return false;
            }
        } catch (NoSuchElementException ex) {
            return false;
        }
        return true;
    }

    @Test
    public void theLoginAspectPageIsServedCorrectly() throws Exception {
        Assert.assertTrue(isLoginPage());
    }

}


