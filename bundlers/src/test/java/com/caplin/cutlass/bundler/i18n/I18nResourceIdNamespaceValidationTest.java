package com.caplin.cutlass.bundler.i18n;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.caplin.cutlass.testing.BRJSTestFactory;
import com.caplin.cutlass.EncodingAccessor;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import com.caplin.cutlass.BRJSAccessor;
import com.google.gson.Gson;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;

public class I18nResourceIdNamespaceValidationTest {
	
	private static final File BASE_DIR = new File("src/test/resources/i18n-bundler/id-scope/" + APPLICATIONS_DIR);
	
	//Blade id tests
	
	@Before
	public void setUp()
	{
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(BASE_DIR));
	}
	
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
	
	@Test (expected=ContentFileProcessingException.class)
	public void testInvalideBladeIdWithNoNamespace() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app3/fx-bladeset/blades/blade1"), null, "i18n/en_EN_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	@Test (expected=ContentFileProcessingException.class)
	public void testInvalideBladeIdWithNoBladesetName() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app4/fx-bladeset/blades/blade1"), null, "i18n/en_EN_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	@Test (expected=ContentFileProcessingException.class)
	public void testInvalidBladeIdWithWrongBladesetName() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app7/fx-bladeset/blades/blade1"), null, "i18n/en_EN_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	@Test (expected=ContentFileProcessingException.class)
	public void testInvalideBladeIdWithNoBladeName() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app5/fx-bladeset/blades/blade1"), null, "i18n/en_EN_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	@Test (expected=ContentFileProcessingException.class)
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
	@Test (expected=ContentFileProcessingException.class)
	public void testInvalidBladesetIdAttemptsToOverrideBladeId() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app8/fx-bladeset"), null, "i18n/en_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	@Test (expected=ContentFileProcessingException.class)
	public void testInvalidBladesetIdWithNoNamespace() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app9/fx-bladeset"), null, "i18n/en_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	@Test (expected=ContentFileProcessingException.class)
	public void testInvalidBladesetIdWithNoBladesetName() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app10/fx-bladeset"), null, "i18n/en_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	@Test (expected=ContentFileProcessingException.class)
	public void testInvalidBladesetIdWithWrongBladesetName() throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = bundler.getBundleFiles(new File(BASE_DIR, "test-app11/fx-bladeset"), null, "i18n/en_i18n.bundle");
		
		bundler.writeBundle(files, new ByteArrayOutputStream());
	}
	
	private Map<String, String> getMapFromJsonOutput(ByteArrayOutputStream outputStream) throws UnsupportedEncodingException
	{
		String json = outputStream.toString(EncodingAccessor.getDefaultOutputEncoding());
		json = json.replace("pUnprocessedI18NMessages = (!window.pUnprocessedI18NMessages) ? [] : pUnprocessedI18NMessages;\n", "");
		json = json.replace("pUnprocessedI18NMessages.push(", "");
		json = json.replace(");\n", "");
		@SuppressWarnings("unchecked")
		Map<String, String> results = new Gson().fromJson(json, HashMap.class);
		return results;
	}
}
