package org.bladerunnerjs.plugin.plugins.commands.standard;

import java.io.File;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.NodeImporter;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.model.exception.name.InvalidRootPackageNameException;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.NameValidator;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;

public class ImportAppCommand extends ArgsParsingCommandPlugin
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
	public String getCommandName()
	{
		return "import-app";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Create a new application by importing a given zipped application.";
	}
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("app-zip").setRequired(true).setHelp("the path to the zip containing the application"));
		argsParser.registerParameter(new UnflaggedOption("new-app-name").setRequired(true).setHelp("the name of the newly imported app"));
		argsParser.registerParameter(new UnflaggedOption("new-app-require-prefix").setRequired(true).setHelp("the require-prefix that the new app will have")); // TODO: switch to <new-app-require-path>
	}
	
	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appZipName = parsedArgs.getString("app-zip");
		String newAppName = parsedArgs.getString("new-app-name");
		String newAppNamespace = parsedArgs.getString("new-app-require-prefix");
		
		File appZip = (new File(appZipName).exists()) ? new File(appZipName) : new File(brjs.file("sdk"), appZipName);
		App app = brjs.app(newAppName);
		
		if(!appZip.exists()) throw new CommandArgumentsException("Couldn't find zip file at '" + appZipName + "'.", this);
		if(app.dirExists()) throw new NodeAlreadyExistsException(app, this);
		
		try
		{
			NameValidator.assertValidDirectoryName(app);
			NameValidator.assertValidRootPackageName(app, newAppNamespace);
			
			File tempDir = FileUtility.createTemporaryDirectory("import-app-command");
			FileUtility.unzip(new ZipFile(appZip), tempDir);
			
			String unzippedAppName = getUnzippedAppName(tempDir);
			File unzippedAppDir = new File(tempDir, unzippedAppName);
			File unzippedLibDir = new File(unzippedAppDir, "/WEB-INF/lib");
			
			FileUtils.copyDirectory(brjs.appJars().dir(), unzippedLibDir);
			
			String unzippedAppRequirePrefix = (new AppConf(brjs, new File(unzippedAppDir, "app.conf"))).getRequirePrefix();
			NodeImporter.importApp(unzippedAppDir, unzippedAppRequirePrefix, app, newAppNamespace);
			
			app.deploy();
			
			logger.println("Successfully imported '" + new File(appZipName).getName() + "' as new application '" + newAppName + "'");
			logger.println(" " + app.dir().getAbsolutePath());
		}
		catch (CommandOperationException e)
		{
			throw e;
		}
		catch(InvalidDirectoryNameException | InvalidRootPackageNameException e) {
			throw new CommandArgumentsException("Failed to import application from zip '" + appZipName + "'.", e, this);
		}
		catch (Exception e)
		{
			throw new CommandOperationException("Failed to import application from zip '" + appZipName + "'.", e);
		}
		
		return 0;
	}
	
	private String getUnzippedAppName(File temporaryDirectoryForNewApplication) throws CommandOperationException {
		String[] applicationsUnzippedIntoTemporaryDirectory = temporaryDirectoryForNewApplication.list();
		
		if(applicationsUnzippedIntoTemporaryDirectory.length > 1)
		{
			throw new CommandOperationException("More than one folder at root of application zip.");
		}
		
		return applicationsUnzippedIntoTemporaryDirectory[0];
	}
}
