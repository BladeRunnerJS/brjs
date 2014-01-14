package com.caplin.cutlass.command.importing;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.base.AbstractCommandPlugin;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.util.FileUtility;

import org.bladerunnerjs.utility.NameValidator;

import com.caplin.cutlass.command.LegacyCommandPlugin;
import com.caplin.cutlass.structure.NamespaceCalculator;

public class ImportApplicationCommand extends AbstractCommandPlugin implements LegacyCommandPlugin
{
	private final File sdkBaseDir;
	private final int jettyPort;
	private ConsoleWriter out;
	
	public ImportApplicationCommand(BRJS brjs) throws ConfigException
	{
		this.sdkBaseDir = new File( brjs.root().dir(), CutlassConfig.SDK_DIR);
		this.jettyPort = brjs.bladerunnerConf().getJettyPort();
		out = brjs.getConsoleWriter();
		setBRJS(brjs);
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
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
	public String getCommandUsage()
	{
		return "<app-zip> <new-app-name> <new-app-namespace>";
	}

	@Override
	public String getCommandHelp() {
		return getCommandUsage();
	}
	
	@Override
	public void doCommand(String... args) throws CommandArgumentsException, CommandOperationException
	{
		assertValidArgs(args);
		
		String applicationZip = args[0];
		String newApplicationName = args[1];
		String newApplicationNamespace = args[2];
		
		try
		{
			File temporaryDirectoryForNewApplication = FileUtility.createTemporaryDirectory("tempApplicationDir");
			ImportApplicationCommandUtility importApplicationCommandUtility = new ImportApplicationCommandUtility();
			
			importApplicationCommandUtility.unzipApplicationToTemporaryDirectoryForNewApplication(applicationZip, sdkBaseDir, temporaryDirectoryForNewApplication);
			String currentApplicationName = importApplicationCommandUtility.getCurrentApplicationName(temporaryDirectoryForNewApplication);

			File temporaryApplicationDir = new File(temporaryDirectoryForNewApplication, currentApplicationName);
			
			importApplicationCommandUtility.copyCutlassSDKJavaLibsIntoApplicationWebInfDirectory(sdkBaseDir, temporaryApplicationDir);
			
			File newApplicationDirectory = importApplicationCommandUtility.createApplicationDirIfItDoesNotAlreadyExist(sdkBaseDir, newApplicationName);
			File currentApplicationDirectoryInTempDir = temporaryApplicationDir;
			FileUtility.copyDirectoryContents(currentApplicationDirectoryInTempDir, newApplicationDirectory);

			/* we cant use the directory locator here since the exploded zip isnt inside an sdk structure so they locator wont recognise it
			 *  - as it happens we know the app.conf is in the root of the app so we can just create a new file object relative to the app path
			 */
			File temporaryDirAppConf = new File(temporaryApplicationDir, CutlassConfig.APP_CONF_FILENAME);
			File newAppDirConf = new File(newApplicationDirectory, CutlassConfig.APP_CONF_FILENAME);
			FileUtils.copyFile(temporaryDirAppConf, newAppDirConf);
			
			String applicationNamespace = NamespaceCalculator.getAppNamespace(newApplicationDirectory);
			Renamer.renameApplication(newApplicationDirectory, applicationNamespace, newApplicationNamespace, currentApplicationName, newApplicationName);
			
			NamespaceCalculator.purgeCachedApplicationNamespaces();
			
			importApplicationCommandUtility.createAutoDeployFileForApp(newApplicationDirectory, jettyPort);
			
			out.println("Successfully imported '" + new File(applicationZip).getName() + "' as new application '" + newApplicationName + "'");
			out.println(" " + newApplicationDirectory.getAbsolutePath());
		}
		catch (CommandOperationException e)
		{
			throw e;
		}
		catch (Exception e)
		{
			throw new CommandOperationException("Failed to import application from zip '" + applicationZip + "'.", e);
		}
	}

	private void assertValidArgs(String[] args) throws CommandArgumentsException
	{
		if (args.length != 3)
		{
			if (args.length > 3)
			{
				throw new CommandArgumentsException("Too many arguments provided.", this);
			}
			throw new CommandArgumentsException("Not enough arguments provided.", this);
		}
		
		if (NameValidator.legacyIsValidDirectoryName(args[1]) == false)
		{
			throw new CommandArgumentsException(
					"The <new-app-name> parameter can only contain alphanumeric, - and _ characters.", this);
		}
		if (NameValidator.legacyIsValidPackageName(args[2]) == false)
		{
			throw new CommandArgumentsException(
					"The <namespace> parameter can only contain lower-case alphanumeric characters.", this);
		}
		if (NameValidator.legacyIsReservedNamespace(args[2]))
		{
			throw new CommandArgumentsException("Could not import application using reserved namespace '" + args[2] + "'\n"
														+ "  " + NameValidator.getReservedNamespaces(), this);
		}
	}
	
}
