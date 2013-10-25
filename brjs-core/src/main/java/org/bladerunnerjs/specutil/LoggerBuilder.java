package org.bladerunnerjs.specutil;

import org.bladerunnerjs.specutil.engine.BuilderChainer;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.bladerunnerjs.testing.utility.LogMessageStore;


public class LoggerBuilder
{
	private final BuilderChainer builderChainer;
	private final LogMessageStore logStore;
	
	public LoggerBuilder(SpecTest modelTest, LogMessageStore logStore)
	{
		this.logStore = logStore;
		builderChainer = new BuilderChainer(modelTest);
	}

	public BuilderChainer enabled()
	{
		logStore.enableLogging();
		return builderChainer;
	}

	public BuilderChainer disabled()
	{
		logStore.disableLogging();
		return builderChainer;
	}
}
