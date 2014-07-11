package org.bladerunnerjs.runner;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.naming.InvalidNameException;

import org.apache.commons.lang3.ArrayUtils;
import org.bladerunnerjs.logger.ConsoleLogger;
import org.bladerunnerjs.logger.ConsoleLoggerStore;
import org.bladerunnerjs.logger.LogLevel;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;
import org.bladerunnerjs.model.engine.AbstractRootNode;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.slf4j.impl.StaticLoggerBinder;

import com.caplin.cutlass.command.test.TestCommand;
import com.caplin.cutlass.command.test.TestServerCommand;
import com.caplin.cutlass.command.testIntegration.TestIntegrationCommand;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

public class CommandRunner {
	private static final JSAP argsParser = new JSAP();
	
	static {
		try {
			argsParser.registerParameter(new Switch("info").setShortFlag('i').setLongFlag("info").setDefault("false").setHelp("info level logging"));
			argsParser.registerParameter(new Switch("debug").setShortFlag('d').setLongFlag("debug").setDefault("false").setHelp("debug level logging"));
			argsParser.registerParameter(new FlaggedOption("pkg").setLongFlag("pkg").setHelp("the comma delimited list of packages to show messages from, or '"+
					ConsoleLogger.LOG_ALL_PACKAGES_PACKAGE_NAME+"' to show everything"));
			argsParser.registerParameter(new Switch("show-pkg").setLongFlag("show-pkg").setDefault("false").setHelp("show which class each log line comes from"));
		}
		catch (JSAPException e) {
			throw new RuntimeException(e);
		}
	}
	
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
				brjs = ThreadSafeStaticBRJSAccessor.initializeModel(sdkBaseDir);
			}
			catch(InvalidSdkDirectoryException e) {
				throw new CommandOperationException(e);
			}
			
			brjs.populate();
			
			injectLegacyCommands(brjs);
			return brjs.runUserCommand(new CommandConsoleLogLevelAccessor(getLoggerStore()), args);
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
		JSAPResult parsedArgs;
		int i = 0;
		while(i < args.length) {
			parsedArgs = argsParser.parse(Arrays.copyOfRange(args, i, args.length));
			
			if(parsedArgs.success()) {
				args = Arrays.copyOfRange(args, 0, i);
				processedParsedArgs(parsedArgs);
				break;
			}
			++i;
		}
		
		return args;
	}
	
	private void processedParsedArgs(JSAPResult parsedArgs) {
		boolean isInfo = parsedArgs.getBoolean("info");
		boolean isDebug = parsedArgs.getBoolean("debug");
		List<String> whitelistedPackages = (parsedArgs.getString("pkg") != null) ? Arrays.asList(parsedArgs.getString("pkg").split("\\s*,\\s*")) : new ArrayList<String>();
		boolean logClassNames = parsedArgs.getBoolean("show-pkg");
		
		if(isDebug) {
			getLoggerStore().setLogLevel(LogLevel.DEBUG);
		}
		else if(isInfo) {
			getLoggerStore().setLogLevel(LogLevel.INFO);
		}
		
		if(logClassNames) {
			getLoggerStore().setLogClassNames(true);
		}
		
		getLoggerStore().setWhitelistedPackages(whitelistedPackages);
	}

	private void injectLegacyCommands(BRJS brjs) {
		brjs.plugins().addCommandPlugin(new TestCommand());
		brjs.plugins().addCommandPlugin(new TestServerCommand());
		brjs.plugins().addCommandPlugin(new TestIntegrationCommand( brjs.root().dir() ));
	}
	
	private ConsoleLoggerStore getLoggerStore() {
		return StaticLoggerBinder.getSingleton().getLoggerFactory();
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
