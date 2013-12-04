package com.caplin.cutlass.structure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import com.caplin.cutlass.util.FileUtility;
import com.caplin.cutlass.AppMetaData;
import com.caplin.cutlass.CutlassConfig;

public class AppMetaDataTest
{
	private static final String APPLICATIONS_DIR = "src/test/resources/ExampleAppStructure/" + CutlassConfig.APPLICATIONS_DIR;
	private static final File applicationDirectory = new File(APPLICATIONS_DIR + "/app1");
	private AppMetaData applicationMetaData;
	
	private File hiddenDir = new File(applicationDirectory, "a-aspect/themes/.hiddenDir");
	private File hiddenFile = new File(applicationDirectory, "a-aspect/themes/.hiddenFile");
	
	@Before
	public void setUp() {
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(APPLICATIONS_DIR)));
		applicationMetaData = new AppMetaData(BRJSAccessor.root.app("app1"));
	}
	
	@After
	public void tearDown()
	{
		if (hiddenDir.exists())
		{
			hiddenDir.delete();
		}
		if (hiddenFile.exists())
		{
			hiddenFile.delete();
		}
	}

	@Test
	public void testGetThemesWithHiddenFileAndFolder() throws IOException
	{
		createHiddenFileAndFolderInsideThemeFolder();
		
		List<String> applicationThemes = applicationMetaData.getThemes();
				
		assertEquals(4, applicationThemes.size());
		
		assertTrue(applicationThemes.contains("noir"));
		assertTrue(applicationThemes.contains("blue"));
		assertTrue(applicationThemes.contains("common"));
		assertTrue(applicationThemes.contains("purple"));
	}

	private void createHiddenFileAndFolderInsideThemeFolder() throws IOException 
	{
		if (!System.getProperty("os.name").toLowerCase().contains("windows"))
		{
			FileUtility.createHiddenFileAndFolder(hiddenDir.getParentFile());
			assertTrue(hiddenDir.isHidden());
			assertTrue(hiddenFile.isHidden());
		}
	}
	
	@Test
	public void testGetBrowsers()
	{
		List<String> applicationBrowsers = applicationMetaData.getBrowsers();
		
		assertEquals(3, applicationBrowsers.size());
		
		assertTrue(applicationBrowsers.contains("ie6"));
		assertTrue(applicationBrowsers.contains("ie7"));
		assertTrue(applicationBrowsers.contains("safari"));
				
	}
	
	@Test
	public void testGetLocales()
	{
		List<String> applicationLocales = applicationMetaData.getLocales();
		
		assertEquals(4, applicationLocales.size());
		
		assertTrue(applicationLocales.contains("de_DE"));
		assertTrue(applicationLocales.contains("en_UK"));
		assertTrue(applicationLocales.contains("en_US"));
		assertTrue(applicationLocales.contains("en_AU"));
	}
	
	@Test
	public void testGetImages()
	{
		List<File> applicationImages = applicationMetaData.getImages();
		
		assertEquals(7, applicationImages.size());
		
		assertTrue(applicationImages.contains(new File(APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/themes/blue/img/blue.png")));
		assertTrue(applicationImages.contains(new File(APPLICATIONS_DIR + "/app1/a-bladeset/themes/noir/noir.gif")));
		assertFalse(applicationImages.contains(new File(APPLICATIONS_DIR + "/app1/a-bladeset/themes/noir/notdefinedimageextension.bla")));
		assertTrue(applicationImages.contains(new File(APPLICATIONS_DIR + "/app1/another-aspect/themes/blue/img/extralevel/blue.jpg")));
		assertTrue(applicationImages.contains(new File(APPLICATIONS_DIR + "/app1/another-bladeset/blades/blade1/themes/noir/img/extralevel/levelup/noir.bmp")));

		assertTrue(applicationImages.contains(new File(APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/themes/blue/img/BLUE_UPPER.PNG")));
		assertTrue(applicationImages.contains(new File(APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/themes/blue/img/BLUE_UPPER.JPEG")));
		assertTrue(applicationImages.contains(new File(APPLICATIONS_DIR + "/app1/a-bladeset/blades/blade1/themes/blue/img/BLUE.JPEG")));
		
	}
	
	@Test
	public void testGetLanguages()
	{
		List<String> applicationLanguages = applicationMetaData.getLanguages();
		
		assertEquals(3, applicationLanguages.size());
		
		assertTrue(applicationLanguages.contains("en"));
		assertTrue(applicationLanguages.contains("de"));
		assertTrue(applicationLanguages.contains("es"));
	}
	
}
