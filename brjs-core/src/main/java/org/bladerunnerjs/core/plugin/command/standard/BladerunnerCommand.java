package org.bladerunnerjs.core.plugin.command.standard;

import java.io.IOException;

import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;
import org.bladerunnerjs.core.plugin.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.appserver.ApplicationServer;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class BladerunnerCommand extends ArgsParsingCommandPlugin
{
	public class Messages {
		public static final String SERVER_STARTUP_MESSAGE = "Bladerunner server is now running and can be accessed at http://localhost:";
		public static final String SERVER_STOP_INSTRUCTION_MESSAGE = "Press Ctrl + C to stop the server";
	}
	
	private ApplicationServer appServer;
	private BRJS brjs;
	private Logger logger;
	
	public BladerunnerCommand()
	{
	}
	
	/* this should only be used for testing */
	public BladerunnerCommand(ApplicationServer appServer)
	{
		this.appServer = appServer;
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
	public String getCommandHelp() {
		return getCommandUsage();
	}

	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException
	{
	}

	@Override
	protected void doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException
	{
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
