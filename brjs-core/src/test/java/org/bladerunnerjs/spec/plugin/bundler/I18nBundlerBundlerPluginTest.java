package org.bladerunnerjs.spec.plugin.bundler;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class I18nBundlerBundlerPluginTest extends SpecTest
{

	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
	}
	
	
	
	@Test
	public void requestForI18nWithoutAnyAssetsReturnsEmptyResponse() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html");
		when(app).requestReceived("/default-aspect/i18n/en_GB.json", response);
		then(response).textEquals("{\n};");
	}
	
	@Test
	public void i18nFilesForTheGivenLocaleInAspectResourcesAreBundled() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "some.property=property value");
		when(app).requestReceived("/default-aspect/i18n/en_GB.json", response);
		then(response).textEquals(	
				"{\n"+
						"\"some.property\":\"property value\"\n"+
				"};");
	}
	
	@Test
	public void i18nFilesForOtherLocalesInAspectResourcesAreIgnored() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "some.property=property value")
			.and(aspect).containsFileWithContents("resources/de_DE.properties", "some.property=a different value");
		when(app).requestReceived("/default-aspect/i18n/en_GB.json", response);
		then(response).textEquals(	
				"{\n"+
						"\"some.property\":\"property value\"\n"+
				"};");
	}
	
	@Test
	public void requestsForALocaleCanContainTheLanguageOnly() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsFileWithContents("resources/en.properties", "some.property=property value");
		when(app).requestReceived("/default-aspect/i18n/en.json", response);
		then(response).textEquals(	
				"{\n"+
						"\"some.property\":\"property value\"\n"+
				"};");
	}
	
	@Test
	public void requestsForALanguageDoesntIncludeLocationSpecificProperties() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsFileWithContents("resources/en.properties", "some.property=property value")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "some.property=another value");
		when(app).requestReceived("/default-aspect/i18n/en.json", response);
		then(response).textEquals(	
				"{\n"+
						"\"some.property\":\"property value\"\n"+
				"};");
	}
	
	@Test
	public void locationSpecificPropertiesAreAddedToLanguageValues() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsFileWithContents("resources/en.properties", "some.property=property value")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "another.property=another value");
		when(app).requestReceived("/default-aspect/i18n/en_GB.json", response);
		then(response).textEquals(	
				"{\n"+
						"\"another.property\":\"another value\",\n"+
						"\"some.property\":\"property value\"\n"+
				"};");
	}

	@Test
	public void locationSpecificPropertiesOverrideLanguageProperties() throws Exception 
	{
		given(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsEmptyFile("index.html")
			.and(aspect).containsFileWithContents("resources/en.properties", "some.property=property value")
			.and(aspect).containsFileWithContents("resources/en_GB.properties", "some.property=another value");
		when(app).requestReceived("/default-aspect/i18n/en_GB.json", response);
		then(response).textEquals(	
				"{\n"+
						"\"some.property\":\"another value\"\n"+
				"};");
	}
	
	
	/*
	////////////////////////
	// I18nBundlerTest 
	////////////////////////
	
	@Test
	public void testAppWithOneLocaleGetsValidRequestStrings() throws Exception
	{
		I18nBundler i18nBundler = new I18nBundler();
		AppMetaData metaData = mock(AppMetaData.class);
		when(metaData.getLocales()).thenReturn(Arrays.asList("en_GB"));
		
		List<String> requests = i18nBundler.getValidRequestStrings(metaData);
		
		assertEquals(Arrays.asList("i18n/en_GB_i18n.bundle"), requests);
	}
	
	@Test
	public void testAppWithNoLocaleGetsValidRequestStrings() throws Exception
	{
		I18nBundler i18nBundler = new I18nBundler();
		AppMetaData metaData = mock(AppMetaData.class);
		when(metaData.getLocales()).thenReturn(Collections.<String>emptyList());
		
		List<String> requests = i18nBundler.getValidRequestStrings(metaData);
		
		assertEquals(Collections.<String>emptyList(), requests);
	}
	
	
	////////////////////////
	// I18nResourceIdNamespaceValidationTest 
	////////////////////////
	
	@Test
	public void testValidBladeIdContainsNamespaceAndBladesetName() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app1/fx-bladeset/blades/blade1"), null, "i18n/en_EN_i18n.bundle");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		bundler.writeBundle(files, outputStream);
		
		Map<String, String> results = getMapFromJsonOutput(outputStream);
		assertEquals("I am blade1 header", results.get("caplinx.fx.blade1.header") );
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalideBladeIdWithNoNamespace() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app3/fx-bladeset/blades/blade1"), null, "i18n/en_EN_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalideBladeIdWithNoBladesetName() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app4/fx-bladeset/blades/blade1"), null, "i18n/en_EN_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalidBladeIdWithWrongBladesetName() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app7/fx-bladeset/blades/blade1"), null, "i18n/en_EN_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalideBladeIdWithNoBladeName() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app5/fx-bladeset/blades/blade1"), null, "i18n/en_EN_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalidBladeIdWithBladeNameTypo() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app6/fx-bladeset/blades/blade1"), null, "i18n/en_EN_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	// Bladeset id tests
	
	@Test
	public void testValidBladeSetIdContainsNamespaceAndBladesetName() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app2/fx-bladeset"), null, "i18n/en_i18n.bundle");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		bundler.writeBundle(files, outputStream);
		
		Map<String, String> results = getMapFromJsonOutput(outputStream);
		assertEquals("I am fx bladeset title", results.get("caplinx.fx.title") );
	}
	
	@Ignore
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalidBladesetIdAttemptsToOverrideBladeId() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app8/fx-bladeset"), null, "i18n/en_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalidBladesetIdWithNoNamespace() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app9/fx-bladeset"), null, "i18n/en_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalidBladesetIdWithNoBladesetName() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app10/fx-bladeset"), null, "i18n/en_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	@Test (expected=BundlerFileProcessingException.class)
	public void testInvalidBladesetIdWithWrongBladesetName() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app11/fx-bladeset"), null, "i18n/en_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	////////////////////////
	// I18nUnicodeBundlerTest 
	////////////////////////
	
	@Test
	public void testAppWithOneLocaleGetsValidRequestStrings() throws Exception
	{
		I18nBundler i18nBundler = new I18nBundler();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		
		i18nBundler.writeBundle(Arrays.asList(new File("src/test/resources/i18n-bundler/unicode-test/UTF-8.properties")), byteArrayOutputStream);
		
		String[] propertiesText = byteArrayOutputStream.toString("UTF-8").split("\n");
		assertEquals("  \"currency\": \"Währung\",", propertiesText[2]);
		assertEquals("  \"euro\": \"€\"", propertiesText[3]);
	}
	
	////////////////////////
	// MergeI18nBundlerTest 
	////////////////////////
	
	@Test
	public void wellFormedRequestsAreProcessedNormally() throws Exception
	{
		LegacyFileBundlerPlugin bundler = new I18nBundler();
		bundler.getBundleFiles(APP_ASPECT_DIR, null, "i18n/en_EN_i18n.bundle");
	}
	
	@Test(expected=RequestHandlingException.class)
	public void localeCodeIsRequired() throws Exception
	{
		LegacyFileBundlerPlugin bundler = new I18nBundler();
		bundler.getBundleFiles(APP_ASPECT_DIR, null, "i18n/i18n.bundle");
	}
	
	@Test(expected=RequestHandlingException.class)
	public void requestMustEndWithExactlyI18nDotBundle() throws Exception
	{
		LegacyFileBundlerPlugin bundler = new I18nBundler();
		bundler.getBundleFiles(APP_ASPECT_DIR, null, "i18n/en_EN_i18n.bundle-foo");
	}
	
	@Test
	public void testNoTranslationsAreMappedForMissingLanguageFiles() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, APPLICATIONS_DIR + "/test-app1/default-aspect"), null, "i18n/tr_TR_i18n.bundle");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		bundler.writeBundle(files, outputStream);
		
		assertTrue(outputStream.toString(EncodingAccessor.getDefaultOutputEncoding()).contains("\n"));
	}
	
	@Test
	public void testLanguagePropertiesAreOverriddenByLocaleAtSameLevel() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, APPLICATIONS_DIR + "/test-app1/default-aspect"), null, "i18n/en_EN_i18n.bundle");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		bundler.writeBundle(files, outputStream);
		
		Map<String, String> results = getMapFromJsonOutput(outputStream);
		assertEquals("i am from en_EN.properties", results.get("app.name") );
	}
	
	@Test
	public void testBladeLocalePropertiesAreOverriddenByAppLocaleProperties() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, APPLICATIONS_DIR + "/test-app2/default-aspect"), null, "i18n/en_EN_i18n.bundle");
		files.addAll(bundler.getBundleFiles(new File(BASE_DIR, APPLICATIONS_DIR + "/test-app2/default-aspect"), null, "i18n/en_i18n.bundle"));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		bundler.writeBundle(files, outputStream);
		
		Map<String, String> results = getMapFromJsonOutput(outputStream);
		assertEquals("fx-blade1 header is from en.properties file in app2 default-aspect section folder", results.get("fx-bladeset.fx-blade1.header") );
	}
	
	@Test
	public void testBladesetLocalePropertiesAreOverriddenByAppLocaleProperties() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, APPLICATIONS_DIR + "/test-app2/default-aspect"), null, "i18n/en_EN_i18n.bundle");
		files.addAll(bundler.getBundleFiles(new File(BASE_DIR, APPLICATIONS_DIR + "/test-app2/default-aspect"), null, "i18n/en_i18n.bundle"));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		bundler.writeBundle(files, outputStream);
		
		Map<String, String> results = getMapFromJsonOutput(outputStream);
		assertEquals("fx-bladeset header is from en.properties file in app2 default-aspect section folder", results.get("fx-bladeset.header") );
	}
	
	@Test
	public void countrySpecificFileAtAppLevelOverridesLanguageFileAtSdkLevel() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, APPLICATIONS_DIR + "/test-app2/default-aspect"), null, "i18n/en_US_i18n.bundle");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		bundler.writeBundle(files, outputStream);
		
		Map<String, String> results = getMapFromJsonOutput(outputStream);
		assertEquals("mm/dd/yyyy from en_US.properties file at app2 default-aspect level", results.get("date.placeholder") );
	}
	
	@Test
	public void languageAtWorkbenchLevelOverridesSameFileAtBladeLevel() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench"), null, "i18n/en_i18n.bundle");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		bundler.writeBundle(files, outputStream);
		
		Map<String, String> results = getMapFromJsonOutput(outputStream);
		assertEquals("header is from en.properties file in fx-blade1/workbench level", results.get("fx-bladeset.fx-blade1.header") );
	}
	
	@Test
	public void languageAtWorkbenchLevelOverridesSameFileAtBladeSetLevel() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench"), null, "i18n/en_i18n.bundle");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		bundler.writeBundle(files, outputStream);
		
		Map<String, String> results = getMapFromJsonOutput(outputStream);
		assertEquals("fx-bladeset header is from en.properties file in fx-blade1/workbench level", results.get("fx-bladeset.header") );
	}
	
	@Test
	public void languageAtWorkbenchLevelOverridesSameFileAtSdkLevel() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench"), null, "i18n/en_US_i18n.bundle");
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		bundler.writeBundle(files, outputStream);
		
		Map<String, String> results = getMapFromJsonOutput(outputStream);
		assertEquals("yyyymmdd from fx-blade1/workbench level", results.get("date.placeholder") );
	}
	
	private Map<String, String> getMapFromJsonOutput(ByteArrayOutputStream outputStream) throws Exception
	{
		String json = outputStream.toString(EncodingAccessor.getDefaultOutputEncoding());
		json = json.replace("pUnprocessedI18NMessages = (!window.pUnprocessedI18NMessages) ? [] : pUnprocessedI18NMessages;\n", "");
		json = json.replace("pUnprocessedI18NMessages.push(", "");
		json = json.replace(");\n", "");
		@SuppressWarnings("unchecked")
		Map<String, String> results = new Gson().fromJson(json, HashMap.class);
		return results;
	}
	
	@Test(expected=BundlerFileProcessingException.class)
	public void testWriteNonExistentBundleThrowsException() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		
		bundler.writeBundle(Arrays.asList(new File("i_dont_exist.properties")), new ByteArrayOutputStream());
	}
	
	
	 */
	
}
