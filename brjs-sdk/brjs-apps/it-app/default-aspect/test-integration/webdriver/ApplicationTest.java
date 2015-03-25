import org.openqa.selenium.WebDriver;
import org.bladerunnerjs.legacy.testIntegration.WebDriverProvider;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ApplicationTest {

    private static WebDriver driver;
    private static String baseUrl = "http://localhost:7070/it-app";
    private static ItApp itapp;

    @BeforeClass
    public static void before() throws Exception {
        driver = new FirefoxDriver();
        driver.get(baseUrl + "/login/j_security_check?isLoginPage=login-page&j_username=user&j_password=password");
        driver.get(baseUrl);
        itapp = new ItApp(driver, baseUrl);
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
}

class ItApp {
    private HashMap<String, String> outputTable;
    private WebDriver driver;

    public ItApp(WebDriver wd, String baseURL) throws Exception {
        this.outputTable = new HashMap<>();
        this.driver = wd;

        URLConnection con = new URL(baseURL+";jsessionid=4ujetr900i6azguu3fr82krx").openConnection();
        con.setRequestProperty("Cookie", "JSESSIONID=4ujetr900i6azguu3fr82krx;");
        con.connect();
        con = new URL(baseURL + "/login/j_security_check?isLoginPage=login-page&j_username=user&j_password=password").openConnection();
        con.setRequestProperty("Cookie", "JSESSIONID=4ujetr900i6azguu3fr82krx;");
        con.connect();
    }

    public void parseOutputTable()
    {
        WebElement uiTable = this.driver.findElement(By.id("outputTable"));

        List<WebElement> tableEntries =  uiTable.findElements(By.tagName("td"));
        Iterator<WebElement> it = tableEntries.iterator();

        while(it.hasNext()) {
            String key = it.next().getText();
            String value;
            if (it.hasNext()) {
                value = it.next().getText();
                this.outputTable.put(key, value);
            }
        }
    }

    public long computeSizeCSSBackgroundImage()
    {
        WebElement imageBackgroundDiv = this.driver.findElement(By.className("image-background"));
        String backgroundImageStyle =  imageBackgroundDiv.getCssValue("background-image");
        String backgroundImageURL = "";
        if(backgroundImageStyle.matches("url\\(\".*v/dev/cssresource/aspect_default/theme_common/images/backgroundImage.png\"\\)"))
        {
            backgroundImageURL = backgroundImageStyle.substring(5, backgroundImageStyle.length() - 2);
        }
        return computeSizeOfImageUsingJS(backgroundImageURL);
    }

    public long computeSizeUnbundledImage()
    {
        String unbundledImageURL = "";
        List<WebElement> imgs = this.driver.findElements(By.tagName("img"));
        for(WebElement we : imgs)
        {
            String srcAttribute = we.getAttribute("src");
            if(srcAttribute.matches(".*v/dev/unbundled-resources/br-logo\\.png"))
            {
                unbundledImageURL = srcAttribute;
            }
        }
        return  computeSizeOfImageUsingJS(unbundledImageURL);
    }

    private long computeSizeOfImageUsingJS(String imagesrc)
    {
        String javaScript = "var img = new Image();" +
                "img.src = \"" + imagesrc + "\";" +
                "return img.naturalWidth * img.naturalHeight;";
        return (long) ((JavascriptExecutor)this.driver).executeScript(javaScript);
    }

    public String getTableValue(String key)
    {
        return outputTable.get(key);
    }
}