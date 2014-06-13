package org.bladerunnerjs.runner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.naming.InvalidNameException;

import org.apache.commons.lang3.ArrayUtils;
import org.bladerunnerjs.logger.RootConsoleLogger;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.logger.LogLevel;
import org.bladerunnerjs.logging.ConsoleLoggerConfigurator;
import org.bladerunnerjs.logging.LogConfiguration;
import org.bladerunnerjs.model.engine.AbstractRootNode;
//import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.utility.command.CommandList;
import org.slf4j.impl.StaticLoggerBinder;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.command.check.CheckCommand;
import com.caplin.cutlass.command.copy.CopyBladesetCommand;
//import com.caplin.cutlass.command.importing.ImportApplicationCommand;
import com.caplin.cutlass.command.test.TestCommand;
import com.caplin.cutlass.command.test.TestServerCommand;
import com.caplin.cutlass.command.testIntegration.TestIntegrationCommand;

public class CommandRunner {

	public static void main(String[] args) {
		int exitCode = -1;
		try 
		{
			exitCode = new CommandRunner().run(args);
		}
		catch (CommandArgumentsException e) 
		{
			System.err.println(e.getMessage());
		}
		catch (Exception ex) 
		{
			System.err.println(formatException(ex));
		}
		finally
		{
			System.exit(exitCode);
		}
	}
	
	private static String formatException(Exception e) {
		ByteArrayOutputStream byteStreamOutputStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(byteStreamOutputStream);
		e.printStackTrace(printStream);
		
		return byteStreamOutputStream.toString().trim();
	}
	
	public int run(String[] args) throws CommandArgumentsException, CommandOperationException, InvalidNameException, ModelUpdateException {
		AbstractRootNode.allowInvalidRootDirectories = false;
		BRJS brjs = null;
		
		try {
			if (args.length < 1 || args[0] == null) throw new NoSdkArgumentException("No SDK base directory was provided");
			
			File sdkBaseDir = new File(args[0]);
			args = ArrayUtils.subarray(args, 1, args.length);
			
			if (!sdkBaseDir.exists() || !sdkBaseDir.isDirectory()) throw new InvalidDirectoryException("'" + sdkBaseDir.getPath() + "' is not a directory");
			sdkBaseDir = sdkBaseDir.getCanonicalFile();
			
			args = processGlobalCommandFlags(args);
			
			try {
				brjs = BRJSAccessor.initialize(new BRJS(sdkBaseDir, new ConsoleLoggerConfigurator(getRootLogger())));
			}
			catch(InvalidSdkDirectoryException e) {
				throw new CommandOperationException(e);
			}
			
			brjs.populate();
			
			injectLegacyCommands(brjs);
			return brjs.runUserCommand(new CommandConsoleLogLevelAccessor(getRootLogger()), args);
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			AbstractRootNode.allowInvalidRootDirectories = true;
			
			if(brjs != null) {
				brjs.close();
			}
		}
	}
	
	private String[] processGlobalCommandFlags(String[] args) {
		if (args.length > 0) {
			String lastArg = args[args.length - 1];
			
			if(lastArg.equals("--quiet") || lastArg.equals("--verbose") || lastArg.equals("--debug")) {
				args = ArrayUtils.subarray(args, 0, args.length - 1);
				setExplicitLogLevel(lastArg);
			}
			else {
				setDefaultLogLevel();
			}
		}
		else {
			setDefaultLogLevel();
		}
		
		return args;
	}
	
	private void setExplicitLogLevel(String levelFlag) {
		RootConsoleLogger rootLogger = getRootLogger();
		LogLevel logLevel = (levelFlag.equals("--quiet")) ? LogLevel.WARN : LogLevel.DEBUG;
		rootLogger.setLogLevel(logLevel);
		
		if(levelFlag.equals("--debug")) {
			rootLogger.setDebugMode(true);
		}
	}
	
	private void setDefaultLogLevel() {
		LogConfiguration logConfigurator = new ConsoleLoggerConfigurator(getRootLogger());
		logConfigurator.ammendProfile(LogLevel.INFO)
			.pkg("org.hibernate").logsAt(LogLevel.WARN); // TODO: this is a plugin concern, so should be handled within the model
		logConfigurator.setLogLevel(LogLevel.INFO);
	}
	
	private void injectLegacyCommands(BRJS brjs) {
//		try {
			CommandList commandList = brjs.plugins().commandList();
			commandList.addCommand(new CheckCommand());
			commandList.addCommand(new CopyBladesetCommand( brjs.root().dir() ));
//			commandList.addCommand(new ImportApplicationCommand( brjs ));
			commandList.addCommand(new TestCommand());
			commandList.addCommand(new TestServerCommand());
			commandList.addCommand(new TestIntegrationCommand( brjs.root().dir() ));
//		}
//		catch(ConfigException e) {
//			throw new RuntimeException(e);
//		}
	}
	
	private RootConsoleLogger getRootLogger() {
		return StaticLoggerBinder.getSingleton().getLoggerFactory().getRootLogger();
	}
	
	class NoSdkArgumentException extends CommandOperationException {
		private static final long serialVersionUID = 1L;
		
		public NoSdkArgumentException(String msg) {
			super(msg);
		}
	}
	
	class InvalidDirectoryException extends CommandOperationException {
		private static final long serialVersionUID = 1L;
		
		public InvalidDirectoryException(String msg) {
			super(msg);
		}
	}
}
