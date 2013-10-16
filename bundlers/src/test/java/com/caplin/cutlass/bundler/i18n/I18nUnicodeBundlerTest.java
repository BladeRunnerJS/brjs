package com.caplin.cutlass.bundler.i18n;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;

import org.junit.Test;

public class I18nUnicodeBundlerTest
{
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
}
