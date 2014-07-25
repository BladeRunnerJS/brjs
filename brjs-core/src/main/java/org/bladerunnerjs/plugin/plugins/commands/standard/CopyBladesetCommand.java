package org.bladerunnerjs.plugin.plugins.commands.standard;

import java.io.IOException;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.NodeImporter;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.exception.name.InvalidPackageNameException;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.NameValidator;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;

public class CopyBladesetCommand  extends ArgsParsingCommandPlugin
{
	private Logger logger;
	private BRJS brjs;
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
		this.logger = brjs.logger(this.getClass());
	}
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("source-app-name").setRequired(true).setHelp("the application from which the bladeset will be copied"));
		argsParser.registerParameter(new UnflaggedOption("source-bladeset-name").setRequired(true).setHelp("the name of the bladeset to be copied"));
		argsParser.registerParameter(new UnflaggedOption("target-app-name").setRequired(true).setHelp("the application to which the bladeset will be copied"));
		argsParser.registerParameter(new UnflaggedOption("target-bladeset-name").setRequired(false).setHelp("the name the bladeset will have within the target application"));
	}
	
	@Override
	public String getCommandName()
	{
		return "copy-bladeset";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Copy a bladeset from one application to another.";
	}
	
	@Override
	public String getCommandHelp() {
		return getCommandUsage();
	}
	
	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String sourceAppName = parsedArgs.getString("source-app-name");
		String sourceBladesetName = parsedArgs.getString("source-bladeset-name");
		String targetAppName = parsedArgs.getString("target-app-name");
		String targetBladesetName = (parsedArgs.getString("target-bladeset-name") == null) ? sourceBladesetName : parsedArgs.getString("target-bladeset-name");
		
		App sourceApp = brjs.app(sourceAppName);
		Bladeset sourceBladeset = sourceApp.bladeset(sourceBladesetName);
		App targetApp = brjs.app(targetAppName);
		
		try {
			NameValidator.assertValidPackageName(targetApp, targetBladesetName);
		}
		catch (InvalidPackageNameException e) {
			throw new CommandArgumentsException(e, this);
		}
		
		Bladeset targetBladeset = targetApp.bladeset(targetBladesetName);
		
		if (!sourceApp.dirExists()) throw new NodeDoesNotExistException(sourceApp, this);
		if (!sourceBladeset.dirExists()) throw new NodeDoesNotExistException(sourceBladeset, this);
		if (!targetApp.dirExists()) throw new NodeDoesNotExistException(targetApp, this);
		if (targetBladeset.dirExists()) throw new NodeAlreadyExistsException(targetBladeset, this);
		
		try {
			NodeImporter.importBladeset(sourceBladeset.dir(), sourceApp.appConf().getRequirePrefix(), sourceApp.appConf().getRequirePrefix() + "/" + sourceBladesetName, targetBladeset);
			
			logger.println("Successfully copied " + sourceAppName + "/" + sourceBladesetName + " to " + targetAppName + "/" + targetBladesetName);
		}
		catch (IOException | ConfigException | InvalidSdkDirectoryException e) {
			throw new CommandOperationException(e);
		}
		
		return 0;
	}
}
