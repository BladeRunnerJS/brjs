package org.bladerunnerjs.runner;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.plugin.base.AbstractCommandPlugin;

public class LogTestCommand extends AbstractCommandPlugin {
	protected Logger logger;
	
	@Override
	public void setBRJS(BRJS brjs) {
		logger = brjs.logger(getClass());
	}
	
	@Override
	public String getCommandName() {
		return "log-test";
	}

	@Override
	public String getCommandDescription() {
		return "";
	}

	@Override
	public String getCommandUsage() {
		return "";
	}
	
	@Override
	public String getCommandHelp() {
		return "";
	}
	
	@Override
	public int doCommand(String... args) throws CommandArgumentsException, CommandOperationException {
		logger.error("error-level");
		logger.warn("warn-level");
		logger.info("info-level");
		logger.console("console-level");
		logger.debug("debug-level");
		return 0;
	}
}
