package org.bladerunnerjs.core.plugin.command.standard;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.core.console.ConsoleWriter;
import org.bladerunnerjs.core.plugin.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class CreateBladesetCommand extends ArgsParsingCommandPlugin
{
	public class Messages {
		public static final String BLADESET_CREATE_SUCCESS_CONSOLE_MSG = "Successfully created new bladeset '%s'";
		public static final String BLADESET_PATH_CONSOLE_MSG = "  %s";
	}
	
	private ConsoleWriter out;
	private BRJS brjs;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("target-app-name").setRequired(true).setHelp("the application within which the new bladeset will be created"));
		argsParser.registerParameter(new UnflaggedOption("new-bladeset-name").setRequired(true).setHelp("the name of the bladeset that will be created"));
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
		return "create-bladeset";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Create a new empty bladeset within a given application.";
	}

	@Override
	protected void doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString("target-app-name");
		String bladesetName = parsedArgs.getString("new-bladeset-name");
		
		App app = brjs.app(appName);
		Bladeset bladeset = app.bladeset(bladesetName);
		
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		if(bladeset.dirExists()) throw new NodeAlreadyExistsException(bladeset, this);
		
		try {
			bladeset.populate();
		}
		catch(InvalidNameException e) {
			throw new CommandArgumentsException(e, this);
		}
		catch(ModelUpdateException e) {
			throw new CommandOperationException("Cannot create bladeset '" + bladeset.dir().getPath() + "'", e);
		}
		
		out.println(Messages.BLADESET_CREATE_SUCCESS_CONSOLE_MSG, bladesetName);
		out.println(Messages.BLADESET_PATH_CONSOLE_MSG, bladeset.dir().getPath());
	}
}
