package com.caplin.cutlass.bundler.js;

import static com.caplin.cutlass.structure.CutlassDirectoryLocator.getScope;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.filefilter.HiddenFileFilter;
import org.apache.commons.lang3.StringUtils;

import com.caplin.cutlass.exception.NamespaceException;
import com.caplin.cutlass.structure.CutlassDirectoryLocator;
import com.caplin.cutlass.structure.RequirePrefixCalculator;
import com.caplin.cutlass.structure.ScopeLevel;


public class JsNamespaceVerifier
{
		
	public void verifyThatJsFilesAreInCorrectNamespace(File baseDir) throws NamespaceException
	{
		if (baseDir.exists()) 
		{
			ScopeLevel requestLevel = getScope(baseDir);
			
			if((requestLevel == ScopeLevel.BLADESET_SCOPE && CutlassDirectoryLocator.isBladesetDir(baseDir)) || (requestLevel == ScopeLevel.BLADE_SCOPE && CutlassDirectoryLocator.isBladeDir(baseDir)))
			{
				File bladesetOrBladeSrcDirectory = new File(baseDir, "src");
				
				if(bladesetOrBladeSrcDirectory.exists())
				{
					File bladesetNamespaceDirectoryInBlade = verifyThatNamespacingDirectoriesAreCorrectToTheBladesetLevel(baseDir, bladesetOrBladeSrcDirectory);
					
					if(requestLevel == ScopeLevel.BLADE_SCOPE)
					{
						verifyThatBladesetNamespaceFolderOnlyContainsFolderWithBladeNamespace(bladesetNamespaceDirectoryInBlade, baseDir);
					}
				}
			}
		}
	}
	
	private File verifyThatNamespacingDirectoriesAreCorrectToTheBladesetLevel(File bladeOrBladeset, File bladesOrBladesetSrcDirectory) throws NamespaceException
	{
		File applicationNamespaceDirectoryInBlade = verifyThatSrcFolderOnlyContainsFolderWithApplicationNamespace(bladesOrBladesetSrcDirectory, bladeOrBladeset);
		return verifyThatApplicationNamespaceFolderOnlyContainsFolderWithBladesetNamespace(applicationNamespaceDirectoryInBlade, bladeOrBladeset);
	}
	
	private File verifyThatSrcFolderOnlyContainsFolderWithApplicationNamespace(File bladesOrBladesetSrcDirectory, File bladeOrBladeset) throws NamespaceException
	{
		String applicationNamespace = RequirePrefixCalculator.getAppRequirePrefix(bladeOrBladeset);
		File[] applicationNamespaceDirectoryInBladeOrBladeset = bladesOrBladesetSrcDirectory.listFiles((FileFilter)HiddenFileFilter.VISIBLE);
		
		if(applicationNamespaceDirectoryInBladeOrBladeset.length != 1)
		{
			String msg = "There must only be one, and only one, folder inside the src directory '" + bladeOrBladeset.getAbsolutePath() + "'\n";
			if (applicationNamespaceDirectoryInBladeOrBladeset.length > 1)
			{
				msg += "The following additional files were found:\n";
				msg += StringUtils.join(applicationNamespaceDirectoryInBladeOrBladeset, "\n");
			}
			throw new NamespaceException(msg);
		}
		
		if(!applicationNamespaceDirectoryInBladeOrBladeset[0].getName().equals(applicationNamespace))
		{
			throw new NamespaceException("The folder inside the src directory at '" + bladeOrBladeset.getAbsolutePath() + "' must be named after the namespace of the application, '" + applicationNamespace + "'");
		}
		
		return applicationNamespaceDirectoryInBladeOrBladeset[0];
	}

	private File verifyThatApplicationNamespaceFolderOnlyContainsFolderWithBladesetNamespace(File bladeOrBladesetApplicationDirectory, File bladeOrBladeset) throws NamespaceException
	{
		String bladesetNamespace = RequirePrefixCalculator.getBladesetRequirePrefix(bladeOrBladeset);
		File[] bladesetNamespaceDirectoryInBladeOrBladeset = bladeOrBladesetApplicationDirectory.listFiles((FileFilter)HiddenFileFilter.VISIBLE);
		
		if(bladesetNamespaceDirectoryInBladeOrBladeset.length != 1)
		{
			String msg = "There must be one, and only one, folder inside the application namespace directory '" + bladeOrBladeset.getAbsolutePath() + "'\n";
			if (bladesetNamespaceDirectoryInBladeOrBladeset.length > 1)
			{
				msg += "The following additional files were found:\n";
				msg += StringUtils.join(bladesetNamespaceDirectoryInBladeOrBladeset, "\n");
			}
			throw new NamespaceException(msg);
		}
		
		if(!bladesetNamespaceDirectoryInBladeOrBladeset[0].getName().equals(bladesetNamespace))
		{
			throw new NamespaceException("The folder inside the src directory at '" + bladeOrBladeset.getAbsolutePath() + "' must be named after the namespace of the bladeset '" + bladesetNamespace + "'");
		}
		
		return bladesetNamespaceDirectoryInBladeOrBladeset[0];
	}
	
	private void verifyThatBladesetNamespaceFolderOnlyContainsFolderWithBladeNamespace(File bladesetNamespaceDirectory, File blade) throws NamespaceException
	{
		String bladeNamespace = RequirePrefixCalculator.getBladeRequirePrefix(blade);
		File[] bladeNamespaceDirectoryInBlade = bladesetNamespaceDirectory.listFiles((FileFilter)HiddenFileFilter.VISIBLE);
		
		if(bladeNamespaceDirectoryInBlade.length != 1)
		{
			String msg = "There must be one, and only one, folder inside the src/<app-namespace>/<bladeset-namespace> directory for blade '" + blade.getName() + "'\n";
			if (bladeNamespaceDirectoryInBlade.length > 1)
			{
				msg += "The following additional files were found:\n";
				msg += StringUtils.join(bladeNamespaceDirectoryInBlade, "\n");
			}
			throw new NamespaceException(msg);
		}
		
		if(!bladeNamespaceDirectoryInBlade[0].getName().equals(bladeNamespace))
		{
			throw new NamespaceException("The directory in '" + bladeNamespaceDirectoryInBlade[0].getAbsolutePath() + "' must be named after the namespace of the blade, '" + bladeNamespace + "'");
		}
	}
	
}
