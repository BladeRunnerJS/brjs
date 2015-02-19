package org.bladerunnerjs.plugin.commands.standard;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.api.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.api.plugin.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.TemplateUtility;

import com.martiansoftware.jsap.FlaggedOption;
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
	private Logger logger;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption(Parameters.APP_NAME).setRequired(true).setHelp("the application within which the new blade will be created"));
		argsParser.registerParameter(new UnflaggedOption(Parameters.BLADESET_NAME).setRequired(true).setHelp("the bladeset within which the new blade will be created"));
		argsParser.registerParameter(new UnflaggedOption(Parameters.BLADE_NAME).setRequired(true).setHelp("the name of the blade that will be created"));	
		argsParser.registerParameter(new FlaggedOption("template-group").setShortFlag('T').setLongFlag("template").setDefault("default").setRequired(false).setHelp("the user-defined template that will be used"));
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
		this.logger = brjs.logger(this.getClass());
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
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString(Parameters.APP_NAME);
		String bladesetName = parsedArgs.getString(Parameters.BLADESET_NAME);
		String bladeName = parsedArgs.getString(Parameters.BLADE_NAME);
		String templateGroup = parsedArgs.getString("template-group");
		
		App app = brjs.app(appName);
		Bladeset bladeset = getBladeset(app, bladesetName);
		Blade blade = bladeset.blade(bladeName);
		
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		if(!bladeset.dirExists()) throw new NodeDoesNotExistException(bladeset, this);
		if(blade.dirExists()) throw new NodeAlreadyExistsException(blade, this);
		
		if (TemplateUtility.templateExists(brjs, blade, templateGroup, this)) {
			try {
				blade.populate(templateGroup);
			}
			catch(InvalidNameException e) {
				throw new CommandArgumentsException(e, this);
			}
			catch(ModelUpdateException e) {
				throw new CommandOperationException("Cannot create blade '" + blade.dir().getPath() + "'", e);
			}
			catch(TemplateInstallationException e) {
				throw new CommandArgumentsException(e, this);
			}
			logger.println(Messages.BLADE_CREATE_SUCCESS_CONSOLE_MSG, bladeName);
			logger.println(Messages.BLADE_PATH_CONSOLE_MSG, blade.dir().getPath());
		}
		return 0;
	}
	
	private Bladeset getBladeset(App app, String bladesetName) throws CommandOperationException
	{
		Bladeset bladeset = (bladesetName.equals(App.DEFAULT_CONTAINER_NAME)) ? app.defaultBladeset() : app.bladeset(bladesetName);
		if (bladeset == app.defaultBladeset()) {
    		try
    		{
    			if (!bladeset.dirExists()) {
    				bladeset.create();
    			}
    		}
    		catch (InvalidNameException | ModelUpdateException e)
    		{
    			throw new CommandOperationException(e);
    		}
		}
		return bladeset;
	}

}
