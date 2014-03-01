package org.bladerunnerjs.plugin.plugins.commands.standard;

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
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class CreateBladeCommand extends ArgsParsingCommandPlugin
{
	private class Parameters {
		public static final String APP_NAME = "target-app-name";
		public static final String BLADESET_NAME = "target-bladeset-name";
		public static final String BLADE_NAME = "new-blade-name";
	}
	
	public class Messages {
		public static final String BLADE_CREATE_SUCCESS_CONSOLE_MSG = "Successfully created new blade '%s'";
		public static final String BLADE_PATH_CONSOLE_MSG = "  %s";
	}
	
	private BRJS brjs;
	private ConsoleWriter out;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption(Parameters.APP_NAME).setRequired(true).setHelp("the application within which the new blade will be created"));
		argsParser.registerParameter(new UnflaggedOption(Parameters.BLADESET_NAME).setRequired(true).setHelp("the bladeset within which the new blade will be created"));
		argsParser.registerParameter(new UnflaggedOption(Parameters.BLADE_NAME).setRequired(true).setHelp("the name of the blade that will be created"));
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
		String appName = parsedArgs.getString(Parameters.APP_NAME);
		String bladesetName = parsedArgs.getString(Parameters.BLADESET_NAME);
		String bladeName = parsedArgs.getString(Parameters.BLADE_NAME);
		
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
