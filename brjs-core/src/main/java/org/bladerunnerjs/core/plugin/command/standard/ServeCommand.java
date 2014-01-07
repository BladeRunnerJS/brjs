package org.bladerunnerjs.core.plugin.command.standard;

import java.io.IOException;

import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;
import org.bladerunnerjs.core.plugin.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.appserver.ApplicationServer;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;

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
		logger = brjs.logger(LoggerType.APP_SERVER, this.getClass());
	}
	
	@Override
	public String getCommandName()
	{
		return "serve";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Starts the embedded application server and database.";
	}
	
	
	@Override
	public String getCommandHelp() {
		return getCommandUsage();
	}

	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException
	{
		argsParser.registerParameter(new FlaggedOption("port").setShortFlag('p').setRequired(false).setHelp("the port number to run the BRJS application (overrides config)"));
	}

	@Override
	protected void doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException
	{
		try
		{	
			if (appServer == null)
			{
				appServer = getApplicationServer(parsedArgs);
			}
			
			appServer.start();
			
			logger.info("\n\t" + Messages.SERVER_STARTUP_MESSAGE + appServer.getPort() + "/");
			logger.info("\t" + Messages.SERVER_STOP_INSTRUCTION_MESSAGE + "\n");
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
	}
	
	private ApplicationServer getApplicationServer(JSAPResult parsedArgs) throws NumberFormatException, ConfigException
	{
	    if(parsedArgs.contains("port"))
	    {
	    	int port = Integer.parseInt(parsedArgs.getString("port")); 
	    	return brjs.applicationServer(port); 
	    }
       
	    return brjs.applicationServer();
	}
}
