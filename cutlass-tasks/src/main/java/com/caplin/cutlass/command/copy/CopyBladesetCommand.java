package com.caplin.cutlass.command.copy;

import java.io.IOException;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.NameValidator;

import com.caplin.cutlass.command.NodeImporter;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;

public class CopyBladesetCommand  extends ArgsParsingCommandPlugin
{
	private Logger logger;
	private BRJS brjs;
	
	public CopyBladesetCommand(BRJS brjs)
	{
		setBRJS(brjs);
	}
	
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
		
		// TODO: stop doing this when we have the new spec tests
		sourceBladesetName = sourceBladesetName.replaceAll("-bladeset$", "");
		targetBladesetName = targetBladesetName.replaceAll("-bladeset$", "");
		
		if(!NameValidator.legacyIsValidPackageName(targetBladesetName)) throw new CommandArgumentsException("The <target-bladeset-name> parameter can only contain lower-case alphanumeric characters.\n" + "  The first character must be a letter.", this);
		
		App sourceApp = brjs.app(sourceAppName);
		Bladeset sourceBladeset = sourceApp.bladeset(sourceBladesetName);
		App targetApp = brjs.app(targetAppName);
		Bladeset targetBladeset = targetApp.bladeset(targetBladesetName);
		
		if (!sourceApp.dirExists()) throw new CommandOperationException("The source application '" + sourceAppName + "' does not exist at location '" + sourceApp.dir().getPath() + "'");
		if (!sourceBladeset.dirExists()) throw new CommandOperationException("The source bladeset '" + sourceBladesetName + "' does not exist.");
		if (!targetApp.dirExists()) throw new CommandOperationException("The target application '" + targetAppName + "' does not exist at location '" + targetApp.dir().getPath() + "'");
		if (targetBladeset.dirExists()) throw new CommandOperationException("The target bladeset '" + targetBladeset.getName() + "' already exists inside application '" + targetAppName + "'.");
		
		try {
			NodeImporter.importBladeset(sourceBladeset.dir(), sourceApp.appConf().getRequirePrefix() + "/" + sourceBladesetName, targetBladeset);
			
//			FileUtils.copyDirectory(sourceBladeset.dir(), targetBladeset.dir());
//			Renamer.renameBladeset(targetBladeset.dir(), sourceApp.appConf().getRequirePrefix() + "." + sourceBladesetName, targetApp.appConf().getRequirePrefix() + "." + targetBladesetName);
			
			logger.println("Successfully copied " + sourceAppName + "/" + sourceBladesetName + " to " + targetAppName + "/" + targetBladesetName);
		}
		catch (IOException | ConfigException | InvalidSdkDirectoryException e) {
			throw new CommandOperationException(e);
		}
		
		return 0;
	}
}
