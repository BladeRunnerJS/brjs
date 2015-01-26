package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.plugin.CommandPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractCommandPlugin;

public class ExplodingCommand extends AbstractCommandPlugin implements CommandPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public String getCommandName() {
		return "explode";
	}

	@Override
	public String getCommandDescription() {
		return "A command that explodes when you use it";
	}
	
	@Override
	public String getCommandUsage() {
		return "";
	}
	
	@Override
	public String getCommandHelp() {
		return "Pull the trigger and run.";
	}
	
	@Override
	public int doCommand(String... args) throws CommandArgumentsException, CommandOperationException {
		throw new CommandOperationException("Bang!");
	}
}