package com.caplin.cutlass.bundler.xml;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.caplin.cutlass.AppMetaData;

public class ValidRequestStringsXmlBundlerTest {
	
	@Test
	public void testXmlBundlerReturnsExpectedValidRequestStrings() throws Exception
	{
		XmlBundler xmlBundler = new XmlBundler();
		AppMetaData metaData = mock(AppMetaData.class);
		when(metaData.getLocales()).thenReturn(Arrays.asList("de_DE"));
		when(metaData.getBrowsers()).thenReturn(Arrays.asList("ie6"));
		when(metaData.getThemes()).thenReturn(Arrays.asList("noir"));
		
		List<String> requests = xmlBundler.getValidRequestStrings(metaData);
		
		assertEquals(Arrays.asList("xml.bundle"), requests);
	}

}
