package org.bladerunnerjs.runner;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.plugin.base.AbstractCommandPlugin;

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
