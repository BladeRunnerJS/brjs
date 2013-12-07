package org.bladerunnerjs.plugin.command;

import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.plugin.VirtualProxyPlugin;

public class VirtualProxyCommandPlugin extends VirtualProxyPlugin implements CommandPlugin {
	private CommandPlugin commandPlugin;
	
	public VirtualProxyCommandPlugin(CommandPlugin commandPlugin) {
		super(commandPlugin);
		this.commandPlugin = commandPlugin;
	}
	
	@Override
	public String getCommandName() {
		return commandPlugin.getCommandName();
	}
	
	@Override
	public String getCommandDescription() {
		initializePlugin();
		return commandPlugin.getCommandDescription();
	}
	
	@Override
	public String getCommandUsage() {
		initializePlugin();
		return commandPlugin.getCommandUsage();
	}

	@Override
	public String getCommandHelp() {
		initializePlugin();
		return commandPlugin.getCommandHelp();
	}

	@Override
	public void doCommand(String[] args) throws CommandArgumentsException, CommandOperationException {
		initializePlugin();
		commandPlugin.doCommand(args);
	}
}
