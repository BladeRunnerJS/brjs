package com.caplin.cutlass.bundler.css;

import java.io.File;

import org.junit.Test;

import org.bladerunnerjs.core.plugin.bundler.LegacyFileBundlerPlugin;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import org.bladerunnerjs.model.sinbin.CutlassConfig;

public class RequestCssBundlerTest
{
	private static final File APP_MAIN_ASPECT_DIR = new File("src/test/resources/generic-bundler/bundler-structure-tests/"
			+ CutlassConfig.APPLICATIONS_DIR +"/app1/main-aspect");
	
	@Test
	public void wellFormedRequestsAreProcessedNormally() throws Exception
	{
		LegacyFileBundlerPlugin bundler = new CssBundler();
		bundler.getBundleFiles(APP_MAIN_ASPECT_DIR, null, "css/theme_css.bundle");
		bundler.getBundleFiles(APP_MAIN_ASPECT_DIR, null, "css/theme_browser10_css.bundle");
		bundler.getBundleFiles(APP_MAIN_ASPECT_DIR, null, "css/theme_en_US_css.bundle");
	}

	@Test(expected=RequestHandlingException.class)
	public void themeNamesCantHaveUnderscores() throws Exception
	{
		LegacyFileBundlerPlugin bundler = new CssBundler();
		bundler.getBundleFiles(APP_MAIN_ASPECT_DIR, null, "css/theme_foo_css.bundle");
	}
	
	@Test(expected=RequestHandlingException.class)
	public void themeNamesCantHaveSlashes() throws Exception
	{
		LegacyFileBundlerPlugin bundler = new CssBundler();
		bundler.getBundleFiles(APP_MAIN_ASPECT_DIR, null, "css/theme/foo_css.bundle");
	}
	
	@Test
	public void themeNamesCanHaveHyphens() throws Exception
	{
		LegacyFileBundlerPlugin bundler = new CssBundler();
		bundler.getBundleFiles(APP_MAIN_ASPECT_DIR, null, "css/theme-foo_css.bundle");
	}
	
	@Test(expected=RequestHandlingException.class)
	public void themeNamesMustEndWithExactlyCssDotBundle() throws Exception
	{
		LegacyFileBundlerPlugin bundler = new CssBundler();
		bundler.getBundleFiles(APP_MAIN_ASPECT_DIR, null, "css/theme_css.bundle-foo");
	}
}