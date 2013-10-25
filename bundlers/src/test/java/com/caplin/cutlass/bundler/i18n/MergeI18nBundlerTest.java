package com.caplin.cutlass.bundler.i18n;

import static org.bladerunnerjs.model.sinbin.CutlassConfig.APPLICATIONS_DIR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.core.plugin.bundler.LegacyFileBundlerPlugin;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import com.caplin.cutlass.EncodingAccessor;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import com.google.gson.Gson;

public class MergeI18nBundlerTest
{
	private static final File BASE_DIR = new File("src/test/resources/generic-bundler/bundler-structure-tests");
	private static final File APP_ASPECT_DIR = new File("src/test/resources/generic-bundler/bundler-structure-tests/" 
															+ APPLICATIONS_DIR + "/test-test-app1/default-aspect");
	
	@Before
	public void setUp()
	{
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(BASE_DIR));
	}
	
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
}
