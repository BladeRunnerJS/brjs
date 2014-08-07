package org.bladerunnerjs.plugin.plugins.commands.standard;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.plugin.plugins.commands.standard.CreateBladeCommand.Messages;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.NameValidator;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class CopyThemeCommand extends ArgsParsingCommandPlugin
{
	public class Messages {
		public static final String APP_CREATED_CONSOLE_MSG = "Successfully created new app '%s'";
		public static final String APP_DEPLOYED_CONSOLE_MSG = "Successfully deployed '%s' app";
	}
	
	private BRJS brjs;
	private Logger logger;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("app-folder-path").setRequired(true).setHelp("required path leading to the app in which themes should be created. If path specifies a themes folder then the CSS theme is created only within that folder."));
		argsParser.registerParameter(new UnflaggedOption("copy-from-theme-name").setRequired(true).setHelp("the name of the application that will be created"));
		argsParser.registerParameter(new UnflaggedOption("copy-to-theme-name").setRequired(true).setHelp("the name of the application that will be created"));
		
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
		return "copy-theme";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Copy a CSS theme files into another CSS theme.";
	}
	
	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String topPath = parsedArgs.getString("app-folder-path");
		String origTheme = parsedArgs.getString("copy-from-theme-name");
		String newTheme = parsedArgs.getString("copy-to-theme-name");
		
		Path path = Paths.get(topPath);
		
		String appName = path.getName(0).toString();
		App app = brjs.app(appName);
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		/*
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
		
		logger.println(Messages.BLADE_CREATE_SUCCESS_CONSOLE_MSG, bladeName);
		logger.println(Messages.BLADE_PATH_CONSOLE_MSG, blade.dir().getPath());
		
		return 0;
		
		App app = brjs.app(appName);
		*/
		if(app.dirExists()) throw new NodeAlreadyExistsException(app, this);
		
		try {
			NameValidator.assertValidDirectoryName(app);
			/*requirePrefix = (requirePrefix == null) ? NameValidator.generateRequirePrefixFromApp(app) : requirePrefix;
			
			app.populate(requirePrefix);*/
			logger.println(Messages.APP_CREATED_CONSOLE_MSG, appName);
			logger.println(" " + app.dir().getPath());
			
			app.deploy();
			logger.println(Messages.APP_DEPLOYED_CONSOLE_MSG, appName);
		}
		catch(InvalidNameException e) {
			throw new CommandArgumentsException(e, this);
		}
		catch(TemplateInstallationException e) {
			throw new CommandOperationException("Cannot create application '" + app.dir().getPath() + "'", e);
		}
		return 0;
	}
}
