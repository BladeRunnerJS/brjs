package com.caplin.cutlass.command.copy;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import org.bladerunnerjs.model.StaticModelAccessor;

import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.base.AbstractCommandPlugin;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.command.LegacyCommandPlugin;
import com.caplin.cutlass.command.importing.Renamer;
import com.caplin.cutlass.util.FileUtility;
import com.caplin.cutlass.structure.AppStructureVerifier;
import com.caplin.cutlass.structure.RequirePrefixCalculator;

import org.bladerunnerjs.utility.NameValidator;

public class CopyBladesetCommand extends AbstractCommandPlugin implements LegacyCommandPlugin
{
	private final File sdkBaseDir;
	private ConsoleWriter out;
	
	public CopyBladesetCommand(File sdkBaseDir)
	{
		this.sdkBaseDir = sdkBaseDir;
		out = StaticModelAccessor.root.getConsoleWriter();
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
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
	public String getCommandUsage()
	{
		return "<source-app-name> <source-bladeset-name> <target-app-name> [<target-bladeset-name>]\n" +
			"Appending '-bladeset' to the bladeset parameters is not mandatory.";
	}

	@Override
	public String getCommandHelp() {
		return getCommandUsage();
	}
	
	@Override
	public int doCommand(String... args) throws CommandArgumentsException, CommandOperationException
	{
		//<source-app-name> <source-bladeset-name> <target-app-name> [<target-bladeset-name>]
		assertValidArgs(args);
		

		String sourceApplicationName = args[0];
		String sourceBladesetName = AppStructureVerifier.chompBladesetFromString(args[1]);
		String targetApplicationName = args[2];
		String targetBladesetName = getTargetBladesetName(args);


		//Check that the specified bladeset name doesn't exist in the specified app-name (fail if that's the case).
		//Create a directory under <app-name> for the specified blade set. 
		File targetBladesetDirectory = getTargetBladesetDirectory(targetApplicationName, targetBladesetName);
		File sourceBladesetDirectory = getSourceBladesetDirectory(sourceApplicationName, sourceBladesetName);

		try
		{
			//Copy over the bladeset into the newly created directory.
			FileUtility.copyDirectoryContents(sourceBladesetDirectory, targetBladesetDirectory);
		}
		catch (Exception e)
		{
			throw new CommandOperationException(e);
		}

		//Renamespace the bladeset to use the new [<target-bladeset-name>] if [<target-bladeset-name>] is passed in.
		File applicationsDir = new File(sdkBaseDir.getParent(), CutlassConfig.APPLICATIONS_DIR);
		String sourceApplicationNamespace = "";
		String targetApplicationNamespace = "";
		try 
		{
			sourceApplicationNamespace = RequirePrefixCalculator.getAppRequirePrefix(new File(applicationsDir, sourceApplicationName));
			targetApplicationNamespace = RequirePrefixCalculator.getAppRequirePrefix(new File(applicationsDir, targetApplicationName));
		} 
		catch (Exception ex) 
		{
			throw new CommandOperationException("Invalid namespace for application.",ex);
		}
		
		try
		{
			Renamer.renameBladeset(targetBladesetDirectory, sourceApplicationNamespace + "." + sourceBladesetName, targetApplicationNamespace + "." + targetBladesetName);
			out.println("Successfully copied " + sourceApplicationName + "/" + sourceBladesetName +
						" to " + targetApplicationName + "/" + targetBladesetName);
		}
		catch (IOException e)
		{
			throw new CommandOperationException(e);
		}
		return 0;
	}
	
	private void assertValidArgs(String[] args) throws CommandArgumentsException
	{
		if (args.length < 3 || args.length > 4)
		{
			throw new CommandArgumentsException("Incorrent number of arguments.", this);
		}
	}

	private String getTargetBladesetName(String[] args) throws CommandArgumentsException
	{
		String bladesetName = AppStructureVerifier.chompBladesetFromString((args[1]));
		if (args.length == 4)
		{
			bladesetName = AppStructureVerifier.chompBladesetFromString(args[3]);
		}

		if(!NameValidator.legacyIsValidPackageName(new String[] {bladesetName} ))
		{
			throw new CommandArgumentsException(
				"The <target-bladeset-name> parameter can only contain lower-case alphanumeric characters.\n" +
				"  The first character must be a letter.", this);
		}
		return bladesetName;
	}

	private File getTargetBladesetDirectory(String targetApplicationName, String targetBladesetName) throws CommandOperationException
	{
		File applicationsDirectory = new File(sdkBaseDir.getParentFile(), CutlassConfig.APPLICATIONS_DIR);
		File targetApplicationDirectory = new File(applicationsDirectory, targetApplicationName);
		File targetBladeset = new File(targetApplicationDirectory, getBladesetFolderName(targetBladesetName));

		if (targetApplicationDirectory.exists() == false)
		{
			throw new CommandOperationException(
				"The target application '" + targetApplicationName + "' does not exist at location '"
				+ targetApplicationDirectory.getAbsolutePath() + "'");
		}
		
		if (StringUtils.split(targetBladeset.getName(), "-").length > 2)
		{
			// E.g. we split on 'fx-bladeset' and we get [0]fx, [1]bladeset. The length should not be > 2
			throw new CommandOperationException("The bladeset name cannot contain hyphens because of JavaScript variable name limitations.");
		}
		if (targetBladeset.exists())
		{
			throw new CommandOperationException("The target bladeset '" + targetBladeset.getName() + "' already exists inside application '" + targetApplicationName + "'.");
		}

		targetBladeset.mkdirs();

		return targetBladeset;
	}

	private String getBladesetFolderName(String targetBladesetName)
	{
		if (targetBladesetName.endsWith(CutlassConfig.BLADESET_SUFFIX))
		{
			return targetBladesetName;
		}

		return targetBladesetName + CutlassConfig.BLADESET_SUFFIX;
	}

	private File getSourceBladesetDirectory(String sourceApplicationName, String sourceBladesetName) throws CommandOperationException
	{
		File applicationsDirectory = new File(sdkBaseDir.getParentFile(), CutlassConfig.APPLICATIONS_DIR);
		File sourceApplicationDirectory = new File(applicationsDirectory, sourceApplicationName);
		File sourceBladeset = new File(sourceApplicationDirectory, getBladesetFolderName(sourceBladesetName));

		if (sourceBladeset.exists())
		{
			return sourceBladeset;
		}

		throw new CommandOperationException("The source bladeset '" + sourceBladesetName + "' does not exist.");
	}
}
