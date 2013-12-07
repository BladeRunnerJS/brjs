package org.bladerunnerjs.plugin.command.standard;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.command.ArgsParsingCommandPlugin;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class CreateBladeCommand extends ArgsParsingCommandPlugin
{
	public class Messages {
		public static final String BLADE_CREATE_SUCCESS_CONSOLE_MSG = "Successfully created new blade '%s'";
		public static final String BLADE_PATH_CONSOLE_MSG = "  %s";
	}
	
	private BRJS brjs;
	private ConsoleWriter out;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("target-app-name").setRequired(true).setHelp("the application within which the new blade will be created"));
		argsParser.registerParameter(new UnflaggedOption("target-bladeset-name").setRequired(true).setHelp("the bladeset within which the new blade will be created"));
		argsParser.registerParameter(new UnflaggedOption("new-blade-name").setRequired(true).setHelp("the name of the blade that will be created"));
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
		return"create-blade";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Create a new empty blade within a given application bladeset.";
	}
	
	@Override
	protected void doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString("target-app-name");
		String bladesetName = parsedArgs.getString("target-bladeset-name");
		String bladeName = parsedArgs.getString("new-blade-name");
		
		App app = brjs.app(appName);
		Bladeset bladeset = app.bladeset(bladesetName);
		Blade blade = bladeset.blade(bladeName);
		
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		if(!bladeset.dirExists()) throw new NodeDoesNotExistException(bladeset, this);
		if(blade.dirExists()) throw new NodeAlreadyExistsException(blade, this);
		
		try {
			blade.populate();
		}
		catch(InvalidNameException e) {
			throw new CommandArgumentsException(e, this);
		}
		catch(ModelUpdateException e) {
			throw new CommandOperationException("Cannot create blade '" + blade.dir().getPath() + "'", e);
		}
		
		out.println(Messages.BLADE_CREATE_SUCCESS_CONSOLE_MSG, bladeName);
		out.println(Messages.BLADE_PATH_CONSOLE_MSG, blade.dir().getPath());
	}
}
