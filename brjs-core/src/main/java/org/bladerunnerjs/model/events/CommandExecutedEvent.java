package org.bladerunnerjs.model.events;

import org.bladerunnerjs.plugin.Event;


public class CommandExecutedEvent implements Event
{
	private String command;
	private String[] args;

	public CommandExecutedEvent(String command, String... args) {
		this.command = command;
		this.args = args;
	}
	
	public String getCommand() {
		return command;
	}
	
	public String[] getCommandArgs() {
		return args;
	}
}
