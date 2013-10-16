package com.caplin.cutlass.command.analyse;

import java.io.File;

import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.sinbin.CutlassConfig;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import com.caplin.cutlass.BRJSAccessor;

public class AnalyserConfig
{
	File appDirectory;
	File aspectDirectory;
	boolean jsonFormat = false;
	private final DependencyAnalyserCommand dependencyAnalyserCommand;

	public AnalyserConfig(String[] args, DependencyAnalyserCommand dependencyAnalyserCommand) throws CommandArgumentsException
	{
		
		this.dependencyAnalyserCommand = dependencyAnalyserCommand;
		if (args.length == 0 || args.length > 3)
		{
			throw new CommandArgumentsException(CutlassConfig.DEFAULT_INVALID_ARGUMENTS_FOR_TASK_MESSAGE, dependencyAnalyserCommand);
		}
		
		String appName = args[0];
		String aspectName = (args.length > 1) ? args[1] : CutlassConfig.DEFAULT_ASPECT_NAME;
		
		try
		{
			aspectDirectory = getAspectDirectory(appName, aspectName);
			checkForJsonArg(args);
		}
		catch (Exception e)
		{
			throw new CommandArgumentsException(e, dependencyAnalyserCommand);
		}
	}

	private File getAspectDirectory(String appName, String aspectName) throws Exception 
	{
		App application = BRJSAccessor.root.app(appName);
		
		if (application.dirExists() == false)
		{
			throw new Exception("Could not find application '" + appName + "' inside your '" + CutlassConfig.APPLICATIONS_DIR + "' folder.");
		}
		
		Aspect aspectDirFileObject = BRJSAccessor.root.app(appName).aspect(aspectName);
		
		if (aspectDirFileObject.dirExists() == false)
		{
			throw new Exception("Could not find aspect '" + aspectName + "' for application '" + appName + "'.");
		}
		
		return aspectDirFileObject.dir(); 
	}

	public File getAspectDirectory()
	{
		return aspectDirectory;
	}
	
	public File getApplicationDirectory()
	{
		return aspectDirectory.getParentFile();
	}
	
	public boolean getOutputFormatInJson()
	{
		return jsonFormat;
	}
	
	private void checkForJsonArg(String[] args) throws CommandArgumentsException
	{
		if (args.length > 2)
		{
			if (args[2].equalsIgnoreCase("json"))
			{
				jsonFormat = true;
			}
			else
			{
				throw new CommandArgumentsException("Could not recognise format parameter '" + args[2] + "'.", dependencyAnalyserCommand);
			}
		}
	}
	
}