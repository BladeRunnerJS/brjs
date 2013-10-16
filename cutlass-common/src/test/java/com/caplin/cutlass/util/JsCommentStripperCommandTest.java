package com.caplin.cutlass.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.model.utility.FileUtility;

public class JsCommentStripperCommandTest
{
	private File testInputDir;
	private File expectedOutputDir;
	private File actualOutputDir;
	
	@Before
	public void setUp() throws IOException
	{
		File testResourceDir = new File("src/test/resources/JsCommentStripperCommandTest");
		
		actualOutputDir = FileUtility.createTemporaryDirectory("tempDirForResources");
		testInputDir = new File(testResourceDir, "input");
		expectedOutputDir = new File(testResourceDir, "expected-output");
	}
	
	@Test
	public void tempDirStartsEmpty()
	{
		assertEquals(0, actualOutputDir.listFiles().length);
	};
	
	@Test
	public void allTestsWithinTheDirectoryAreProcessed() throws FileNotFoundException, IOException
	{
		JsCommentStripperCommand.main(new String[]{testInputDir.getAbsolutePath(), actualOutputDir.getAbsolutePath()});
		
		assertEquals(3, actualOutputDir.listFiles().length);
		assertEquals("Class1.js", getExpectedFile("Class1.js"), getActualFile("Class1.js"));
		assertEquals("Class2.js", getExpectedFile("Class2.js"), getActualFile("Class2.js"));
		assertEquals("pkg/Class3.js", getExpectedFile("pkg/Class3.js"), getActualFile("pkg/Class3.js"));
	}
	
	private String getExpectedFile(String path) throws IOException
	{
		return FileUtils.readFileToString(new File(expectedOutputDir, path), String.valueOf(Charset.defaultCharset()));
	}
	
	private String getActualFile(String path) throws IOException
	{
		return FileUtils.readFileToString(new File(actualOutputDir, path), String.valueOf(Charset.defaultCharset()));
	}
}