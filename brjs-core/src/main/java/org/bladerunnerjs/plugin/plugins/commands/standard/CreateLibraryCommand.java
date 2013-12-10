package org.bladerunnerjs.plugin.plugins.commands.standard;

import javax.naming.InvalidNameException;

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

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class CreateLibraryCommand extends ArgsParsingCommandPlugin
{
	public class Messages {
		public static final String LIBRARY_CREATE_SUCCESS_CONSOLE_MSG = "Successfully created new library '%s'";
		public static final String LIBRARY_PATH_CONSOLE_MSG = "  %s";
	}
	
	private BRJS brjs;
	private ConsoleWriter out;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("target-app-name").setRequired(true).setHelp("the application the new library will be created within"));
		argsParser.registerParameter(new UnflaggedOption("new-library-name").setRequired(true).setHelp("the name of the library that will be created"));
		argsParser.registerParameter(new UnflaggedOption("library-namespace").setRequired(true).setHelp("the top-level namespace that all source code will reside within"));
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
	protected void doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString("target-app-name");
		String libraryName = parsedArgs.getString("new-library-name");
		String libraryNamespace = parsedArgs.getString("library-namespace");
		App app = brjs.app(appName);
		JsLib library = app.jsLib(libraryName);
		
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		if(library.dirExists()) throw new NodeAlreadyExistsException(library, this);
		
		try {
    		library.populate(libraryNamespace);
		}
		catch(InvalidNameException e) {
			throw new CommandArgumentsException(e, this);
		}
		catch(ModelUpdateException e) {
			throw new CommandOperationException("Cannot create library '" + library.dir().getPath() + "'", e);
		}
		
		out.println(Messages.LIBRARY_CREATE_SUCCESS_CONSOLE_MSG, libraryName);
		out.println(Messages.LIBRARY_PATH_CONSOLE_MSG, library.dir().getAbsolutePath());
	}
}
