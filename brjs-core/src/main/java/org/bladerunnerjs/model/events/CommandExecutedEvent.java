package org.bladerunnerjs.model.events;

import org.bladerunnerjs.api.plugin.Event;


public class CommandExecutedEvent implements Event
{
	private String command;
	private String[] args;
	private String prefix;

	public CommandExecutedEvent(String prefix, String command, String... args) {
		this.prefix = prefix;
		this.command = command;
		this.args = args;
	}
	
	public String getCommand() {
		return command;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getCommandId() {
		return getPrefix()+":"+getCommand();
	}
	
	public String[] getCommandArgs() {
		return args;
	}
}
