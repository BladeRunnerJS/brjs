package org.bladerunnerjs.plugin.plugins.commands.standard;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
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


public class CreateAspectCommand extends ArgsParsingCommandPlugin
{
	public class Messages {
		public static final String ASPECT_CREATE_SUCCESS_CONSOLE_MSG = "Successfully created new aspect '%s'";
		public static final String ASPECT_PATH_CONSOLE_MSG = "  %s";
	}
	
	private BRJS brjs;
	private Logger logger;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("target-app-name").setRequired(true).setHelp("the application within which the new aspect will be created"));
		argsParser.registerParameter(new UnflaggedOption("new-aspect-name").setRequired(true).setHelp("the name of the aspect that will be created"));
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
		return "create-aspect";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Create a new aspect for an application.";
	}

	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString("target-app-name");
		String aspectName = parsedArgs.getString("new-aspect-name");
		
		App app = brjs.app(appName);
		Aspect aspect = app.aspect(aspectName);
		
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		if(aspect.dirExists()) throw new NodeAlreadyExistsException(aspect, this);
		
		try {
			aspect.populate();
		}
		catch(InvalidNameException e) {
			throw new CommandArgumentsException(e, this);
		}
		catch(ModelUpdateException e) {
			throw new CommandOperationException("Cannot create aspect '" + aspect.dir().getPath() + "'", e);
		}
		
		logger.println(Messages.ASPECT_CREATE_SUCCESS_CONSOLE_MSG, aspectName);
		logger.println(Messages.ASPECT_PATH_CONSOLE_MSG, aspect.dir().getPath());
		
		return 0;
	}
}
