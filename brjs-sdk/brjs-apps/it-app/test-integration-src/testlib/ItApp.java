package testlib;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.*;

import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ItApp {
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

    public String extractColourForItblade()
    {
        WebElement itBladeDiv = this.driver.findElement(By.className("itbladeset-itblade-blade"));
        return itBladeDiv.getCssValue("color");
    }

    public String getTableValue(String key)
    {
        return outputTable.get(key);
    }
}