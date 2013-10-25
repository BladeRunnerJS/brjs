import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.caplin.cutlass.testIntegration.WebDriverProvider;

@RunWith(BlockJUnit4ClassRunner.class)
public class WorkbenchIntegrationTest
{
	
	private static WebDriver driver;
	private static String baseUrl;
	
	@BeforeClass
	public static void setupClass() throws Exception
	{
		driver = WebDriverProvider.getDriver("firefox-webdriver");
		
		baseUrl = WebDriverProvider.getBaseUrl("/dashboard/dashboard-bladeset/blades/app/workbench/");

		//TODO: this wont be needed when we are using SL4Bv5
		if (baseUrl.contains("localhost") && !baseUrl.contains(".caplin.com"))
		{
			baseUrl = baseUrl.replace("localhost", "localhost.caplin.com");
		}	
		driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
	}

	@AfterClass
	public static void tearDownClass()
	{ 	
		WebDriverProvider.closeDriver(driver);
	}
	
	
	@Test
	public void testWorkbenchLoads() throws Exception
	{
		loadDashboard();
		openAndThenCloseNewAppDialog("novotrader", "novox");
		loadSupportPage();
	}
	
	
	private void loadSupportPage() 
	{
		driver.get(baseUrl + "/#note/latest");
		//accept leave page alert
		Alert alert = driver.switchTo().alert(); 
		alert.accept(); 
		assertTrue(driver.findElement(By.id("releaseNoteScreen")).isDisplayed());
	}

	private void loadDashboard() throws InterruptedException 
	{
		driver.manage().deleteAllCookies();
		driver.get(baseUrl);
		
		assertPageTitleIs("Workbench");
	}

	private void openAndThenCloseNewAppDialog(String appName, String appNamespace) throws InterruptedException
	{
		WebElement newAppButton = driver.findElements(By.tagName("button")).get(0);
		assertTrue(newAppButton.getText().equals("New App"));
		newAppButton.click();
		
		List<WebElement> inputFields = driver.findElement(By.id("modalDialog")).findElements(By.tagName("input"));
		inputFields.get(0).sendKeys(appName);
		inputFields.get(1).sendKeys(appNamespace);
		
		assertTrue(driver.findElement(By.id("modalDialog")).isDisplayed());
		driver.findElement(By.id("fancybox-close")).click();
		// TODO: remove the need for a sleep -- does webdriver have the equivalent of waitFor?
		Thread.sleep(100);
		assertTrue(driver.findElement(By.id("modalDialog")).isDisplayed() == false);
	}
	
	private void assertPageTitleIs(String title) throws InterruptedException
	{
		for (int i = 0; i < 10; i++) 
		{
			if (!driver.getTitle().equals(""))
			{
				break;
			}
			Thread.sleep(1000);
		}
		assertEquals(title, driver.getTitle());
	}
	
}