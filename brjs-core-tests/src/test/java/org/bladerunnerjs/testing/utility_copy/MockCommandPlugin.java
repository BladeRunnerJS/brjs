package org.bladerunnerjs.testing.utility_copy;

import java.util.Arrays;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.plugin.CommandPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractCommandPlugin;


public class MockCommandPlugin extends AbstractCommandPlugin implements CommandPlugin
{

	BRJS brjs;
	String name;
	String description;
	String usage;
	private final String helpMessage;
	RuntimeException throwException;
	
	public MockCommandPlugin()
	{
		this("mock", "mock command", "I'm a mock!", "no help for you", null);
	}
	
	public MockCommandPlugin(String name, String description, String usage, String helpMessage)
	{
		this(name, description, usage, helpMessage, null);
	}
	
	public MockCommandPlugin(String name, String description, String usage, String helpMessage, RuntimeException throwException)
	{
		this.name = name;
		this.description = description;
		this.usage = usage;
		this.helpMessage = helpMessage;
		this.throwException = throwException;
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
		if (throwException != null) { throw throwException; }
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
	public int doCommand(String... args) throws CommandArgumentsException, CommandOperationException
	{
		brjs.logger(this.getClass()).println( String.format( "command '%s' did command with args: (%s)", name, Arrays.toString(args) ) );
		return 0;
	}

}
