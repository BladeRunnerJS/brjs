package org.bladerunnerjs.utility;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.LogLevelAccessor;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NoSuchCommandException;
import org.bladerunnerjs.plugin.command.CommandList;
import org.bladerunnerjs.plugin.command.core.HelpCommand;
import org.bladerunnerjs.plugin.command.core.VersionCommand;

public class UserCommandRunner {
	public static void run(BRJS brjs, CommandList commandList, LogLevelAccessor logLevelAccessor, String args[]) throws CommandOperationException {
		ConsoleWriter out = brjs.getConsoleWriter();
		
		if (!CommandRunner.extractCommandFromArgs(args).equals(new VersionCommand().getCommandName())) {
			out.println(brjs.versionInfo().toString());
			out.println("");
		}
		
		doRunCommand(brjs, args, out);
	}

	private static void doRunCommand(BRJS brjs, String[] args, ConsoleWriter out) throws CommandOperationException
	{
		try {
			brjs.runCommand(args);
		}
		catch (NoSuchCommandException e) {
			if (e.getCommandName().length() > 0)
			{
				out.println(e.getMessage());
				out.println("--------");
				out.println("");
			}
			doRunCommand(brjs, new String[] {new HelpCommand().getCommandName() }, out);
		}
		catch (CommandArgumentsException e) {
			out.println("Problem:");
			out.println("  " + e.getMessage());
			out.println("");
			out.println("Usage:");
			out.println("  brjs " + e.getCommand().getCommandName() + " " + e.getCommand().getCommandUsage());
		}
		catch (CommandOperationException e) {
			out.println("Error:");
			out.println("  " + e.getMessage());
			
			if (e.getCause() != null) {
				out.println("");
				out.println("Caused By:");
				out.println("  " + e.getCause().getMessage());
			}
			
			out.println("");
			out.println("Stack Trace:");
			StringWriter stackTrace = new StringWriter();
			e.printStackTrace(new PrintWriter(stackTrace));
			out.println(stackTrace.toString());
			
			throw e;
		}
	}
}