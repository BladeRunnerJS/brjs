package com.caplin.cutlass.bundler.js;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.caplin.cutlass.AppMetaData;

public class ValidRequestStringsJsBundlerTest {
	
	@Test
	public void testJsBundlerReturnsExpectedValidRequestStrings() throws Exception
	{
		JsBundler jsBundler = new JsBundler();
		AppMetaData metaData = mock(AppMetaData.class);
		when(metaData.getLocales()).thenReturn(Arrays.asList("de_DE"));
		when(metaData.getBrowsers()).thenReturn(Arrays.asList("ie6"));
		when(metaData.getThemes()).thenReturn(Arrays.asList("noir"));
		
		List<String> requests = jsBundler.getValidRequestStrings(metaData);
		
		assertEquals(Arrays.asList("js/js.bundle"), requests);
	}

}
