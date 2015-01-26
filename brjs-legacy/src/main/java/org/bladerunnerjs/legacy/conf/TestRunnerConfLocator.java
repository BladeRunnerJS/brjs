package org.bladerunnerjs.legacy.conf;

import java.io.FileNotFoundException;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;

public class TestRunnerConfLocator
{
	public static MemoizedFile getTestRunnerConf() throws FileNotFoundException
	{
		MemoizedFile testRunnerConf = ThreadSafeStaticBRJSAccessor.root.file("conf/test-runner.conf");
		
		if (!testRunnerConf.exists() || !testRunnerConf.isFile())
		{
			throw new FileNotFoundException("Test runner config file does not exist at " + testRunnerConf.getAbsolutePath());
		}
		
		return testRunnerConf;
	}
}
