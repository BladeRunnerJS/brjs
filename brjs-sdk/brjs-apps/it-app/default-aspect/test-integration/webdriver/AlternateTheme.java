import org.bladerunnerjs.legacy.testIntegration.WebDriverProvider;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import testlib.ItApp;

public class AlternateTheme
{
    private static WebDriver driver;
    private static String baseUrl = "http://localhost:7070/it-app";
    private static ItApp itapp;

    @BeforeClass
    public static void before() throws Exception {
        driver = new FirefoxDriver();
        driver.get(baseUrl + "/login/j_security_check?isLoginPage=login-page&j_username=user&j_password=password");
        String themedURL = baseUrl + "/?theme=alternate";
        driver.get(themedURL);
        itapp = new ItApp(driver, themedURL);
    }
    @AfterClass
    public static void after() {
        WebDriverProvider.closeDriver(driver);
    }

    @Test
    public void themingIsAppliedSuccessfully() {
        String colour = itapp.extractColourForItblade();
        boolean isColourRed = colour.equals("rgba(255, 0, 0, 1)") || colour.equals("red") ||
                colour.equals("#F00") || colour.equals("#FF0000");
        Assert.assertTrue(isColourRed);
    }
}
