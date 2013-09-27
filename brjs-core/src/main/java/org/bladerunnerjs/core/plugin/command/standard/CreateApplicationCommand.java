package org.bladerunnerjs.core.plugin.command.standard;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.core.console.ConsoleWriter;
import org.bladerunnerjs.core.plugin.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class CreateApplicationCommand extends ArgsParsingCommandPlugin
{
	public class Messages {
		public static final String APP_CREATED_CONSOLE_MSG = "Successfully created new app '%s'";
		public static final String APP_DEPLOYED_CONSOLE_MSG = "Successfully deployed '%s' app";
	}
	
	private BRJS brjs;
	private ConsoleWriter out;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("new-app-name").setRequired(true).setHelp("the name of the application that will be created"));
		argsParser.registerParameter(new UnflaggedOption("app-namespace").setRequired(true).setHelp("the top-level namespace that all source code will reside within"));
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
		return "create-app";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Create a new application.";
	}
	
	@Override
	protected void doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString("new-app-name");
		String appNamespace = parsedArgs.getString("app-namespace");
		App app = brjs.app(appName);
		
		if(app.dirExists()) throw new NodeAlreadyExistsException(app, this);
		
		try {
			app.populate(appNamespace);
			out.println(Messages.APP_CREATED_CONSOLE_MSG, appName);
			out.println(" " + app.dir().getPath());
			
			app.deploy();
			out.println(Messages.APP_DEPLOYED_CONSOLE_MSG, appName);
		}
		catch(InvalidNameException e) {
			throw new CommandArgumentsException(e, this);
		}
		catch(ModelUpdateException | TemplateInstallationException e) {
			throw new CommandOperationException("Cannot create application '" + app.dir().getPath() + "'", e);
		}
	}
}
