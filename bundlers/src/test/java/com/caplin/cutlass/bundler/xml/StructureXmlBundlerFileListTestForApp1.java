package com.caplin.cutlass.bundler.xml;

import org.junit.Before;
import org.junit.Test;
import com.caplin.cutlass.bundler.BundlerFileTester;

import static com.caplin.cutlass.CutlassConfig.APPLICATIONS_DIR;
import static com.caplin.cutlass.CutlassConfig.SDK_DIR;

public class StructureXmlBundlerFileListTestForApp1
{
	private BundlerFileTester test;
	
	@Before
	public void setUp() throws Exception
	{
		test = new BundlerFileTester(new XmlBundler(), "src/test/resources/generic-bundler/bundler-structure-tests");
	}
	
	@Test
	public void appAspectLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/main-aspect")
		.whenRequestReceived("xml.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/randomXmlFile.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/rendererDefinitions.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/grid/gridDefinitions.xml",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/xml/bs1.xml",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/xml/bs2.xml",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/xml/b1.xml", 
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/xml/b2.xml",
			APPLICATIONS_DIR + "/test-app1/main-aspect/resources/xml/m1.xml",
			APPLICATIONS_DIR + "/test-app1/main-aspect/resources/xml/m2.xml"
		});
	}
	
	@Test
	public void bladesetLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset")
		.whenRequestReceived("xml.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/randomXmlFile.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/rendererDefinitions.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/grid/gridDefinitions.xml",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/xml/bs1.xml",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/xml/bs2.xml",
		});
	}
	
	@Test
	public void bladeLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1")
		.whenRequestReceived("xml.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/randomXmlFile.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/rendererDefinitions.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/grid/gridDefinitions.xml",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/xml/bs1.xml",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/xml/bs2.xml",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/xml/b1.xml", 
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/xml/b2.xml",
		});
	}
	
	@Test
	public void workbenchLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench")
		.whenRequestReceived("xml.bundle")
		.thenBundledFilesEquals(new String[] {
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/randomXmlFile.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/element/rendererDefinitions.xml",
			SDK_DIR + "/libs/javascript/caplin/resources/caplin/grid/gridDefinitions.xml",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/xml/bs1.xml",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/resources/xml/bs2.xml",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/xml/b1.xml", 
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/resources/xml/b2.xml",
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/resources/xml/wb1.xml", 
			APPLICATIONS_DIR + "/test-app1/a-bladeset/blades/blade1/workbench/resources/xml/wb2.xml"
		});
	}
}
