package com.caplin.cutlass.command.analyse;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.command.analyse.AnalyserConfig;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;
import com.caplin.cutlass.structure.model.path.AspectPath;
import com.caplin.cutlass.structure.model.path.RootPath;
import com.caplin.cutlass.structure.model.path.AppPath;

public class AnalyserConfigTest 
{
	private File sdkBaseDir;
	private AnalyserConfig config;
	
	@Before
	public void setUp()
	{
		sdkBaseDir = new File("src/test/resources/AnalyseApplicationCommandTest/structure-tests/" + CutlassConfig.SDK_DIR);
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(sdkBaseDir));
	}
	
	@After
	public void tearDown()
	{
		
	}
	
	@Test
	public void testAnalyserConfigWithoutProvidingAspect() throws CommandArgumentsException
	{
		AppPath appPath = new RootPath(sdkBaseDir.getParentFile()).appsPath().appPath("app1");
		AspectPath aspectPath = appPath.aspectPath("default");
		
		assertTrue(appPath.getDir().exists());
		assertTrue(aspectPath.getDir().exists());
		
		config = new AnalyserConfig(new String[] {"app1"}, null);
		
		assertTrue(config.getApplicationDirectory().equals(appPath.getDir()));
		assertTrue(config.getAspectDirectory().equals(aspectPath.getDir()));
		assertTrue(config.getOutputFormatInJson() == false);
	}

	@Test
	public void testAnalyserConfigWhenProvidingAspect() throws CommandArgumentsException
	{
		File appDir = new File(sdkBaseDir.getParentFile(), CutlassConfig.APPLICATIONS_DIR + "/app2");
		File aspectDir = new File(appDir, "xtra-aspect");
		
		assertTrue(appDir.exists());
		assertTrue(aspectDir.exists());
		
		config = new AnalyserConfig(new String[] {"app2", "xtra"}, null);
		
		assertTrue(config.getApplicationDirectory().equals(appDir));
		assertTrue(config.getAspectDirectory().equals(aspectDir));
		assertTrue(config.getOutputFormatInJson() == false);
	}
	
	@Test(expected=CommandArgumentsException.class)
	public void testAnalyseConfigWithNoArguments() throws CommandArgumentsException
	{
		config = new AnalyserConfig(new String[] {}, null);
	}
	
	@Test(expected=CommandArgumentsException.class)
	public void testAnalyserConfigWithAppThatDoesNotExist() throws CommandArgumentsException
	{
		config = new AnalyserConfig(new String[] {"doesNotExist"}, null);
	}
	
	@Test
	public void testAnalyserConfigWhenPassingValidOptionalFormatParameter() throws CommandArgumentsException
	{
		config = new AnalyserConfig(new String[] {"app2", "xtra", "json"}, null);
		assertTrue(config.getOutputFormatInJson() == true);
	}
	
	@Test
	public void testAnalyserConfigWhenPassingValidOptionalFormatParameterInUppercase() throws CommandArgumentsException
	{
		config = new AnalyserConfig(new String[] {"app2", "xtra", "JSON"}, null);
		assertTrue(config.getOutputFormatInJson() == true);
	}
	
	@Test(expected=CommandArgumentsException.class)
	public void testAnalyserConfigWhenPassingInvalidOptionalFormatParameter() throws CommandArgumentsException
	{
		config = new AnalyserConfig(new String[] {"app2", "xtra", "pdf"}, null);
		assertTrue(config.getOutputFormatInJson() == true);
	}
	
	@Test(expected=CommandArgumentsException.class)
	public void testAnalyserConfigWhenProvidingUnnecessaryExtraArguments() throws CommandArgumentsException
	{
		config = new AnalyserConfig(new String[] {"app2", "xtra", "json", "blbla"}, null);
	}
}
