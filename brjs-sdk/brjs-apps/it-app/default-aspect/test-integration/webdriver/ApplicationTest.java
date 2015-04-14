import org.openqa.selenium.WebDriver;
import org.bladerunnerjs.legacy.testIntegration.WebDriverProvider;
import org.junit.*;
import testlib.ItApp;
import org.openqa.selenium.firefox.FirefoxDriver;

public class ApplicationTest {

    private static WebDriver driver;
    private static String baseUrl = WebDriverProvider.getBaseUrl("/it-app");
    private static ItApp itapp;

    @BeforeClass
    public static void before() throws Exception {
        driver = new FirefoxDriver();
        driver.get(baseUrl + "/login/j_security_check?isLoginPage=login-page&j_username=user&j_password=password");
        driver.get(baseUrl);
        itapp = new ItApp(driver);
        itapp.parseOutputTable();
    }

    @AfterClass
    public static void after() {
        WebDriverProvider.closeDriver(driver);
    }

    @Test
    public void i18n() throws Exception {
        Assert.assertEquals("Hello from i18n", itapp.getTableValue("i18n"));
    }

    @Test
    public void aliasing() throws Exception {
        Assert.assertEquals("Hello from an aliased class", itapp.getTableValue("aliasing"));
    }

    @Test
    public void aliasingImplementsConstraint() throws Exception {
        Assert.assertEquals("Aliasing successfully prevented", itapp.getTableValue("aliasing-implement-fail"));
    }

    @Test
    public void namedspacedJS() throws Exception {
        Assert.assertEquals("Hello from a namespaced-js lib", itapp.getTableValue("namespaced-js"));
    }

    @Test
    public void commonJS() throws Exception {
        Assert.assertEquals("Hello from a common-js lib", itapp.getTableValue("common-js"));
    }

    @Test
    public void thirdParty() throws Exception {
        Assert.assertEquals("Hello from a third-party lib", itapp.getTableValue("third-party-lib"));
    }

    @Test
    public void jndi() throws Exception {
        Assert.assertEquals("Hello from JNDI", itapp.getTableValue("jndi"));
    }

    @Test
    public void xmlBundling() throws Exception {
        Assert.assertEquals("Hello from bundled XML", itapp.getTableValue("xml-bundle"));
    }

    @Test
    public void unbundledImageIsDisplayedCorrectly() throws Exception {
        Assert.assertEquals(40000L, itapp.computeSizeUnbundledImage());
    }

    @Test
    public void cssBackgroundImageIsDisplayedCorrectly() throws Exception {
        Assert.assertEquals(46265L, itapp.computeSizeCSSBackgroundImage());
    }

    @Test
    public void themeIsCommonOnly() throws Exception {
        String colour = itapp.extractColourForItblade();
        boolean isColourBlack = colour.equals("rgba(0, 0, 0, 1)") || colour.equals("black") ||
                colour.equals("#000") || colour.equals("#000000");
        Assert.assertTrue(isColourBlack);
    }
}
