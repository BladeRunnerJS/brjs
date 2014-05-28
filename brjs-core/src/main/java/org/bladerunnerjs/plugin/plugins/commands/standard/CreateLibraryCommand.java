package org.bladerunnerjs.plugin.plugins.commands.standard;

import javax.naming.InvalidNameException;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class CreateLibraryCommand extends ArgsParsingCommandPlugin
{
	public static enum SupportedLibraryType {
		br,
		thirdparty;
	};
	
	public class Messages {
		public static final String LIBRARY_CREATE_SUCCESS_CONSOLE_MSG = "Successfully created new library '%s'";
		public static final String LIBRARY_PATH_CONSOLE_MSG = "  %s";
		public static final String INVALID_LIB_TYPE_MESSAGE = "Unsupported library type '%s'. Valid types are: %s";
	}
	
	private static final String NEW_LIBRARY_NAME = "new-library-name";
	private static final String TARGET_APP_NAME = "target-app-name";
	private static final String LIBRARY_TYPE = "type";
	
	private BRJS brjs;
	private ConsoleWriter out;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption(TARGET_APP_NAME).setRequired(true).setHelp("the application the new library will be created within"));
		argsParser.registerParameter(new UnflaggedOption(NEW_LIBRARY_NAME).setRequired(true).setHelp("the name of the library that will be created"));
		argsParser.registerParameter(new FlaggedOption(LIBRARY_TYPE).setShortFlag('t').setLongFlag("type").setDefault( SupportedLibraryType.br.toString() ));
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{	
		this.brjs = brjs;
		out = brjs.getConsoleWriter();
	}
	
	@Override
	public String getCommandName()
	{
		return "create-library";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Create a new empty library within a given application.";
	}
	
	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString(TARGET_APP_NAME);
		String libraryName = parsedArgs.getString(NEW_LIBRARY_NAME);
		String libraryType = parsedArgs.getString(LIBRARY_TYPE);
		
		App app = brjs.app(appName);
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		
		JsLib library = null;
		
		SupportedLibraryType createLibraryType;
		try {
			createLibraryType = SupportedLibraryType.valueOf(libraryType);
		} catch (IllegalArgumentException ex) {
			String exceptionMsg = String.format(Messages.INVALID_LIB_TYPE_MESSAGE, libraryType, StringUtils.join(SupportedLibraryType.values(), ", ") );
			throw new CommandArgumentsException(exceptionMsg, this);
		}
		
		
		switch ( createLibraryType ) {
			case br:
				library = app.jsLib(libraryName);
				break;
			case thirdparty:
				library = app.nonBladeRunnerLib(libraryName);
				break;
		}
		
		if (library.dirExists()) throw new NodeAlreadyExistsException(library, this);
		try {
			library.populate();
		}
		catch(InvalidNameException e) {
			throw new CommandArgumentsException(e, this);
		}
		catch(ModelUpdateException e) {
			throw new CommandOperationException("Cannot create library '" + library.dir().getPath() + "'", e);
		}
		
		out.println(Messages.LIBRARY_CREATE_SUCCESS_CONSOLE_MSG, libraryName);
		out.println(Messages.LIBRARY_PATH_CONSOLE_MSG, library.dir().getPath());
		
		return 0;
	}
}
