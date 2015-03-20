import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.WebDriver;

import com.caplin.cutlass.testIntegration.WebDriverProvider;

@RunWith(Parameterized.class)

public class IntegrationTestMain {

    private static WebDriver driver;
    private static String baseUrl;
    private static Utility utility;
    private static FxTradingPortal fxTradingPortal = null;
    private static TriggerManager triggerManager = null;
    private static String currentlyLoggedInUser = null;

    @Parameters
    public static Collection<Object[]> getUsers()
    {
        return Arrays.asList(new Object[][] {
                {"user1@caplin.com"},
                //	{"user2@caplin.com"}
        });
    }


    @AfterClass
    public static void tearDownClass()
    {
//        WebDriverProvider.closeDriver(driver);
    }

    @Before
    public void setUp() throws Exception
    {
//        triggerManager = new TriggerManager(driver);
//        triggerManager.createDummyTrigger();
    }


    @Test
    public void testNotificationsCanBeRemoved() throws Exception
    {

    }


}
