package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.spec.engine.BuilderChainer;
import org.bladerunnerjs.api.spec.engine.SpecTest;


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
	
	public BuilderChainer echoEnabled() {
		logStore.enableEchoingLogs();
		return builderChainer;
	}
}
