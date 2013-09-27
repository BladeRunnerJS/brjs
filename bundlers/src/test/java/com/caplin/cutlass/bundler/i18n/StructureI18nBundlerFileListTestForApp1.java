package com.caplin.cutlass.bundler.i18n;

import org.junit.Before;
import org.junit.Test;

import static org.bladerunnerjs.model.sinbin.CutlassConfig.APPLICATIONS_DIR;
import static org.bladerunnerjs.model.sinbin.CutlassConfig.SDK_DIR;

import com.caplin.cutlass.bundler.BundlerFileTester;

public class StructureI18nBundlerFileListTestForApp1 
{
	private BundlerFileTester test;
	
	@Before
	public void setUp()
	{
		test = new BundlerFileTester(new I18nBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
	}
	
	@Test
	public void appAspectLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/main-aspect")
		.whenRequestReceived("i18n/en_EN_i18n.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en_EN.properties", 
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/i18n/en/en_EN.properties",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/i18n/en/en_EN.properties",
			APPLICATIONS_DIR + "/test-app1/main-aspect/resources/i18n/en/en_EN.properties"
		});
	}
	
	@Test
	public void appAspectLevelRequestForApp1WithLanguage() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/main-aspect")
		.whenRequestReceived("i18n/en_i18n.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en.properties",
			APPLICATIONS_DIR + "/test-app1/main-aspect/resources/i18n/en/en.properties",
		});
	}
	
	@Test
	public void bladesetLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset")
		.whenRequestReceived("i18n/en_EN_i18n.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en_EN.properties",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/i18n/en/en_EN.properties"
		});
	}
	
	@Test
	public void bladesetLevelRequestForApp1WithLanguage() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset")
		.whenRequestReceived("i18n/en_i18n.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en.properties",
		});
	}
	
	@Test
	public void bladeLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1")
		.whenRequestReceived("i18n/en_EN_i18n.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en_EN.properties",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/i18n/en/en_EN.properties",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/i18n/en/en_EN.properties"
		});
	}
	
	@Test
	public void bladeLevelRequestForApp1WithLanguage() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1")
		.whenRequestReceived("i18n/en_i18n.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en.properties",
		});
	}
	
	@Test
	public void workbenchLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench")
		.whenRequestReceived("i18n/en_EN_i18n.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en_EN.properties", 
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/i18n/en/en_EN.properties",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/i18n/en/en_EN.properties",
			APPLICATIONS_DIR + "/test-app1/default-aspect/resources/i18n/en/en_EN.properties",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/resources/i18n/en/en_EN.properties"
		});
	}
	
	@Test
	public void workbenchLevelRequestForApp1WithLanguage() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench")
		.whenRequestReceived("i18n/en_i18n.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en.properties",
			APPLICATIONS_DIR + "/test-app1/default-aspect/resources/i18n/en/en.properties",
			
		});
	}
}
