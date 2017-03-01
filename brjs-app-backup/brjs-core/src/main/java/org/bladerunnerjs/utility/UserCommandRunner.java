package org.bladerunnerjs.utility;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.model.exception.command.NoSuchCommandException;
import org.bladerunnerjs.model.LogLevelAccessor;
import org.bladerunnerjs.plugin.utility.CommandList;

public class UserCommandRunner {
	
	public static int run(BRJS brjs, CommandList commandList, LogLevelAccessor logLevelAccessor, String args[]) throws CommandOperationException {
		return doRunCommand(brjs, args);
	}

	private static int doRunCommand(BRJS brjs, String[] args) throws CommandOperationException
	{
		Logger logger = brjs.logger(UserCommandRunner.class);
		try {
			checkApplicationLibVersions(brjs, logger);
			return brjs.runCommand(args);
		}
		catch (NoSuchCommandException e) {
			if (e.getCommandName().length() > 0)
			{
				logger.println(e.getMessage());
				logger.println("--------");
				logger.println("");
			}
			if (!e.getCommandName().equals("help")) { // prevent an infinite loop if the help command doesnt exist
				return doRunCommand(brjs, new String[] {"help"});				
			}
		}
		catch (CommandArgumentsException e) {
			logger.println("Problem:");
			logger.println("  " + e.getMessage());
			logger.println("");
			logger.println("Usage:");
			logger.println("  brjs " + e.getCommand().getCommandName() + " " + e.getCommand().getCommandUsage());
		}
		catch (CommandOperationException e) {
			logger.println("Error:");
			logger.println("  " + e.getMessage());
			
			if (e.getCause() != null) {
				logger.println("");
				logger.println("Caused By:");
				logger.println("  " + e.getCause().getMessage());
			}
			
			logger.println("");
			logger.println("Stack Trace:");
			StringWriter stackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(stackTrace));
			logger.println(stackTrace.toString());
			
			throw e;
		}
		return -1;
	}
	
	
	private static void checkApplicationLibVersions(BRJS brjs, Logger logger)
	{
		for (App app : brjs.userApps()) {
			if (!MissingAppJarChecker.hasCorrectApplicationLibVersions(app)) {
				logger.warn( new MissingAppJarsException(app).getMessage() );
			}
		}
	}
	
}