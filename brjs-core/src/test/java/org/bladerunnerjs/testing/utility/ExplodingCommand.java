package org.bladerunnerjs.testing.utility;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.base.AbstractCommandPlugin;

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
	public void doCommand(String[] args) throws CommandArgumentsException, CommandOperationException {
		throw new CommandOperationException("Bang!");
	}
}