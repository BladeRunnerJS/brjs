package com.caplin.cutlass.conf;

import java.io.File;
import java.io.FileNotFoundException;

import org.bladerunnerjs.model.StaticModelAccessor;

public class TestRunnerConfLocator
{
	public static File getTestRunnerConf() throws FileNotFoundException
	{
		File testRunnerConf = StaticModelAccessor.root.file("conf/test-runner.conf");
		
		if (!testRunnerConf.exists() || !testRunnerConf.isFile())
		{
			throw new FileNotFoundException("Test runner config file does not exist at " + testRunnerConf.getAbsolutePath());
		}
		
		return testRunnerConf;
	}
}
