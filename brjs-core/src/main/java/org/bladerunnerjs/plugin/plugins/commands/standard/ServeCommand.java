package org.bladerunnerjs.plugin.plugins.commands.standard;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.bladerunnerjs.appserver.ApplicationServer;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.RelativePathUtility;

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
		public static final String OUTDATED_JAR_MESSAGE = "The app '%s' contains outdated BRJS jar(s)."+
		" You should delete all jars prefixed with '%s' in the WEB-INF/lib directory and copy in new versions from %s.";
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
			
			checkApplicationLibVersions();
			
			appServer.start();
			
			logger.println("\n");
			logger.println(Messages.SERVER_STARTUP_MESSAGE + appServer.getPort() + "/");
			logger.println(Messages.SERVER_STOP_INSTRUCTION_MESSAGE + "\n");
			
			appServer.join();
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
	
	private void checkApplicationLibVersions()
	{
		for (App app : brjs.userApps()) {
			checkApplicationLibVersions(app);
		}
	}
	
	private void checkApplicationLibVersions(App app)
	{
		File webinfLib = app.file("WEB-INF/lib");
		File appJarsDir = app.root().appJars().dir();
		if (!webinfLib.exists() || !appJarsDir.exists()) {
			return;
		}
		
		boolean containsInvalidJars = false;
		
		for (File appJar : FileUtils.listFiles(webinfLib, new PrefixFileFilter("brjs-"), null)) {
			File sdkJar = app.root().appJars().file(appJar.getName());
			if (!sdkJar.exists()) {
				containsInvalidJars = true;
			}
		}
		
		for (File sdkJar : FileUtils.listFiles(appJarsDir, new PrefixFileFilter("brjs-"), null)) {
			File appJar = new File(webinfLib, sdkJar.getName());
			if (!appJar.exists()) {
				containsInvalidJars = true;
			}
		}
		
		if (containsInvalidJars) {
			logger.warn( Messages.OUTDATED_JAR_MESSAGE, app.getName(), "brjs-", RelativePathUtility.get(app.root(), app.root().dir(), appJarsDir) );
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
