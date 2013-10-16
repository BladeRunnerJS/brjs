package com.caplin.cutlass.bundler.xml;

import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.bundler.BundlerFileTester;
import com.caplin.cutlass.structure.model.path.RootPath;
import com.caplin.cutlass.structure.model.path.SdkPath;

public class StructureXmlBundlerFileListTestForSdkResources
{
	private BundlerFileTester test;

	@Before
	public void setUp() throws Exception
	{
		SdkPath sdkPath = new RootPath("src/test/resources/generic-bundler/bundler-structure-tests").sdkPath();
		
		test = new BundlerFileTester(new XmlBundler(), sdkPath.getPathStr());
	}

	@Test
	public void appAspectLevelRequestForApp1() throws Exception
	{
		test.givenDirectoryOnDisk(".")
		.whenRequestReceived("xml.bundle")
		.thenBundledFilesEquals(new String[] {
			"libs/javascript/caplin/resources/caplin/element/randomXmlFile.xml",
			"libs/javascript/caplin/resources/caplin/element/rendererDefinitions.xml",
			"libs/javascript/caplin/resources/caplin/grid/gridDefinitions.xml"
		});
	}
}