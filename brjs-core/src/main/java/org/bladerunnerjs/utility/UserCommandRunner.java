package org.bladerunnerjs.utility;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.LogLevelAccessor;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NoSuchCommandException;
import org.bladerunnerjs.plugin.plugins.commands.core.HelpCommand;
import org.bladerunnerjs.plugin.utility.command.CommandList;

public class UserCommandRunner {
	
	public class Messages {
		public static final String OUTDATED_JAR_MESSAGE = "The app '%s' is either missing BRJS jar(s), contains BRJS jar(s) it shouldn't or the BRJS jar(s) are outdated."+
				" You should delete all jars prefixed with '%s' in the WEB-INF/lib directory and copy in all jars contained in %s.";
	}
	
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
	
	
	private static void checkApplicationLibVersions(BRJS brjs, Logger logger)
	{
		for (App app : brjs.userApps()) {
			checkApplicationLibVersions(app, logger);
		}
	}
	
	private static void checkApplicationLibVersions(App app, Logger logger)
	{
		File webinfLib = app.file("WEB-INF/lib");
		MemoizedFile appJarsDir = app.root().appJars().dir();
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
			logger.warn( Messages.OUTDATED_JAR_MESSAGE, app.getName(), "brjs-", app.root().dir().getRelativePath(appJarsDir) );
		}
	}
	
}