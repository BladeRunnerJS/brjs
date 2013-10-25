package com.caplin.cutlass.bundler.html;

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

import org.bladerunnerjs.model.DirNode;

public class HtmlBundlerFileAppenderTest 
{	
	private HtmlBundlerFileAppender htmlBundlerFileAppender;

	@Before
	public void setUp()
	{
		htmlBundlerFileAppender = new HtmlBundlerFileAppender();
	}

	@Test
	public void appendSdkFilesAddsNothingIfSdkRootDirectoryDoesNotExist()
	{
		List<File> actualFileList = new ArrayList<File>();
		htmlBundlerFileAppender.appendLibraryResourceFiles(new File("blah"), actualFileList);
		assertEquals(Collections.emptyList(), actualFileList);
	}

	@Test
	public void appendSdkFilesAddsResourceDirIfItExists()
	{
		BRJS brjs = BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File("src/test/resources/generic-bundler/bundler-structure-tests")));

		DirNode resourcesDir = brjs.sdkLib().resources();
		
		File resourceDir = new File(resourcesDir.dir(), "caplin/chart");
		File speculativeFile = new File(resourceDir, "somehtml.html");

		List<File> expectedFileList = new ArrayList<File>(Arrays.asList(speculativeFile));
		List<File> actualFileList = new ArrayList<File>();
		
		htmlBundlerFileAppender.appendLibraryResourceFiles(resourceDir, actualFileList);
		assertEquals(expectedFileList, actualFileList);
	}

}
