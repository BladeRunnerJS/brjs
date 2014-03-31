package com.caplin.cutlass.command.test.testrunner;

import org.junit.Assert;
import org.junit.Test;


public class CmdCreatorTest {
	
	@Test
	public void testBrowserPathsCanBeCreatedUsingDoubleDollarSymbols() throws Exception
	{
		Assert.assertArrayEquals(new String[]{"path/to/browser.exe", "--param1=value1"}, CmdCreator.cmd("path/to/browser.exe$$--param1=value1"));
	}
	
	@Test
	public void testBrowserPathsCanBeCreatedUsingSpaces() throws Exception
	{
		Assert.assertArrayEquals(new String[]{"path/to/browser.exe", "--param1=value1"}, CmdCreator.cmd("path/to/browser.exe --param1=value1"));
	}

}
