package com.caplin.cutlass.command.war;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.LegacyFileBundlerPlugin;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import com.caplin.cutlass.AppMetaData;
import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.util.FileUtility;
import com.caplin.cutlass.bundler.BladeRunnerSourceFileProvider;
import com.caplin.cutlass.bundler.css.CssBundler;
import com.caplin.cutlass.bundler.html.HtmlBundler;
import com.caplin.cutlass.bundler.i18n.I18nBundler;
import com.caplin.cutlass.bundler.image.ImageBundler;
import com.caplin.cutlass.bundler.js.JsBundler;
import com.caplin.cutlass.bundler.thirdparty.ThirdPartyBundler;
import com.caplin.cutlass.bundler.xml.XmlBundler;
import com.caplin.cutlass.structure.BladerunnerConf;
import com.caplin.cutlass.structure.CutlassDirectoryLocator;

public class WarCommandUtilityTest
{
	private final static WarCommandUtility warCommandUtility = new WarCommandUtility();
	
	final private File sdkBaseDir = new File("src/test/resources/WarCommandUtilityTest/" + CutlassConfig.SDK_DIR);
	private File applicationToWarDirectory = new File(sdkBaseDir.getParent(), APPLICATIONS_DIR + "/emptytrader");
	private File applicationWEBINFDir = new File(applicationToWarDirectory, "WEB-INF");
	private AppMetaData appMetaData;
	private List<File> applicationAspects = CutlassDirectoryLocator.getApplicationAspects(applicationToWarDirectory);

	private File tempDirectoryForWar = null;
	private File loginAspectDirectory = null;
	private File defaultAspectDirectory = null;
	private File mobileAspectDirectory = null;
	
	private RandomAccessFile xmlReader;
	
	@Before
	public void setup() throws IOException
	{
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(sdkBaseDir));
		appMetaData = new AppMetaData(BRJSAccessor.root.app("emptytrader"));
		
		tempDirectoryForWar = FileUtility.createTemporaryDirectory("tempDirWar");
		loginAspectDirectory = new File(tempDirectoryForWar, "login-aspect");
		defaultAspectDirectory = new File(tempDirectoryForWar, "default-aspect");
		mobileAspectDirectory = new File(tempDirectoryForWar, "mobile-aspect");
		BladeRunnerSourceFileProvider.disableUsedBladesFiltering();
	}

	@After
	public void tearDown() throws IOException
	{
		if(xmlReader != null) //If test fails before closing this all subsequent tests fail so close in tearDown.
		{
			xmlReader.close();
		}
	}
	
	@Test
	public void testCopyWEBINFFolderToTemporaryDirectoryForWarCreation() throws Exception
	{
		String[] filesInTempDirectory = tempDirectoryForWar.list();
		
		assertEquals(0, filesInTempDirectory.length);
		
		warCommandUtility.copyWEBINFFolderToTemporaryDirectoryForWarCreation(applicationWEBINFDir, tempDirectoryForWar);
		
		filesInTempDirectory = tempDirectoryForWar.list();
		
		assertEquals(1, filesInTempDirectory.length);
		assertEquals("WEB-INF", filesInTempDirectory[0].toString());
	}
	
	@Test
	public void testCopyAppConfFolderToTemporaryDirectoryForWarCreation() throws Exception
	{
		String[] filesInTempDirectory = tempDirectoryForWar.list();
		
		assertEquals(0, filesInTempDirectory.length);
		
		warCommandUtility.copyAppConf(applicationToWarDirectory, tempDirectoryForWar);
		
		filesInTempDirectory = tempDirectoryForWar.list();
		
		assertEquals(1, filesInTempDirectory.length);
		assertEquals("app.conf", filesInTempDirectory[0].toString());
	}
	
	@Test
	public void testDeleteJettyEnvXmlFromTemporaryDirectoryForWarCreation() throws Exception
	{
		File jettyEnvXmlFile = new File(tempDirectoryForWar, "WEB-INF/jetty-env.xml");
		
		assertFalse(jettyEnvXmlFile.exists());
		
		warCommandUtility.copyWEBINFFolderToTemporaryDirectoryForWarCreation(applicationWEBINFDir, tempDirectoryForWar);
		
		assertTrue(jettyEnvXmlFile.exists());
		
		warCommandUtility.deleteJettyEnvConfigurationFromTemporaryDirectoryForWarCreation(tempDirectoryForWar);
		
		assertFalse(jettyEnvXmlFile.exists());
	}
	
	@Test
	public void testDeleteBladeRunnerDevServletsFromTemporaryDirectoryForWarCreation() throws Exception
	{
		File bladeRunnerDevServletsJar = new File(tempDirectoryForWar, "WEB-INF/lib/bladerunner-dev-servlets.jar");
		
		assertFalse(bladeRunnerDevServletsJar.exists());
		
		warCommandUtility.copyWEBINFFolderToTemporaryDirectoryForWarCreation(applicationWEBINFDir, tempDirectoryForWar);
		
		assertTrue(bladeRunnerDevServletsJar.exists());
		
		warCommandUtility.deleteBladeRunnerDevServletsFromTemporaryDirectoryForWarCreation(tempDirectoryForWar);
		
		assertFalse(bladeRunnerDevServletsJar.exists());
	}
	
	@Test
	public void testRewriteApplicationWebxmlFileToCommentInProdAspectsAndCommentOutDevAspects() throws Exception
	{
		warCommandUtility.copyWEBINFFolderToTemporaryDirectoryForWarCreation(applicationWEBINFDir, tempDirectoryForWar);
		
		File webxmlInTemporaryDirectoryForWarCreation = new File(tempDirectoryForWar, "WEB-INF/web.xml");
		xmlReader = new RandomAccessFile(webxmlInTemporaryDirectoryForWarCreation, "r");
		
		xmlReader.readLine();
		assertEquals("	<!-- start-env: prod", xmlReader.readLine());
		
		warCommandUtility.rewriteApplicationWebxmlFileToAddInProdAspectsRemoveDevAspectsAndInjectAppVersionToken(tempDirectoryForWar);
		
		xmlReader.seek(0);
		xmlReader.readLine();
		String appVersionToken = xmlReader.readLine();
		assertTrue(appVersionToken.matches("	<env-entry-value>v_\\d{13}</env-entry-value>"));
		assertEquals("	<prod>1</prod>", xmlReader.readLine());
		assertEquals("	<echo msg=\"No comment.\"/>", xmlReader.readLine());
		assertEquals("	", xmlReader.readLine());
		assertEquals("	<prod>2</prod>", xmlReader.readLine());
		assertEquals("	", xmlReader.readLine());
		assertEquals("	<echo msg=\"Further lack of comment.\"/>", xmlReader.readLine());
		
		xmlReader.close();
	}
	
	@Test
	public void testCopyIndexHtmlAndUnbundledResourcesFromEachApplicationAspect() throws Exception
	{
		File loginAspectIndexHtml = new File(loginAspectDirectory, "index.html");
		File mainAspectUnbundledResource = new File(defaultAspectDirectory, "unbundled-resources/resource.js");
		File mobileAspectUnbundledResource = new File(mobileAspectDirectory, "unbundled-resources/resource.png");
		File mobileAspectIndexHtml = new File(mobileAspectDirectory, "index.html");
		
		File mainAspectNotWared = new File(defaultAspectDirectory, "src/not-wared.js");
		
		assertFalse(loginAspectDirectory.exists());
		assertFalse(defaultAspectDirectory.exists());
		assertFalse(mobileAspectDirectory.exists());
		assertFalse(loginAspectIndexHtml.exists());
		assertFalse(mainAspectUnbundledResource.exists());
		assertFalse(mobileAspectUnbundledResource.exists());
		assertFalse(mobileAspectIndexHtml.exists());
		assertFalse(mainAspectNotWared.exists());
		
		warCommandUtility.copyIndexFilesAndUnbundledResourcesFromEachApplicationAspect(applicationAspects, tempDirectoryForWar);
		
		assertTrue(loginAspectDirectory.exists());
		assertTrue(defaultAspectDirectory.exists());
		assertTrue(mobileAspectDirectory.exists());
		assertTrue(loginAspectIndexHtml.exists());
		assertTrue(mainAspectUnbundledResource.exists());
		assertTrue(mobileAspectUnbundledResource.exists());
		assertTrue(mobileAspectIndexHtml.exists());
		assertFalse(mainAspectNotWared.exists());
	}
	
	@Test
	public void testCopyIndexJspAndUnbundledResourcesFromEachApplicationAspect() throws Exception
	{
		applicationToWarDirectory = new File(sdkBaseDir.getParent(), APPLICATIONS_DIR + "/emptytrader-jsp");
		applicationWEBINFDir = new File(applicationToWarDirectory, "WEB-INF");
		appMetaData = new AppMetaData(BRJSAccessor.root.app("emptytrader-jsp"));
		applicationAspects = CutlassDirectoryLocator.getApplicationAspects(applicationToWarDirectory);
		
		File loginAspectIndexJsp = new File(loginAspectDirectory, "index.jsp");
		File mainAspectUnbundledResource = new File(defaultAspectDirectory, "unbundled-resources/resource.js");
		File mobileAspectUnbundledResource = new File(mobileAspectDirectory, "unbundled-resources/resource.png");
		File mobileAspectIndexJsp = new File(mobileAspectDirectory, "index.jsp");
		
		File mainAspectNotWared = new File(defaultAspectDirectory, "src/not-wared.js");
		
		assertFalse(loginAspectDirectory.exists());
		assertFalse(defaultAspectDirectory.exists());
		assertFalse(mobileAspectDirectory.exists());
		assertFalse(loginAspectIndexJsp.exists());
		assertFalse(mainAspectUnbundledResource.exists());
		assertFalse(mobileAspectUnbundledResource.exists());
		assertFalse(mobileAspectIndexJsp.exists());
		assertFalse(mainAspectNotWared.exists());
		
		warCommandUtility.copyIndexFilesAndUnbundledResourcesFromEachApplicationAspect(applicationAspects, tempDirectoryForWar);
		
		assertTrue(loginAspectDirectory.exists());
		assertTrue(defaultAspectDirectory.exists());
		assertTrue(mobileAspectDirectory.exists());
		assertTrue(loginAspectIndexJsp.exists());
		assertTrue(mainAspectUnbundledResource.exists());
		assertTrue(mobileAspectUnbundledResource.exists());
		assertTrue(mobileAspectIndexJsp.exists());
		assertFalse(mainAspectNotWared.exists());
	}
	
	@Test
	public void testWriteOutToWarFolderGZippedFileBundleWithHTMLBundler() throws Exception
	{
		HtmlBundler htmlBundler = new HtmlBundler();
		
		warCommandUtility.copyIndexFilesAndUnbundledResourcesFromEachApplicationAspect(applicationAspects, tempDirectoryForWar);
		
		File mainAspectHtmlBundle = new File(defaultAspectDirectory, "html.bundle");
		File loginAspectHtmlBundle = new File(loginAspectDirectory, "html.bundle");
		File mobileAspectHtmlBundle = new File(mobileAspectDirectory, "html.bundle");
		
		assertFalse(mainAspectHtmlBundle.exists());
		assertFalse(loginAspectHtmlBundle.exists());
		assertFalse(mobileAspectHtmlBundle.exists());
		
		for(File applicationAspect : applicationAspects)
		{
			warCommandUtility.writeOutToWarDirectoryGZippedFileBundle(htmlBundler, appMetaData, applicationAspect, tempDirectoryForWar);
		}
		
		assertTrue(mainAspectHtmlBundle.exists());
		assertTrue(loginAspectHtmlBundle.exists());
		assertTrue(mobileAspectHtmlBundle.exists());
	}
	
	@Test
	public void testGzippedHtmlBundleContentWhenDecompressed() throws Exception
	{
		LegacyFileBundlerPlugin htmlBundler = new HtmlBundler();
		
		warCommandUtility.copyIndexFilesAndUnbundledResourcesFromEachApplicationAspect(applicationAspects, tempDirectoryForWar);
		
		File mainAspectHtmlBundle = new File(defaultAspectDirectory, "html.bundle");
		
		for(File applicationAspect : applicationAspects)
		{
			warCommandUtility.writeOutToWarDirectoryGZippedFileBundle(htmlBundler, appMetaData, applicationAspect, tempDirectoryForWar);
		}
		
		File decompressedFile = new File(tempDirectoryForWar + File.separator + "htmlBundle.txt");
		decompressGzipInputStreamToFile(new GZIPInputStream(new FileInputStream(mainAspectHtmlBundle)), decompressedFile);
		
		String expectedOutput =
			"\n" +
			"<!-- File: bladeset-resource.html -->\n" +
			"<div id=\"caplinx.bs1.ID\">\n" +
			"	<HTML resource inside of a bladeset>\n" +
			"</div>\n" +
			"<!-- File: blade-resource.html -->\n" +
			"<div id=\"caplinx.bs1.b1.ID\">\n" +
			"	<HTML resource in a blade that is part of a bladeset>\n" +
			"</div>";
		assertEquals(expectedOutput, FileUtils.readFileToString(decompressedFile).replaceAll("\r\n", "\n"));
	}
	
	// NOTE: This test creates a JsBundler WITHOUT passing in a ClosureCompilerMinifier (which is not what the current implementation does)
	@Test
	public void testGzippedJsBundleContentWhenDecompressed() throws Exception
	{
		LegacyFileBundlerPlugin minifyingJsBundler = new JsBundler();
		
		warCommandUtility.copyIndexFilesAndUnbundledResourcesFromEachApplicationAspect(applicationAspects, tempDirectoryForWar);
		
		File defaultAspectJsBundle = new File(defaultAspectDirectory, "js/js.bundle");
		
		for(File applicationAspect : applicationAspects)
		{
			warCommandUtility.writeOutToWarDirectoryGZippedFileBundle(minifyingJsBundler, appMetaData, applicationAspect, tempDirectoryForWar);
		}
		
		File decompressedFile = new File(tempDirectoryForWar + File.separator + "jsBundle.txt");
		decompressGzipInputStreamToFile(new GZIPInputStream(new FileInputStream(defaultAspectJsBundle)), decompressedFile);
		
		String expectedOutput =
			"/**************************************************************/\n" +
			"/**  Created with SDK Version Unknown (build date Unknown).  **/\n" +
			"/**************************************************************/\n" +
			"\n" +
			"// package definition block\n" +
			"window.caplinx = {};\n" +
			"\n" +
			"\n" +
			"\n" +
			"caplinx.MyClass = function()\n" +
			"{\n" +
			"};\n" +
			"\n" +
			"\n" +
			"caplin.__aliasData = {};\n" +
			"\n" +
			"caplin.onLoad();\n";
		assertEquals(expectedOutput, FileUtils.readFileToString(decompressedFile).replaceAll("\r\n", "\n"));
	}
	
	@Test
	public void testGzippedMinifiedJsBundleContentWhenDecompressed() throws Exception
	{
		BladerunnerConf.getMinifiers().put("closure-whitespace", "com.caplin.cutlass.bundler.js.minification.WhitespaceClosureCompilerMinifier");
		LegacyFileBundlerPlugin minifyingJsBundler = new JsBundler("closure-whitespace");
		
		warCommandUtility.copyIndexFilesAndUnbundledResourcesFromEachApplicationAspect(applicationAspects, tempDirectoryForWar);
		
		File defaultAspectJsBundle = new File(defaultAspectDirectory, "js/js.bundle");
		
		for(File applicationAspect : applicationAspects)
		{
			warCommandUtility.writeOutToWarDirectoryGZippedFileBundle(minifyingJsBundler, appMetaData, applicationAspect, tempDirectoryForWar);
		}
		
		File decompressedFile = new File(tempDirectoryForWar + File.separator + "jsBundle.txt");
		decompressGzipInputStreamToFile(new GZIPInputStream(new FileInputStream(defaultAspectJsBundle)), decompressedFile);
		
		String expectedOutput =
			"/**************************************************************/\n" +
			"/**  Created with SDK Version Unknown (build date Unknown).  **/\n" +
			"/**************************************************************/\n" +
			"\n" +
			"// package definition block\n" +
			"window.caplinx = {};\n" +
			"\n" +
			"caplinx.MyClass=function(){};caplin.__aliasData={};\n" +
			"caplin.onLoad();\n";
		
		assertEquals(expectedOutput, FileUtils.readFileToString(decompressedFile).replaceAll("\r\n", "\n"));
	}
	
	@Test
	public void testWriteOutToWarFolderGZippedFileBundleWithXMLBundler() throws Exception
	{
		XmlBundler xmlBundler = new XmlBundler();
		
		warCommandUtility.copyIndexFilesAndUnbundledResourcesFromEachApplicationAspect(applicationAspects, tempDirectoryForWar);
		
		File mainAspectXmlBundle = new File(defaultAspectDirectory, "xml.bundle");
		File loginAspectXmlBundle = new File(loginAspectDirectory, "xml.bundle");
		File mobileAspectXmlBundle = new File(mobileAspectDirectory, "xml.bundle");
		
		assertFalse(mainAspectXmlBundle.exists());
		assertFalse(loginAspectXmlBundle.exists());
		assertFalse(mobileAspectXmlBundle.exists());
		
		for(File applicationAspect : applicationAspects)
		{
			warCommandUtility.writeOutToWarDirectoryGZippedFileBundle(xmlBundler, appMetaData, applicationAspect, tempDirectoryForWar);
		}
		
		assertTrue(mainAspectXmlBundle.exists());
		assertTrue(loginAspectXmlBundle.exists());
		assertTrue(mobileAspectXmlBundle.exists());
	}

	@Test
	public void testWriteOutToWarFolderGZippedFileBundleWithi18lBundler() throws Exception
	{
		I18nBundler i18nBundler = new I18nBundler();
		
		warCommandUtility.copyIndexFilesAndUnbundledResourcesFromEachApplicationAspect(applicationAspects, tempDirectoryForWar);
		
		File mainAspecti18nBundle = new File(defaultAspectDirectory, "i18n/en_i18n.bundle");
		File loginAspecti18nBundle = new File(loginAspectDirectory, "i18n/en_US_i18n.bundle");
		File mobileAspecti18nBundle = new File(mobileAspectDirectory, "i18n/en_i18n.bundle");
		
		assertFalse(mainAspecti18nBundle.exists());
		assertFalse(loginAspecti18nBundle.exists());
		assertFalse(mobileAspecti18nBundle.exists());
		
		for(File applicationAspect : applicationAspects)
		{
			warCommandUtility.writeOutToWarDirectoryGZippedFileBundle(i18nBundler, appMetaData, applicationAspect, tempDirectoryForWar);
		}
		
		assertTrue(mainAspecti18nBundle.exists());
		assertTrue(loginAspecti18nBundle.exists());
		assertTrue(mobileAspecti18nBundle.exists());
	}
	
	@Test
	public void testWriteOutToWarFolderGZippedFileBundleWithJsBundler() throws Exception
	{
		JsBundler jsBundler = new JsBundler();
		
		warCommandUtility.copyIndexFilesAndUnbundledResourcesFromEachApplicationAspect(applicationAspects, tempDirectoryForWar);
		
		File defaultAspectJsBundle = new File(defaultAspectDirectory, "js/js.bundle");
		File loginAspectJsBundle = new File(loginAspectDirectory, "js/js.bundle");
		File mobileAspectJsBundle = new File(mobileAspectDirectory, "js/js.bundle");
		
		assertFalse(defaultAspectJsBundle.exists());
		assertFalse(loginAspectJsBundle.exists());
		assertFalse(mobileAspectJsBundle.exists());
		
		for(File applicationAspect : applicationAspects)
		{
			warCommandUtility.writeOutToWarDirectoryGZippedFileBundle(jsBundler, appMetaData, applicationAspect, tempDirectoryForWar);
		}
		
		assertTrue(defaultAspectJsBundle.exists());
		assertTrue(loginAspectJsBundle.exists());
		assertTrue(mobileAspectJsBundle.exists());
	}
	
	@Test
	public void testWriteOutToWarFolderGZippedFileBundleWithCssBundler() throws Exception
	{
		CssBundler cssBundler = new CssBundler();
		
		warCommandUtility.copyIndexFilesAndUnbundledResourcesFromEachApplicationAspect(applicationAspects, tempDirectoryForWar);
		
		File mainAspectCssBundle = new File(defaultAspectDirectory, "css/blue_en_UK_css.bundle");
		File loginAspectCssBundle = new File(loginAspectDirectory, "css/blue_css.bundle");
		File mobileAspectCssBundle = new File(mobileAspectDirectory, "css/blue_en_US_css.bundle");
		
		assertFalse(mainAspectCssBundle.exists());
		assertFalse(loginAspectCssBundle.exists());
		assertFalse(mobileAspectCssBundle.exists());
		
		for(File applicationAspect : applicationAspects)
		{
			warCommandUtility.writeOutToWarDirectoryGZippedFileBundle(cssBundler, appMetaData, applicationAspect, tempDirectoryForWar);
		}
		
		assertTrue(mainAspectCssBundle.exists());
		assertTrue(loginAspectCssBundle.exists());
		assertTrue(mobileAspectCssBundle.exists());
	}
	
	@Test
	public void testWriteOutToWarFolderGZippedFileBundleWithImageBundler() throws Exception
	{
		ImageBundler imageBundler = new ImageBundler();
		
		warCommandUtility.copyIndexFilesAndUnbundledResourcesFromEachApplicationAspect(applicationAspects, tempDirectoryForWar);
		
		File mainAspectImageBundle = new File(defaultAspectDirectory, "images/theme_blue/img/blue.jpg_image.bundle");
		File loginAspectImageBundle = new File(loginAspectDirectory, "images/bladeset_bs2/blade_b2/theme_blue/blue.png_image.bundle");
		File mobileAspectImageBundle = new File(mobileAspectDirectory, "images/bladeset_bs1/theme_blue/blue.gif_image.bundle");
		
		assertFalse(mainAspectImageBundle.exists());
		assertFalse(loginAspectImageBundle.exists());
		assertFalse(mobileAspectImageBundle.exists());
		
		for(File applicationAspect : applicationAspects)
		{
			warCommandUtility.writeOutToWarDirectoryGZippedFileBundle(imageBundler, appMetaData, applicationAspect, tempDirectoryForWar);
		}
		
		assertTrue(mainAspectImageBundle.exists());
		assertTrue(loginAspectImageBundle.exists());
		assertTrue(mobileAspectImageBundle.exists());
	}
	
	@Test
	public void testWriteOutToWarFolderGZippedFileBundleWithThirdPartyBundler() throws Exception
	{
		ThirdPartyBundler thirdPartyBundler = new ThirdPartyBundler();
		
		warCommandUtility.copyIndexFilesAndUnbundledResourcesFromEachApplicationAspect(applicationAspects, tempDirectoryForWar);
		
		List<File> thirdPartyLibraryDirsOfAspects = new ArrayList<File>(Arrays.asList
				(new File(defaultAspectDirectory, "thirdparty-libraries/"),
				new File(loginAspectDirectory, "thirdparty-libraries/"),
				new File(mobileAspectDirectory, "thirdparty-libraries/")));
		
		for(File thirdPartyLibraryDir : thirdPartyLibraryDirsOfAspects)
		{
			assertFalse(thirdPartyLibraryDir.exists());
		}
		
		for(File applicationAspect : applicationAspects)
		{
			warCommandUtility.writeOutToWarDirectoryGZippedFileBundle(thirdPartyBundler, appMetaData, applicationAspect, tempDirectoryForWar);
		}
		
		for(File thirdPartyLibraryDir : thirdPartyLibraryDirsOfAspects)
		{
			assertTrue(thirdPartyLibraryDir.exists());
			
			File lib1 = new File(thirdPartyLibraryDir, "lib1/");
			File lib1resource = new File(lib1, "lib1-resource.txt_thirdparty.bundle");
			assertTrue(lib1resource.exists());
			assertEquals(1, lib1.listFiles().length);
			
			File lib2 = new File(thirdPartyLibraryDir, "lib2/");
			File lib2resource = new File(lib2, "lib2-resource.txt_thirdparty.bundle");
			assertTrue(lib2resource.exists());
			assertEquals(1, lib2.listFiles().length);
			
			File lib3 = new File(thirdPartyLibraryDir, "lib3/");
			File lib3resource1 = new File(lib3, "lib3-resource1-in-sdk.txt_thirdparty.bundle");
			File lib3resource2 = new File(lib3, "lib3-resource2-in-sdk.txt_thirdparty.bundle");
			assertTrue(lib3resource1.exists());
			assertTrue(lib3resource2.exists());
			assertEquals(2, lib3.listFiles().length);
			
			File lib4 = new File(thirdPartyLibraryDir, "lib4/");
			File lib4resource = new File(lib4, "lib4-resource1-with_underscore-in-sdk.txt_thirdparty.bundle");
			assertTrue(lib4resource.exists());
			assertEquals(1, lib4.listFiles().length);
		}
	}

	@Test
	public void testWriteOutToWarDirectoryAllGZippedBundles() throws Exception
	{
		warCommandUtility.copyIndexFilesAndUnbundledResourcesFromEachApplicationAspect(applicationAspects, tempDirectoryForWar);
		
		File loginAspectXmlBundle = new File(loginAspectDirectory, "xml.bundle");
		File mobileAspectJsBundle = new File(mobileAspectDirectory, "js/js.bundle");
		File mobileAspectHtmlBundle = new File(mobileAspectDirectory, "html.bundle");
		File loginAspectCssBundle = new File(loginAspectDirectory, "css/blue_css.bundle");
		File mainAspecti18nBundle = new File(defaultAspectDirectory, "i18n/en_i18n.bundle");
		File mainAspectImageBundle = new File(defaultAspectDirectory, "images/theme_blue/img/blue.jpg_image.bundle");
		
		assertFalse(loginAspectXmlBundle.exists());
		assertFalse(mainAspecti18nBundle.exists());
		assertFalse(loginAspectCssBundle.exists());
		assertFalse(mobileAspectJsBundle.exists());
		assertFalse(mainAspectImageBundle.exists());
		assertFalse(mobileAspectHtmlBundle.exists());
		
		warCommandUtility.writeOutToWarDirectoryAllGZippedBundles(applicationAspects, appMetaData, tempDirectoryForWar);
		
		assertTrue(mainAspecti18nBundle.exists());
		assertTrue(loginAspectXmlBundle.exists());
		assertTrue(loginAspectCssBundle.exists());
		assertTrue(mobileAspectJsBundle.exists());
		assertTrue(mainAspectImageBundle.exists());
		assertTrue(mobileAspectHtmlBundle.exists());
	}
	
	@Test
	public void testZipUpTemporaryDirectoryIntoWar() throws Exception
	{
		File warFile = FileUtility.createTemporaryFile("tempWar", ".war");
		warFile.delete();
		assertFalse(warFile.exists());
		assertTrue(tempDirectoryForWar.exists());
		
		warCommandUtility.copyIndexFilesAndUnbundledResourcesFromEachApplicationAspect(applicationAspects, tempDirectoryForWar);
		
		warCommandUtility.zipUpTemporaryDirectoryIntoWarAndDeleteTemporaryWarCreationDirectory(tempDirectoryForWar, warFile);
		
		assertTrue(warFile.exists());
		assertFalse(tempDirectoryForWar.exists());
	}
	
	private void decompressGzipInputStreamToFile(InputStream input, File file) throws FileNotFoundException, IOException
	{

		IOUtils.copy(input, new FileOutputStream(file));
		input.close();
		
	}
}
