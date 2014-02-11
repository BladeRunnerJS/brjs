package com.caplin.cutlass.bundler;

import static com.caplin.cutlass.structure.CutlassDirectoryLocator.getChildBlades;
import static com.caplin.cutlass.structure.CutlassDirectoryLocator.getChildBladesets;
import static com.caplin.cutlass.structure.CutlassDirectoryLocator.getParentApp;
import static com.caplin.cutlass.structure.CutlassDirectoryLocator.getParentAppAspect;
import static com.caplin.cutlass.structure.CutlassDirectoryLocator.getParentBlade;
import static com.caplin.cutlass.structure.CutlassDirectoryLocator.getParentBladeContainer;
import static com.caplin.cutlass.structure.CutlassDirectoryLocator.getScope;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.App;

import com.caplin.cutlass.BRJSAccessor;

import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.JsLib;

import com.caplin.cutlass.structure.CutlassDirectoryLocator;
import com.caplin.cutlass.structure.ScopeLevel;
import com.caplin.cutlass.structure.model.Node;
import com.caplin.cutlass.structure.model.SdkModel;
import com.caplin.cutlass.structure.model.path.AppPath;
import com.caplin.cutlass.structure.model.path.AspectPath;
import com.caplin.cutlass.util.FileUtility;

public class BladeRunnerSourceFileProvider implements SourceFileProvider
{
	private BladeRunnerFileAppender bundlerFileAppender;
	private static boolean DO_FILTER = true;

	public BladeRunnerSourceFileProvider(BladeRunnerFileAppender bundlerFileAppender)
	{
		this.bundlerFileAppender = bundlerFileAppender;
	}

	@Override
	public List<File> getSourceFiles(File baseDir, File testDir) throws ContentProcessingException
	{

		if (!baseDir.exists()) 
		{
			return new ArrayList<File>();
		}
		ScopeLevel requestLevel = getScope(baseDir); // TODO: get rid of this line and just use the line below instead
		Node sdkNode = SdkModel.getNode(baseDir);
		int requestLevelDepth = requestLevel.ordinal();
		List<ScopeLevel> scopeLevels = RequestScopeProvider.getScopeLevels(sdkNode);
		List<File> sourceFiles = new ArrayList<File>();

		for(ScopeLevel scopeLevel : scopeLevels)
		{
			int scopeLevelDepth = scopeLevel.ordinal();
			List<File> contextDirectories = new ArrayList<File>();
			
			if(scopeLevelDepth < requestLevelDepth)
			{
				getParentScope(baseDir, scopeLevel, requestLevel, contextDirectories);
			}
			else if(scopeLevelDepth > requestLevelDepth)
			{
				getChildScopes(baseDir, scopeLevel, contextDirectories);
			}
			else
			{
				contextDirectories.add(baseDir);
			}
			
			for(File contextDir : contextDirectories)
			{
				appendScopeFiles(baseDir, contextDir, scopeLevel, requestLevel, sourceFiles);
			}
		}
	
		if (testDir != null)
		{
			bundlerFileAppender.appendTestFiles(testDir, sourceFiles);
		}
		
		if(requestLevel == ScopeLevel.ASPECT_SCOPE && DO_FILTER)
		{
			EnabledBladesFilter enabledBladesFilter = new EnabledBladesFilter(new UsedBladesFinder());
			sourceFiles = enabledBladesFilter.filter(baseDir, sourceFiles);
		}
		
		return sourceFiles;
	}
	
	private void appendScopeFiles(File baseDir, File contextDir, ScopeLevel scopeLevel, ScopeLevel requestLevel, List<File> sourceFiles) throws ContentProcessingException
	{
		switch(scopeLevel)
		{
			case THIRDPARTY_LIBRARY_SCOPE:
				bundlerFileAppender.appendThirdPartyLibraryFiles(contextDir, sourceFiles);
				break;
		
			case SDK_SCOPE:
				appendAllLibraryFiles(contextDir, sourceFiles);
				break;

			case LIB_SCOPE:
				appendAllUserLibraryFiles(contextDir, sourceFiles);
				break;
			
			case ASPECT_SCOPE:
				if(baseDir.getName().equals("workbench"))
				{
					AspectPath aspectPath = AppPath.locateAncestorPath(baseDir).aspectPath("default");
					bundlerFileAppender.appendWorkbenchAspectFiles(aspectPath.getDir(), sourceFiles);
				}
				else
				{
					bundlerFileAppender.appendAppAspectFiles(contextDir, sourceFiles);
				}
				break;
			
			case BLADESET_SCOPE:
				bundlerFileAppender.appendBladesetFiles(contextDir, sourceFiles);
				break;
			
			case BLADE_SCOPE:
				bundlerFileAppender.appendBladeFiles(contextDir, sourceFiles);
				break;
			
			case WORKBENCH_SCOPE:
				bundlerFileAppender.appendWorkbenchFiles(contextDir, sourceFiles);
				break;
			
			default:
				break;
		}
	}
	
	private void appendAllLibraryFiles(File contextDir, List<File> sourceFiles) throws ContentProcessingException
	{
		JsLib jsLib = BRJSAccessor.root.sdkLib();
		
		AssetLocation libSrcRoot = jsLib.assetLocation("src");
		AssetLocation libResourcesRoot = jsLib.assetLocation("resources");

		if(libSrcRoot.dirExists())
		{
			bundlerFileAppender.appendLibrarySourceFiles(libSrcRoot.dir(), sourceFiles);
		}
		
		if(libResourcesRoot.dirExists())
		{
			List<File> libraryResourceDirs = getLibraryResourceDirs(libResourcesRoot.dir(), libSrcRoot.dir());
			
			for(File libraryResourceDir : libraryResourceDirs)
			{
				bundlerFileAppender.appendLibraryResourceFiles(libraryResourceDir, sourceFiles);
			}
		}
	}
	
	private void appendAllUserLibraryFiles(File contextDir, List<File> sourceFiles) throws ContentProcessingException
	{
		App app = BRJSAccessor.root.locateAncestorNodeOfClass(contextDir, App.class);
		
		Path sdkLibPath = BRJSAccessor.root.sdkLib().assetLocation("src").dir().toPath();

		for(JsLib jsLib: app.jsLibs())
		{
			AssetLocation libSrcRoot = jsLib.assetLocation("src");
			AssetLocation libResourcesRoot = jsLib.assetLocation("resources");
			
			if((libSrcRoot != null) && libSrcRoot.dirExists())
			{
				if(libSrcRoot.dir().toPath().normalize().equals(sdkLibPath))
				{
					continue;
				}
				bundlerFileAppender.appendLibrarySourceFiles(libSrcRoot.dir(), sourceFiles);
			}
			
			if((libResourcesRoot != null) && libResourcesRoot.dirExists())
			{
				List<File> libraryResourceDirs = getLibraryResourceDirs(libResourcesRoot.dir(), libSrcRoot.dir());
				
				for(File libraryResourceDir : libraryResourceDirs)
				{
					bundlerFileAppender.appendLibraryResourceFiles(libraryResourceDir, sourceFiles);
				}
			}
		}
	}
	
	private void getParentScope(File baseDir, ScopeLevel scopeLevel, ScopeLevel requestLevel, List<File> contextDirectories)
	{
		switch(scopeLevel)
		{
			case THIRDPARTY_LIBRARY_SCOPE:
				contextDirectories.add(getParentApp(baseDir));
				break;
			
			case SDK_SCOPE:
				contextDirectories.add(SdkModel.getSdkPath(baseDir).getDir());
				break;
			
			case LIB_SCOPE:
				contextDirectories.add(SdkModel.getUserLibsPath(baseDir).getDir());
				break;
			
			case ASPECT_SCOPE:
				addAppAspectDir(contextDirectories, baseDir, requestLevel);
				break;
			
			case BLADESET_SCOPE:
				contextDirectories.add(getParentBladeContainer(baseDir).getParentFile());
				break;
			
			case BLADE_SCOPE:
				contextDirectories.add(getParentBlade(baseDir));
				break;
			default:
				break;
		}
	}

	private void addAppAspectDir(List<File> contextDirectories, File baseDir, ScopeLevel requestLevel)
	{
		if(requestLevel == ScopeLevel.WORKBENCH_SCOPE)
		{
			File defaultAppAspect = CutlassDirectoryLocator.getDefaultAppAspect(baseDir);
			if(defaultAppAspect != null)
			{
				contextDirectories.add(defaultAppAspect);
			}
		}
		else
		{
			contextDirectories.add(getParentAppAspect(baseDir));
		}
	}

	
	private void getChildScopes(File baseDir, ScopeLevel scopeLevel, List<File> contextDirectories)
	{
		switch(scopeLevel)
		{
			case BLADESET_SCOPE:
				for(File bladeset : getChildBladesets(getParentApp(baseDir)))
				{
					contextDirectories.add(bladeset);
				}
				break;
			
			case BLADE_SCOPE:
				for(File bladeset : getChildBladesets(getParentApp(baseDir)))
				{
					for(File blade : getChildBlades(bladeset))
					{
						contextDirectories.add(blade);
					}
				}
				break;
			default:
				break;
		}
	}
	
	private List<File> getLibraryResourceDirs(File resourceDir, File sourceDir)
	{
		List<File> resourceDirs = new ArrayList<File>();
		
		getLibraryResourceDirs(resourceDir, sourceDir, resourceDirs);
		
		return resourceDirs;
	}
	
	private void getLibraryResourceDirs(File resourceDir, File sourceDir, List<File> resourceDirs)
	{
		for(File resourceSubDir : FileUtility.listDirs(resourceDir))
		{
			File sourceSubDir = new File(sourceDir, resourceSubDir.getName());
			
			if(resourceSubDir.isDirectory() && !resourceSubDir.isHidden() && sourceSubDir.isDirectory() && !sourceSubDir.isHidden())
			{
				resourceDirs.add(resourceSubDir);
				getLibraryResourceDirs(resourceSubDir, sourceSubDir, resourceDirs);
			}
		}
	}
	
	/**
	 * DO NOT USE - for testing - so tests that dont care about only bundling used blades dont have to care
	 */
	public static void disableUsedBladesFiltering()
	{
		DO_FILTER = false;
	}
}
