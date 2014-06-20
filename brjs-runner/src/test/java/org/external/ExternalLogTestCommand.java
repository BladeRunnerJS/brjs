package org.external;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.runner.LogTestCommand;

public class ExternalLogTestCommand extends LogTestCommand {
	@Override
	public void setBRJS(BRJS brjs) {
		logger = brjs.logger(getClass());
	}
	
	@Override
	public String getCommandName() {
		return "external-log-test";
	}
}
