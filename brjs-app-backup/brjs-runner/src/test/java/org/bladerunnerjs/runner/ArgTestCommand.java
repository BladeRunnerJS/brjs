package org.bladerunnerjs.runner;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.plugin.base.AbstractCommandPlugin;

import com.google.common.base.Joiner;

public class ArgTestCommand extends AbstractCommandPlugin {
	private Logger logger;
	
	@Override
	public void setBRJS(BRJS brjs) {
		logger = brjs.logger(getClass());
	}
	
	@Override
	public String getCommandName() {
		return "arg-test";
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
		logger.warn(Joiner.on(", ").join(args));
		
		return 0;
	}
}
