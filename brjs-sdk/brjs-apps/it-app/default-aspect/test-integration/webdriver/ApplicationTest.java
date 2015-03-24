import org.apache.commons.io.IOUtils;
import org.openqa.selenium.WebDriver;
import org.bladerunnerjs.legacy.testIntegration.WebDriverProvider;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        itapp.parseImageData();
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
        Assert.assertEquals(2477, itapp.getUnbundledImageSize());
    }

    @Test
    public void cssBackgroundImageIsDisplayedCorrectly() throws Exception {
        Assert.assertEquals(7175, itapp.getBackgroundImageSize());
    }
}

class ItApp {
    private HashMap<String, String> outputTable;
    private WebDriver driver;
    private String baseURL;

    private int sizeOfCSSBackgroundImage = -1;
    private int sizeOfUnbundledImage = -1;

    public ItApp(WebDriver wd, String baseURL) throws Exception {
        this.outputTable = new HashMap<>();
        this.driver = wd;
        this.baseURL = baseURL;



        URLConnection con = new URL(baseURL+";jsessionid=1nocri2exl1te13sm8ginkn7iu").openConnection();
        con.setRequestProperty("Cookie", "JSESSIONID=1nocri2exl1te13sm8ginkn7iu");
        con.connect();
        con = new URL(baseURL + "/login/j_security_check?isLoginPage=login-page&j_username=user&j_password=password").openConnection();
        con.setRequestProperty("Cookie", "JSESSIONID=1nocri2exl1te13sm8ginkn7iu");
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

    public void parseImageData() {
        String unbundledImageURL = null;
        String backgroundImageURL = null;
        List<WebElement> imgs = this.driver.findElements(By.tagName("img"));
        for(WebElement we : imgs)
        {
            String srcAttribute = we.getAttribute("src");
            if(srcAttribute.matches(".*v/dev/unbundled-resources/br-logo\\.png"))
            {
                unbundledImageURL = srcAttribute;
            }
        }
        WebElement imageBackgroundDiv = this.driver.findElement(By.className("image-background"));
        String backgroundImageStyle =  imageBackgroundDiv.getCssValue("background-image");
        if(backgroundImageStyle.matches("url\\(\".*v/dev/cssresource/aspect_default/theme_common/images/backgroundImage.png\"\\)"))
        {
            backgroundImageURL = backgroundImageStyle.substring(5, backgroundImageStyle.length() - 2);
        }
        this.sizeOfUnbundledImage = computeSizeOfURL(unbundledImageURL);
        this.sizeOfCSSBackgroundImage = computeSizeOfURL(backgroundImageURL);
    }

    private int computeSizeOfURL(String urlString)
    {
        try {
            URLConnection con = new URL(urlString).openConnection();
            con.setRequestProperty("Cookie", "JSESSIONID=1nocri2exl1te13sm8ginkn7iu");
            return IOUtils.toByteArray(con.getInputStream()).length;
        } catch (IOException e) {
            return -1;
        }
    }

    public String getTableValue(String key)
    {
        return outputTable.get(key);
    }

    public int getBackgroundImageSize() {
        return sizeOfCSSBackgroundImage;
    }

    public int getUnbundledImageSize() {
        return sizeOfUnbundledImage;
    }
}