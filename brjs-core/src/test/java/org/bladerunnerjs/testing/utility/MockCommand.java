package org.bladerunnerjs.testing.utility;

import java.util.Arrays;

import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;


public class MockCommand implements CommandPlugin
{

	BRJS brjs;
	String name;
	String description;
	String usage;
	private final String helpMessage;
	
	public MockCommand(String name, String description, String usage, String helpMessage)
	{
		this.name = name;
		this.description = description;
		this.usage = usage;
		this.helpMessage = helpMessage;
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
	}

	@Override
	public String getCommandName()
	{
		return name;
	}

	@Override
	public String getCommandDescription()
	{
		return description;
	}

	@Override
	public String getCommandUsage()
	{
		return usage;
	}
	
	@Override
	public String getCommandHelp() {
		return helpMessage;
	}
	
	@Override
	public void doCommand(String[] args) throws CommandArgumentsException, CommandOperationException
	{
		brjs.getConsoleWriter().println( String.format( "command '%s' did command with args: (%s)", name, Arrays.toString(args) ) );
	}

}
