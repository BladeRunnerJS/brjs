package org.bladerunnerjs.plugin.commands.standard;

import java.io.IOException;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.appserver.ApplicationServer;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.plugin.ArgsParsingCommandPlugin;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public class ServeCommand extends ArgsParsingCommandPlugin
{
	public class Messages {
		public static final String SERVER_STARTUP_MESSAGE = "BladerunnerJS server is now running and can be accessed at http://localhost:";
		public static final String SERVER_STOP_INSTRUCTION_MESSAGE = "Press Ctrl + C to stop the server";
		public static final String INVALID_PORT_MESSAGE = "Unable to serve BladeRunnerJS with invalid port value";
	}
	
	private ApplicationServer appServer;
	private BRJS brjs;
	private Logger logger;
	
	public ServeCommand()
	{
	}
	
	/* this should only be used for testing */
	public ServeCommand(ApplicationServer appServer)
	{
		this.appServer = appServer;
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
		logger = brjs.logger(this.getClass());
	}
	
	@Override
	public String getCommandName()
	{
		return "serve";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Starts the embedded application server.";
	}
	
	
	@Override
	public String getCommandHelp() {
		return getCommandUsage();
	}

	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException
	{
		argsParser.registerParameter(new FlaggedOption("port").setShortFlag('p').setLongFlag("port").setRequired(false).setHelp("the port number to run the BRJS application (overrides config)"));
	}

	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException
	{
		try
		{			
			if (appServer == null)
			{
				appServer = getApplicationServer(parsedArgs);
			}
			
			appServer.start();
			brjs.getFileWatcherThread().start();
			
			logger.println("\n");
			logger.println(Messages.SERVER_STARTUP_MESSAGE + appServer.getPort() + "/");
			logger.println(Messages.SERVER_STOP_INSTRUCTION_MESSAGE + "\n");
			
			appServer.join();
			brjs.getFileWatcherThread().interrupt();
			brjs.getFileWatcherThread().join();
		}
		catch(NumberFormatException e)
		{
			throw new CommandArgumentsException(Messages.INVALID_PORT_MESSAGE + " '" + parsedArgs.getString("port") + "' ", e, this);
		}
		
		catch (IOException e)
		{
			throw new CommandOperationException(e);
		}
		catch (Exception ex)
		{
			throw new CommandOperationException("Error creating application server.", ex);
		}
		
		return 0;
	}

	private ApplicationServer getApplicationServer(JSAPResult parsedArgs) throws NumberFormatException, ConfigException
	{
		if(parsedArgs.contains("port"))
		{
			int port = Integer.parseInt(parsedArgs.getString("port"));
			ApplicationServer appServer = brjs.applicationServer(port);
			appServer.setAppDeploymentWatcherInterval(1000);
			return appServer;
		}
		
		return brjs.applicationServer();
	}
}
