package org.bladerunnerjs.utility;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.LogLevelAccessor;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NoSuchCommandException;
import org.bladerunnerjs.plugin.plugins.commands.core.HelpCommand;
import org.bladerunnerjs.plugin.utility.command.CommandList;

public class UserCommandRunner {
	
	public static int run(BRJS brjs, CommandList commandList, LogLevelAccessor logLevelAccessor, String args[]) throws CommandOperationException {
		return doRunCommand(brjs, args);
	}

	private static int doRunCommand(BRJS brjs, String[] args) throws CommandOperationException
	{
		Logger logger = brjs.logger(UserCommandRunner.class);
		try {
			return brjs.runCommand(args);
		}
		catch (NoSuchCommandException e) {
			if (e.getCommandName().length() > 0)
			{
				logger.println(e.getMessage());
				logger.println("--------");
				logger.println("");
			}
			return doRunCommand(brjs, new String[] {new HelpCommand().getCommandName() });
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
}