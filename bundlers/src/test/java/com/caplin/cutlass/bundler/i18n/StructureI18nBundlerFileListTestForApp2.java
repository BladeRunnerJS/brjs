package com.caplin.cutlass.bundler.i18n;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

import com.caplin.cutlass.bundler.BundlerFileTester;
	
public class StructureI18nBundlerFileListTestForApp2 {
	
	private BundlerFileTester test;
		
		@Before
		public void setUp()
		{
			test = new BundlerFileTester(new I18nBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
		}
		
		@Test @Ignore
		public void appAspectLevelRequestForApp2DefaultAspect() throws Exception
		{
			test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/default-aspect")
			.whenRequestReceived("i18n/en_US_i18n.bundle")
			.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en_US.properties", 
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/resources/i18n/en/en_US.properties",	
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/i18n/en/en_US.properties",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/resources/i18n/en/en_US.properties",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/resources/i18n/en/en_US.properties", 
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/i18n/en/en_US.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/resources/i18n/en/en_US.properties",
				APPLICATIONS_DIR + "/test-app2/default-aspect/resources/i18n/en/en_US.properties"
			});
		}
		
		@Test @Ignore
		public void appAspectLevelRequestForApp2DefaultAspectWithLanguage() throws Exception
		{
			test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/default-aspect")
			.whenRequestReceived("i18n/en_i18n.bundle")
			.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en.properties",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/resources/i18n/en/en.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/i18n/en/en.properties",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/resources/i18n/en/en.properties",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/resources/i18n/en/en.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/i18n/en/en.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/resources/i18n/en/en.properties",
				APPLICATIONS_DIR + "/test-app2/default-aspect/resources/i18n/en/en.properties",
			});
		}
		
		@Test @Ignore
		public void appAspectLevelRequestForApp2AlternateAspect() throws Exception
		{
			test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/xtra-aspect")
			.whenRequestReceived("i18n/en_US_i18n.bundle")
			.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en_US.properties", 
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/resources/i18n/en/en_US.properties",	
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/i18n/en/en_US.properties",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/resources/i18n/en/en_US.properties",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/resources/i18n/en/en_US.properties", 
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/i18n/en/en_US.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/resources/i18n/en/en_US.properties",
				APPLICATIONS_DIR + "/test-app2/xtra-aspect/resources/i18n/en/en_US.properties"
			});
		}
		
		@Test @Ignore
		public void appAspectLevelRequestForApp2AlternateAspectWithLanguage() throws Exception
		{
			test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/xtra-aspect")
			.whenRequestReceived("i18n/en_i18n.bundle")
			.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en.properties",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/resources/i18n/en/en.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/i18n/en/en.properties",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/resources/i18n/en/en.properties",
				APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/resources/i18n/en/en.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/i18n/en/en.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/resources/i18n/en/en.properties",
				APPLICATIONS_DIR + "/test-app2/xtra-aspect/resources/i18n/en/en.properties",
			});
		}
		
		@Test @Ignore
		public void bladesetLevelRequestForApp2() throws Exception
		{
			test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset")
			.whenRequestReceived("i18n/en_US_i18n.bundle")
			.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en_US.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/i18n/en/en_US.properties"
			});
		}
		
		@Test @Ignore
		public void bladesetLevelRequestForApp2WithLanguage() throws Exception
		{
			test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset")
			.whenRequestReceived("i18n/en_i18n.bundle")
			.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/i18n/en/en.properties"
			});
		}
		
		@Test @Ignore
		public void bladeLevelRequestForApp2() throws Exception
		{
			test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1")
			.whenRequestReceived("i18n/en_US_i18n.bundle")
			.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en_US.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/i18n/en/en_US.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/i18n/en/en_US.properties" 
			});
		}
		
		@Test @Ignore
		public void bladeLevelRequestForApp2WithLanguage() throws Exception
		{
			test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1")
			.whenRequestReceived("i18n/en_i18n.bundle")
			.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/i18n/en/en.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/i18n/en/en.properties"
			});
		}
		
		@Test @Ignore
		public void workbenchLevelRequestForApp2() throws Exception
		{
			test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench")
			.whenRequestReceived("i18n/en_US_i18n.bundle")
			.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en_US.properties",  
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/i18n/en/en_US.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/i18n/en/en_US.properties",
				APPLICATIONS_DIR + "/test-app2/default-aspect/resources/i18n/en/en_US.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench/resources/i18n/en/en_US.properties"
			});
		}
		
		@Test @Ignore
		public void workbenchLevelRequestForApp2WithLanguage() throws Exception
		{
			test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench")
			.whenRequestReceived("i18n/en_i18n.bundle")
			.thenBundledFilesEquals(new String[] {
				SDK_DIR + "/libs/javascript/caplin/resources/caplin/alerts/en.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/i18n/en/en.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/i18n/en/en.properties",
				APPLICATIONS_DIR + "/test-app2/default-aspect/resources/i18n/en/en.properties",
				APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench/resources/i18n/en/en.properties",
			});
		}
	}

