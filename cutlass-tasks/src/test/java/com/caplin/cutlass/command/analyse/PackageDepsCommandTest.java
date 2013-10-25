package com.caplin.cutlass.command.analyse;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.PrintStream;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.sinbin.CutlassConfig;
import com.caplin.cutlass.command.CommandTaskTest;

public class PackageDepsCommandTest extends CommandTaskTest
{
	private final File sdkBaseDir = new File("src/test/resources/AnalyserLibraries/" + CutlassConfig.SDK_DIR);
	private PackageDepsCommand packageDepsCommand;

	private ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
	
	@Before
	public void setUp()
	{
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(sdkBaseDir, new PrintStream(byteStream)));
		out = BRJSAccessor.root.getConsoleWriter();
		packageDepsCommand = new PackageDepsCommand();
	}

	@Test
	public void testCaplinPackage() throws Exception
	{
		String[] args = { "a1", "caplin.package1" };
		
		packageDepsCommand.doCommand(args);		
		String result = byteStream.toString().trim();
		
		assertEquals("caplin.package2.Pack2Class", result);
	}
	
	@Test
	public void testUserLibPackage() throws Exception
	{
		String[] args = { "a1", "userlib.package1" };
		
		packageDepsCommand.doCommand(args);		
		String result = byteStream.toString().trim();
		
		assertEquals("caplin.package1.Pack1Class\n"
				+ "caplin.package2.Pack2Class\n"
				+ "userlib.package2.Pack2Class", result);
	}
	
	@Test
	public void testCaplinPackageSummary() throws Exception
	{
		
		String[] args = { "a1", "caplin.package1", "summary" };
		
		packageDepsCommand.doCommand(args);
		String result = byteStream.toString().trim();
		
		assertEquals("caplin.package2", result);
	}
	
	@Test
	public void testUserLibPackageSummary() throws Exception
	{
		String[] args = { "a1", "userlib.package1", "summary" };
		
		packageDepsCommand.doCommand(args);
		String result = byteStream.toString().trim();
		
		assertEquals("caplin.package1\n"
				+ "caplin.package2\n"
				+ "userlib.package2", result);
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void testNoArgs() throws Exception
	{
		String[] args = {  };
		
		packageDepsCommand.doCommand(args);
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void testTooManyArgs() throws Exception
	{
		String[] args = { "appName", "package", "summaryFlag", "erroneousArg" };
		
		packageDepsCommand.doCommand(args);
	}
	
	@Test  (expected=CommandArgumentsException.class)
	public void testBadSummaryArg() throws Exception
	{
		String[] args = { "a1", "caplin.package1", "ZZZZZZ"};
		
		packageDepsCommand.doCommand(args);
	}
	
	@Test  (expected=CommandArgumentsException.class)
	public void testNonExistentPackageName() throws Exception
	{
		String[] args = { "a1", "caplin.XXXXXXX"};
		
		packageDepsCommand.doCommand(args);
	}
}
