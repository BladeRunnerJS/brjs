package com.caplin.cutlass.bundler.xml;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.model.BRJS;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

public class XmlBundlerFileAppenderTest
{
	private XmlBundlerFileAppender xmlBundlerFileAppender;
	private File ROOT_LOCATION = new File("src/test/resources/generic-bundler/bundler-structure-tests");
	private BRJS brjs;

	@Before
	public void setUp()
	{
		xmlBundlerFileAppender = new XmlBundlerFileAppender();
		brjs = BRJSAccessor.initialize(BRJSTestFactory.createBRJS(ROOT_LOCATION));
	}

	@Test
	public void appendSdkFilesAddsNothingIfSdkRootDirectoryDoesNotExist()
	{
		List<File> actualFileList = new ArrayList<File>();
		xmlBundlerFileAppender.appendLibraryResourceFiles(new File("blah"), actualFileList);
		assertEquals(Collections.emptyList(), actualFileList);
	}

	@Test
	public void appendSdkFilesAddsAnyXmlResources()
	{
		File resourcesDir = brjs.sdkLib().assetLocation("resources").dir();
		File resourceDir = new File(resourcesDir, "caplin/grid");
		File speculativeFile = new File(resourceDir, "gridDefinitions.xml");

		List<File> expectedFileList = new ArrayList<File>(Arrays.asList(speculativeFile));
		List<File> actualFileList = new ArrayList<File>();
		
		xmlBundlerFileAppender.appendLibraryResourceFiles(resourceDir, actualFileList);
		assertEquals(expectedFileList, actualFileList);
	}
}
