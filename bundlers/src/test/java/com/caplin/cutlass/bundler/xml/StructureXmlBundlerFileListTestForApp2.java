package com.caplin.cutlass.bundler.xml;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.caplin.cutlass.bundler.BundlerFileTester;

public class StructureXmlBundlerFileListTestForApp2
{
	private BundlerFileTester test;
	
	@Before
	public void setUp() throws Exception
	{
		test = new BundlerFileTester(new XmlBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
	}

	@Test @Ignore
	public void appAspectLevelRequestForApp2DefaultAspect() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/default-aspect")
		.whenRequestReceived("xml.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/randomXmlFile.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/rendererDefinitions.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/grid/gridDefinitions.xml",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/resources/xml/fi-1.xml",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/resources/xml/fi-2.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/xml/fx-1.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/xml/fx-2.xml",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/resources/xml/fi-b11.xml",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/resources/xml/fi-b12.xml",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/resources/xml/fi-b21.xml",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/resources/xml/fi-b22.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/xml/fx-b11.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/xml/fx-b12.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/resources/xml/fx-b21.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/resources/xml/fx-b22.xml",
			APPLICATIONS_DIR + "/test-app2/default-aspect/resources/xml/m1.xml",
			APPLICATIONS_DIR + "/test-app2/default-aspect/resources/xml/m2.xml"
		});
	}
	
	@Test @Ignore
	public void appAspectLevelRequestForApp2AlternateAspect() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/xtra-aspect")
		.whenRequestReceived("xml.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/randomXmlFile.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/rendererDefinitions.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/grid/gridDefinitions.xml",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/resources/xml/fi-1.xml",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/resources/xml/fi-2.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/xml/fx-1.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/xml/fx-2.xml",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/resources/xml/fi-b11.xml",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade1/resources/xml/fi-b12.xml",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/resources/xml/fi-b21.xml",
			APPLICATIONS_DIR + "/test-app2/fi-bladeset/blades/fi-blade2/resources/xml/fi-b22.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/xml/fx-b11.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/xml/fx-b12.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/resources/xml/fx-b21.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade2/resources/xml/fx-b22.xml",
			APPLICATIONS_DIR + "/test-app2/xtra-aspect/resources/xml/x1.xml",
			APPLICATIONS_DIR + "/test-app2/xtra-aspect/resources/xml/x2.xml"
		});
	}
	
	@Test @Ignore
	public void bladesetLevelRequestForApp2() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset")
		.whenRequestReceived("xml.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/randomXmlFile.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/rendererDefinitions.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/grid/gridDefinitions.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/xml/fx-1.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/xml/fx-2.xml"
		});
	}
	
	@Test @Ignore
	public void bladeLevelRequestForApp2() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1")
		.whenRequestReceived("xml.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/randomXmlFile.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/rendererDefinitions.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/grid/gridDefinitions.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/xml/fx-1.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/xml/fx-2.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/xml/fx-b11.xml", 
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/xml/fx-b12.xml"
		});
	}
	
	@Test @Ignore
	public void workbenchLevelRequestForApp2() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench")
		.whenRequestReceived("xml.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/randomXmlFile.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/rendererDefinitions.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/grid/gridDefinitions.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/xml/fx-1.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/resources/xml/fx-2.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/xml/fx-b11.xml", 
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/resources/xml/fx-b12.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench/resources/xml/fx-b1wb1.xml", 
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench/resources/xml/fx-b1wb2.xml",
			APPLICATIONS_DIR + "/test-app2/fx-bladeset/blades/fx-blade1/workbench/resources/xml/fx-b1wb3.xml"
		});
	}
}
