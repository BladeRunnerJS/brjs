package com.caplin.cutlass.command.testIntegration;

import java.io.File;
import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.bladerunnerjs.logging.Logger;

import org.bladerunnerjs.model.StaticModelAccessor;


public class IntegrationTestRunner
{
	private Logger logger = StaticModelAccessor.root.logger(IntegrationTestRunner.class);
	
	public Result runTests(File runnerConf, List<Class<?>> classes) throws Exception {
		logger.info("Running tests '" + classes.toString() + "'");
		
		JUnitCore testRunner = new JUnitCore();
		testRunner.addListener(new CutlassIntegrationTestRunListener());

		Result testResult = testRunner.run(classes.toArray(new Class<?>[0]));
		return testResult;
	}
	
}
