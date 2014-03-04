package com.caplin.cutlass.command.analyse;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import com.caplin.cutlass.CutlassConfig;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.JsLib;

import com.caplin.cutlass.BRJSAccessor;

public class PackageDepsConfig
{
	private File packageDirectory = null;
	private boolean isSummary = false;
	private App app;
	private String packageName;
	private final PackageDepsCommand packageDepsCommand;
	
	public PackageDepsConfig(String[] args, PackageDepsCommand packageDepsCommand) throws CommandArgumentsException
	{
		this.packageDepsCommand = packageDepsCommand;
		if (args.length < 2 || args.length > 3)
		{
			throw new CommandArgumentsException(CutlassConfig.DEFAULT_INVALID_ARGUMENTS_FOR_TASK_MESSAGE, packageDepsCommand);
		}
		
		app = BRJSAccessor.root.app(args[0]);
		packageName = args[1]; 

		setPackageName(packageName);
		
		if(args.length == 3)
		{
			String summary = args[2];
			
			if(summary.toLowerCase().equals("summary"))
			{
				isSummary = true;
			}
			else
			{
				throw new CommandArgumentsException(CutlassConfig.DEFAULT_INVALID_ARGUMENTS_FOR_TASK_MESSAGE, packageDepsCommand);
			}
		}
	}
	
	public File getPackageDirectory()
	{
		return packageDirectory;
	}
	
	
	private void setPackageName(String packageName) throws CommandArgumentsException
	{
		File matchingDirectory = null;
		ArrayList<File> visitedPackageLocations = new ArrayList<>();

		for(JsLib jsLibrary: app.jsLibs())
		{
			File targetDirectory = new File(jsLibrary.assetLocation("src").dir(), packageName.replaceAll("\\.", "/"));
			
			visitedPackageLocations.add(targetDirectory);
			
			if(targetDirectory.exists() && targetDirectory.isDirectory())
			{
				if(matchingDirectory != null)
				{
					throw new CommandArgumentsException("Ambiguous package location: The given package '"+ packageName +"' has been found in more than one library root (" + targetDirectory.getAbsolutePath() + " and " + matchingDirectory.getAbsolutePath() + ")", packageDepsCommand);
				}
				matchingDirectory = targetDirectory;
			}
		}

		if(matchingDirectory == null)
		{
			String locations = visitedPackageLocations.size() > 1 ? "locations" : "location";
			throw new CommandArgumentsException("Package '" + packageName + "' not found in library " + locations + ": " + formatSearchedLocationsString(visitedPackageLocations), packageDepsCommand);
		}

		packageDirectory = matchingDirectory;
		
	}
	
	public boolean isSummary() 
	{
		return isSummary;
	}
	
	public String getPackageName()
	{
		return packageName;
	}

	public App getApp()
	{
		return app;
	}

	// TODO: Move to a utility class
	private String formatSearchedLocationsString(List<File> visitedPackageLocations)
	{
		StringBuilder sb = new StringBuilder();
		String separator = "";
		for(File f: visitedPackageLocations)
		{
			sb.append(separator);
			sb.append(f.getPath());
			
			separator = ", ";
		}

		return sb.toString();
	}
	
}
