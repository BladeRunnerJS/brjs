package com.caplin.cutlass.command.testIntegration;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.model.exception.command.CommandOperationException;

public class TestCompilerTest
{

	private static final String TEST_BASE = "src/test/resources/testCompiler";
	private static final String WEBDRIVER_DIR = TEST_BASE+"/apps/app1/main-aspect/tests/test-integration/webdriver";
	private TestCompiler testCompiler;
	
	
	
	@Before
	public void setup() throws CommandOperationException
	{
		testCompiler = new TestCompiler();
	}
	
	@Test
	public void testCorrectClassPathIsReturnedForFiles() 
	{
		assertEquals( "Test", testCompiler.getTestClassName(new File(WEBDRIVER_DIR,"/tests/Test.java")) );
		assertEquals( "Test", testCompiler.getTestClassName(new File(WEBDRIVER_DIR,"/tests/Test.class")) );
		assertEquals( "pkg1.pkg2.Test", testCompiler.getTestClassName(new File(WEBDRIVER_DIR,"/tests/pkg1/pkg2/Test.java")) );
		assertEquals( "pkg1.pkg2.Test", testCompiler.getTestClassName(new File(WEBDRIVER_DIR,"/tests/pkg1/pkg2/Test.class")) );
	}
	
}
