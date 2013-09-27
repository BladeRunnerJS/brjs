package com.caplin.cutlass.command.bladerunner;

import java.io.IOException;

import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.appserver.ApplicationServer;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;

import com.caplin.cutlass.command.LegacyCommandPlugin;

public class BladerunnerCommand implements LegacyCommandPlugin
{
	public class Messages {
		public static final String SERVER_STARTUP_MESSAGE = "Bladerunner server is now running and can be accessed at http://localhost:";
		public static final String SERVER_STOP_INSTRUCTION_MESSAGE = "Press Ctrl + C to stop the server";
	}
	
	private ApplicationServer appServer;
	private BRJS brjs;
	private Logger logger;
	
	public BladerunnerCommand(BRJS brjs)
	{
		setBRJS(brjs);
	}
	
	public BladerunnerCommand(BRJS brjs, ApplicationServer appServer)
	{
		this.appServer = appServer;
		setBRJS(brjs);
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
		logger = brjs.logger(LoggerType.APP_SERVER, this.getClass());
	}
	
	@Override
	public String getCommandName()
	{
		return "start";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Start the embedded application server and database.";
	}
	
	@Override
	public String getCommandUsage()
	{
		return "";
	}
	
	@Override
	public String getCommandHelp() {
		return getCommandUsage();
	}
	
	@Override
	public void doCommand(String[] args) throws CommandArgumentsException, CommandOperationException
	{
		if (args.length > 0)
		{
			throw new CommandOperationException("This command does not take any arguments.");
		}
		
		startAppServer();
	}

	private void startAppServer() throws CommandOperationException
	{
		try
		{
			if (appServer == null)
			{
				appServer = brjs.applicationServer();
			}
			
			appServer.start();
			
			logger.info("\n\t" + Messages.SERVER_STARTUP_MESSAGE + appServer.getPort() + "/");
			logger.info("\t" + Messages.SERVER_STOP_INSTRUCTION_MESSAGE + "\n");
		}
		catch (IOException e)
		{
			throw new CommandOperationException(e);
		}
		catch (Exception ex)
		{
			throw new CommandOperationException("Error creating application server.", ex);
		}
	}
}
