package com.caplin.cutlass.bundler;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.caplin.cutlass.EncodingAccessor;
import com.caplin.cutlass.bundler.i18n.I18nBundler;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class UnicodeBundlerTest
{
	private String defaultInputEncoding;
	private String defaultOutputEncoding;
	
	@Before
	public void setUp()
	{
		defaultInputEncoding = EncodingAccessor.getDefaultInputEncoding();
		defaultOutputEncoding = EncodingAccessor.getDefaultOutputEncoding();
	}
	
	@After
	public void tearDown()
	{
		EncodingAccessor.setDefaultInputEncoding(defaultInputEncoding);
		EncodingAccessor.setDefaultOutputEncoding(defaultOutputEncoding);
	}
	
	@Test
	public void utf8IsHandledCorrectlyWhenInputEncodingIsUtf8() throws Exception
	{
		EncodingAccessor.setDefaultInputEncoding("UTF-8");
		EncodingAccessor.setDefaultOutputEncoding("UTF-8");
		
		Map<String, String> results = getMapFromJsonOutput("utf8.properties");
		assertEquals("€", results.get("app.eurosign"));
	}
	
	@Test
	public void utf8WithOptionalBomIsHandledCorrectlyWhenInputEncodingIsUtf8() throws Exception
	{
		EncodingAccessor.setDefaultInputEncoding("UTF-8");
		EncodingAccessor.setDefaultOutputEncoding("UTF-8");
		
		Map<String, String> results = getMapFromJsonOutput("utf8-with-optional-bom.properties");
		assertEquals("€", results.get("app.eurosign"));
	}
	
	@Ignore // TODO we are no longer relying on the old bladerunner conf and have hard-coded this to UTF-8
	@Test
	public void utf8BecomesGarbledWhenInputEncodingIsLatin1() throws Exception
	{
		EncodingAccessor.setDefaultInputEncoding("ISO-8859-1");
		EncodingAccessor.setDefaultOutputEncoding("UTF-8");
		
		Map<String, String> results = getMapFromJsonOutput("utf8.properties");
		assertEquals("â¬", results.get("app.eurosign"));
	}
	
	@Test
	public void utf8WithOptionalBomIsHandledCorrectlyEvenWhenInputEncodingIsLatin1() throws Exception
	{
		EncodingAccessor.setDefaultInputEncoding("ISO-8859-1");
		EncodingAccessor.setDefaultOutputEncoding("UTF-8");
		
		Map<String, String> results = getMapFromJsonOutput("utf8-with-optional-bom.properties");
		assertEquals("€", results.get("app.eurosign"));
	}
	
	@Ignore // TODO we are no longer relying on the old bladerunner conf and have hard-coded this to UTF-8
	@Test
	public void outputEncodingCanBeSetToUtf16() throws Exception
	{
		EncodingAccessor.setDefaultInputEncoding("UTF-8");
		EncodingAccessor.setDefaultOutputEncoding("UTF-16");
		
		Map<String, String> results = getMapFromJsonOutput("utf8.properties");
		assertEquals("€", results.get("app.eurosign"));
	}
	
	@Ignore // TODO we are no longer relying on the old bladerunner conf and have hard-coded this to UTF-8
	@Test(expected=JsonSyntaxException.class)
	public void verifyOutputEncodingIsDefinitelyUtf16() throws Exception
	{
		EncodingAccessor.setDefaultInputEncoding("UTF-8");
		EncodingAccessor.setDefaultOutputEncoding("UTF-16");
		
		Map<String, String> results = getMapFromJsonOutputWithEncoding("utf8.properties", "UTF-8");
		assertEquals("€", results.get("app.eurosign"));
	}
	
	private Map<String, String> getMapFromJsonOutput(String inputFile) throws Exception
	{
		return getMapFromJsonOutputWithEncoding(inputFile, EncodingAccessor.getDefaultOutputEncoding());
	}
	
	private Map<String, String> getMapFromJsonOutputWithEncoding(String inputFile, String outputEncoding) throws Exception
	{
		I18nBundler bundler = new I18nBundler();
		List<File> files = Arrays.asList(new File("src/test/resources/generic-bundler/unicode-tests", inputFile));
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		bundler.writeBundle(files, outputStream);
		outputStream.close();
		
		String json = outputStream.toString(outputEncoding);
		json = json.replace("pUnprocessedI18NMessages = (!window.pUnprocessedI18NMessages) ? [] : pUnprocessedI18NMessages;\n", "");
		json = json.replace("pUnprocessedI18NMessages.push(", "");
		json = json.replace(");\n", "");
		@SuppressWarnings("unchecked")
		Map<String, String> results = new Gson().fromJson(json, HashMap.class);
		return results;
	}
}
