package com.caplin.cutlass.bundler.css;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

import static org.bladerunnerjs.model.sinbin.CutlassConfig.APPLICATIONS_DIR;
import static org.bladerunnerjs.model.sinbin.CutlassConfig.SDK_DIR;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CssBundlerFileAppenderTest
{
	public static final File BASE_DIR = new File ("src/test/resources/css-bundler/css-bundler-file-appender");
	public static final File APP_DIR1 = new File(BASE_DIR, APPLICATIONS_DIR + "/app1");
	public static final File APP_DIR2 = new File(BASE_DIR, APPLICATIONS_DIR + "/app2");
	
	@Test
	public void testAppendThirdPartyLibraryFilesForNonCommonThemeGetsEmptyList() throws Exception
	{
		CssBundlerFileAppender fileAppender = new CssBundlerFileAppender("blue");
		List<File> cssFiles = new ArrayList<File>();
		fileAppender.appendThirdPartyLibraryFiles(APP_DIR1, cssFiles);
		assertEquals(0, cssFiles.size());
	}
	
	@Test
	public void testAppendThirdPartyLibraryFilesForCommonThemeGetsCorrectCssFiles() throws Exception
	{
		CssBundlerFileAppender fileAppender = new CssBundlerFileAppender("common");
		List<File> cssFiles = new ArrayList<File>();
		fileAppender.appendThirdPartyLibraryFiles(APP_DIR1, cssFiles);
		assertEquals(4, cssFiles.size());
		assertTrue(cssFiles.contains(new File(APP_DIR1, "thirdparty-libraries/lib1/subfolder/lib1_style_in_subfolder.css")));
		assertTrue(cssFiles.contains(new File(APP_DIR1, "thirdparty-libraries/lib1/lib1_style.css")));
		assertTrue(cssFiles.contains(new File(BASE_DIR, SDK_DIR + "/libs/javascript/thirdparty/lib2/lib2_style1.css")));
		assertTrue(cssFiles.contains(new File(BASE_DIR, SDK_DIR + "/libs/javascript/thirdparty/lib2/lib2_style2.css")));
	}
	
	@Test(expected=BundlerProcessingException.class)
	public void testAppendThirdPartyLibraryThrowsExceptionIfManifestIncludesNonExistentCssFile() throws Exception
	{
		CssBundlerFileAppender fileAppender = new CssBundlerFileAppender("common");
		List<File> cssFiles = new ArrayList<File>();
		fileAppender.appendThirdPartyLibraryFiles(APP_DIR2, cssFiles);
	}
}
