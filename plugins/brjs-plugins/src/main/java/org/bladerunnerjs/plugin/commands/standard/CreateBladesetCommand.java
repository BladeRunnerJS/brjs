package org.bladerunnerjs.plugin.commands.standard;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
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


public class CreateBladesetCommand extends ArgsParsingCommandPlugin
{
	public class Messages {
		public static final String BLADESET_CREATE_SUCCESS_CONSOLE_MSG = "Successfully created new bladeset '%s'";
		public static final String BLADESET_PATH_CONSOLE_MSG = "  %s";
	}
	
	private BRJS brjs;
	private Logger logger;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("target-app-name").setRequired(true).setHelp("the application within which the new bladeset will be created"));
		argsParser.registerParameter(new UnflaggedOption("new-bladeset-name").setRequired(true).setHelp("the name of the bladeset that will be created"));
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
		return "create-bladeset";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Create a new empty bladeset within a given application.";
	}

	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString("target-app-name");
		String bladesetName = parsedArgs.getString("new-bladeset-name");
		String templateGroup = parsedArgs.getString("template-group");
		
		App app = brjs.app(appName);
		Bladeset bladeset = app.bladeset(bladesetName);
		
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		if(bladeset.dirExists()) throw new NodeAlreadyExistsException(bladeset, this);
		
		if (TemplateUtility.templateExists(brjs, bladeset, templateGroup, this)) {
			try {
				bladeset.populate(templateGroup);
			}
			catch(InvalidNameException e) {
				throw new CommandArgumentsException(e, this);
			}
			catch(ModelUpdateException e) {
				throw new CommandOperationException("Cannot create bladeset '" + bladeset.dir().getPath() + "'", e);
			}
			catch (TemplateInstallationException e) {
				throw new CommandOperationException(e);
			}
			logger.println(Messages.BLADESET_CREATE_SUCCESS_CONSOLE_MSG, bladesetName);
			logger.println(Messages.BLADESET_PATH_CONSOLE_MSG, bladeset.dir().getPath());
		}
		return 0;
	}
}
