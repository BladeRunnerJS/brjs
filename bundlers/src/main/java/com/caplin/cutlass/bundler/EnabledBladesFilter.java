package com.caplin.cutlass.bundler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import com.caplin.cutlass.structure.CutlassDirectoryLocator;

public class EnabledBladesFilter
{
	private UsedBladesFinder usedBladesFinder;
	
	public EnabledBladesFilter(UsedBladesFinder usedBladesFinder)
	{
		this.usedBladesFinder = usedBladesFinder;
	}

	public List<File> filter(File aspectRoot, List<File> files) throws BundlerProcessingException
	{
		List<File> usedBlades = getUsedBlades(aspectRoot);
		List<File> usedBladesets = getUsedBladesets(usedBlades);
		
		List<File> filteredFiles = new ArrayList<File>();
		for(File file : files)
		{
			if(shouldIncludeFile(usedBladesets, usedBlades, file))
			{
				filteredFiles.add(file);
			}
		}
		
		return filteredFiles;
	}

	private List<File> getUsedBladesets(List<File> usedBlades)
	{
		List<File> usedBladesets = new ArrayList<File>();
		for(File blade : usedBlades)
		{
			File parentBladeset = CutlassDirectoryLocator.getParentBladeset(blade);
			if(!usedBladesets.contains(parentBladeset))
			{
				usedBladesets.add(parentBladeset);
			}
		}
		return usedBladesets;
	}

	private List<File> getUsedBlades(File aspectRoot) throws BundlerProcessingException
	{
		List<File> usedBlades = usedBladesFinder.findUsedBlades(aspectRoot);
		return usedBlades;
	}
	
	private boolean shouldIncludeFile(List<File> usedBladesets, List<File> usedBlades, File file)
	{
		File parentBlade = CutlassDirectoryLocator.getParentBlade(file);
		File parentBladeset = CutlassDirectoryLocator.getParentBladeset(file);
		
		if(parentBlade != null)
		{
			return usedBlades.contains(parentBlade);
		}
		else if(parentBladeset != null)
		{
			return usedBladesets.contains(parentBladeset);
		}
		return true;
	}
	
}
