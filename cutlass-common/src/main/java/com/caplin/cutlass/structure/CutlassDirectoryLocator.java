package com.caplin.cutlass.structure;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.caplin.cutlass.util.FileUtility;
import com.caplin.cutlass.CutlassConfig;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;
import com.caplin.cutlass.structure.model.Node;
import com.caplin.cutlass.structure.model.NodeType;
import com.caplin.cutlass.structure.model.SdkModel;
import com.caplin.cutlass.structure.model.node.*;

public class CutlassDirectoryLocator
{

	public static ScopeLevel getScope(File location)
	{
		Node nodeForLocation = SdkModel.getNode(location);
		if (nodeForLocation == null)
		{
			return ScopeLevel.UNKNOWN_SCOPE;
		}
		switch (nodeForLocation.getNodeType())
		{
			case SDK:
				return ScopeLevel.SDK_SCOPE;
			case LIB:
				return ScopeLevel.LIB_SCOPE;
			case APP:
				return ScopeLevel.APP_SCOPE;
			case THIRDPARTY_LIB:
				return ScopeLevel.LIB_SCOPE;
			case ASPECT:
				return ScopeLevel.ASPECT_SCOPE;
			case BLADESET:
				return ScopeLevel.BLADESET_SCOPE;
			case BLADE:
				return ScopeLevel.BLADE_SCOPE;
			case WORKBENCH:
				return ScopeLevel.WORKBENCH_SCOPE;
			case TEST:
				return getScope(nodeForLocation.getParentNode().getDir());
			default:
				return ScopeLevel.UNKNOWN_SCOPE;
		}
	}

	public static File getScopePath(File fullRequestPath)
	{
		ScopeLevel requestScope = getScope(fullRequestPath);

		switch (requestScope)
		{
			case APP_SCOPE:
				return getParentApp(fullRequestPath);
			case ASPECT_SCOPE:
				return getParentAppAspect(fullRequestPath);
			case BLADESET_SCOPE:
				return getParentBladeset(fullRequestPath);
			case BLADE_SCOPE:
				return getParentBlade(fullRequestPath);
			case WORKBENCH_SCOPE:
				return getParentWorkbench(fullRequestPath);
			case SDK_SCOPE:
				return SdkModel.getSdkPath(fullRequestPath).getDir();
			case LIB_SCOPE:
				return SdkModel.getUserLibsPath(fullRequestPath).getDir();
			case UNKNOWN_SCOPE:
			default:
				return null;
		}
	}
	
	public static File getDatabaseRootDir(File location)
	{
		RootNode rootNode = SdkModel.getRootNode(location);
		if (rootNode == null)
		{
			return null;
		}
		else
		{
			return rootNode.getDatabaseNode().getDir();
		}
	}

	public static File getConfigDir(File location)
	{
		RootNode rootNode = SdkModel.getRootNode(location);
		if (rootNode == null)
		{
			return null;
		}
		else
		{
			return rootNode.getConfigNode().getDir();
		}
	}
	
	public static File getTempDir(File location)
	{
		RootNode rootNode = SdkModel.getRootNode(location);
		if (rootNode == null)
		{
			return null;
		}
		else
		{
			return rootNode.getTempNode().getDir();
		}
	}
	
	public static File getTestResultsDir(File location)
	{
		RootNode rootNode = SdkModel.getRootNode(location);
		if (rootNode == null)
		{
			return null;
		}
		else
		{
			return rootNode.getTestResultsNode().getDir();
		}
	}
	
	public static File getRootDir(File location)
	{
		RootNode rootNode = SdkModel.getRootNode(location);
		if (rootNode == null)
		{
			return null;
		}
		else
		{
			return rootNode.getDir();
		}
	}

	//TODO does anything need to ever get this dir?
	public static File getAppRootDir(File location)
	{
		RootNode rootNode = SdkModel.getRootNode(location);
		if (rootNode == null) { return null; }

		Node nodeForThisLocation = SdkModel.getNode(location);
		if (nodeForThisLocation.getNodeType() == NodeType.ROOT)
		{
			return rootNode.getAppsRootNode().getDir();
		}
		if (nodeForThisLocation.getNodeType() == NodeType.SDK)
		{
			return rootNode.getSdkNode().getSysAppsRootNode().getDir();
		}
		
		AppsRootNode appsRootNode = SdkModel.getAppsRootNode(location);
		if (appsRootNode.getParentNode().getNodeType() == NodeType.ROOT)
		{
			return rootNode.getAppsRootNode().getDir();
		}
		else if (appsRootNode.getParentNode().getNodeType() == NodeType.SDK)
		{
			return rootNode.getSdkNode().getSysAppsRootNode().getDir();
		}

		return null;
	}
	
	public static File getAppConfFile(File location)
	{
		Node appNode = SdkModel.getAppNode(location);
		if (appNode != null)
		{
			File appDir = appNode.getDir();
			return new File(appDir, CutlassConfig.APP_CONF_FILENAME);
		}
		return null;
	}
	
	public static File getThirdpartyLibrary(File location) 
	{
		ThirdpartyLibNode thirdpartyNode = SdkModel.getThirdpartyNode(location);
		if (thirdpartyNode == null)
		{
			return null;
		}
		else
		{
			return thirdpartyNode.getDir();
		}
	}

	public static File getParentAppAspect(File location)
	{
		AspectNode aspectNode = SdkModel.getAspectNode(location);
		if (aspectNode == null)
		{
			return null;
		}
		else
		{
			return aspectNode.getDir();
		}
	}

	public static File getParentApp(File location)
	{
		AppNode appNode = SdkModel.getAppNode(location);
		if (appNode == null)
		{
			return null;
		}
		else
		{
			return appNode.getDir();
		}
	}

	public static File getParentBladeset(File location)
	{
		BladesetNode bladesetNode = SdkModel.getBladesetNode(location);
		if (bladesetNode == null)
		{
			return null;
		}
		else
		{
			return bladesetNode.getDir();
		}
	}

	public static File getParentBladeContainer(File location)
	{
		//TODO does anything need to ever get this dir?
		File parentBladeset = getParentBladeset(location);
		if (parentBladeset == null)
		{
			return null;
		}
		else
		{
			return new File(parentBladeset, CutlassConfig.BLADES_CONTAINER_DIR);
		}
	}

	public static File getParentBlade(File location)
	{
		BladeNode bladeNode = SdkModel.getBladeNode(location);
		if (bladeNode == null)
		{
			return null;
		}
		else
		{
			return bladeNode.getDir();
		}
	}

	public static File getParentWorkbench(File location)
	{
		WorkbenchNode workbenchNode = SdkModel.getWorkbenchNode(location);
		if (workbenchNode == null)
		{
			return null;
		}
		else
		{
			return workbenchNode.getDir();
		}
	}

	//TODO: delete this method
	public static List<File> getChildApps(File location)
	{
		return getApps(location);
	}
	
	public static List<File> getApps(File location)
	{
		ArrayList<File> apps = new ArrayList<File>();
		
		RootNode rootNode = SdkModel.getRootNode(location);
		if (rootNode != null)
		{
			Node nodeForThisLocation = SdkModel.getNode(location);
			AppsRootNode appsRootNode = SdkModel.getAppsRootNode(location);
			
			List<Node> appNodes = new LinkedList<Node>();
			if (nodeForThisLocation.getNodeType() == NodeType.ROOT || nodeForThisLocation.getNodeType() == NodeType.SDK)
			{
				appNodes.addAll(rootNode.getAppsRootNode().getAppNodes());
			}
			else if (appsRootNode.getParentNode().getNodeType() == NodeType.ROOT)
			{
				appNodes.addAll(rootNode.getAppsRootNode().getAppNodes());
			}
			else if (appsRootNode.getParentNode().getNodeType() == NodeType.SDK)
			{
				appNodes.addAll(rootNode.getSdkNode().getSysAppsRootNode().getAppNodes());
			}
			
			
			for (Node n : appNodes)
			{
				apps.add(n.getDir());
			}
			Collections.sort(apps);
		}
		else 
		{
			apps = null;
		}
		return apps;
	}
	
	//TODO: delete this method
	public static List<File> getChildBladesets(File location)
	{
		return getBladesets(location);
	}
	
	public static List<File> getBladesets(File location)
	{
		ArrayList<File> bladesets = new ArrayList<File>();
		
		AppNode parentApp = SdkModel.getAppNode(location);
		if (parentApp != null)
		{
			for (Node n : parentApp.getBladesetNodes())
			{
				bladesets.add(n.getDir());
			}
			Collections.sort(bladesets);
		}
		else
		{
			bladesets = null;
		}
		return bladesets;
	}

	//TODO: delete this method
	public static List<File> getChildBlades(File location)
	{
		return getBlades(location);
	}
	
	public static List<File> getBlades(File location)
	{
		ArrayList<File> blades = new ArrayList<File>();
		
		BladesetNode parentBladeset = SdkModel.getBladesetNode(location);
		if (parentBladeset != null)
		{
			for (Node n : parentBladeset.getBladeNodes())
			{
				blades.add(n.getDir());
			}
			Collections.sort(blades);
		}
		return blades;
	}

	public static File getBladeWorkbenchDir(File location)
	{
		BladeNode bladeNode = SdkModel.getBladeNode(location);
		if (bladeNode == null)
		{
			return null;
		}
		else
		{
			return bladeNode.getWorkbenchNode().getDir();
		}
	}
	
	//TODO: delete this method
	public static List<File> getApplicationAspects(File application)
	{
		return getAspects(application);
	}
	
	public static List<File> getAspects(File location)
	{
		ArrayList<File> aspects = new ArrayList<File>();
		
		AppNode parentApp = SdkModel.getAppNode(location);
		if (parentApp != null)
		{
			for (Node n : parentApp.getAspectNodes())
			{
				aspects.add(n.getDir());
			}
		}
		return aspects;
	}
	
	public static List<File> getApplications(File location)
	{
		ArrayList<File> apps = new ArrayList<File>();
		
		RootNode rootNode = SdkModel.getRootNode(location);
		if (rootNode != null)
		{
			for (Node n : rootNode.getAppsRootNode().getAppNodes())
			{
				if (!n.getDir().isHidden())
				{
					apps.add(n.getDir());
				}
			}
		}
		return apps;
	}

	//TODO: should this be here?
	public static List<File> getDirectories(File directory) 
	{		
		List<File> dirs = new ArrayList<File>();
		if (invalidInput(directory) || !directory.exists())
		{
			return dirs;
		}
		List<File> sortedDirContents = Arrays.asList(FileUtility.sortFiles(directory.listFiles()));
		
		for (File dirContent : sortedDirContents)
		{
			if(dirContent.isDirectory() == true)
			{
				dirs.add(dirContent);
			}
		}

		return dirs;
	}

	
	public static boolean isRootDir(File dir)
	{
		return SdkModel.nodeForLocationIsOfType(dir, NodeType.ROOT);
	}
	
	public static boolean isAppDir(File dir)
	{
		return SdkModel.nodeForLocationIsOfType(dir, NodeType.APP);
	}

	public static boolean isSdkRootDir(File dir)
	{
		return SdkModel.nodeForLocationIsOfType(dir, NodeType.SDK);
	}

	public static boolean isBladesetDir(File dir)
	{
		return SdkModel.nodeForLocationIsOfType(dir, NodeType.BLADESET);
	}

	public static boolean isBladeDir(File dir)
	{
		return SdkModel.nodeForLocationIsOfType(dir, NodeType.BLADE);
	}

	public static boolean isWorkbenchDir(File dir)
	{
		return SdkModel.nodeForLocationIsOfType(dir, NodeType.WORKBENCH);
	}
	
	//TODO: move to sdk model
	public static File getSDkThirdpartySrcDir(File location)
	{
		if (invalidInput(location))
		{
			return null;
		}
		if (isRootDir(location))
		{
			File sdkThirdpartyDir = SdkModel.getSdkPath(location).libsPath().javascriptLibsPath().thirdpartyLibsPath().getDir();
			
			if (existsAndIsDir(sdkThirdpartyDir))
			{
				return sdkThirdpartyDir;
			}
			else
			{
				ThreadSafeStaticBRJSAccessor.root.logger(CutlassDirectoryLocator.class).info("Could not find thirdparty directory at '" + sdkThirdpartyDir.getPath() + "'.");
				return null;
			}
		}
		else
		{
			return getSDkThirdpartySrcDir(location.getParentFile());
		}
	}
	
	public static File getDefaultAppAspect(File contextDir)
	{
		File defaultAppAspect = new File(CutlassDirectoryLocator.getParentApp(contextDir), CutlassConfig.DEFAULT_ASPECT_DIR);
		if(existsAndIsDir(defaultAppAspect))
		{
			return defaultAppAspect;
		}
		return null;
	}
	
	/* helper methods */
	
	//TODO: delete these when the final methods have been moved to use sdk model
	private static boolean invalidInput(File input)
	{
		return input == null;
		/* we cannot check that the file exists since in the logical structure js/js.bundle will not exist */
		/* TODO: check this is correct or find a better way to check a path is valid */	
	}
	
	private static boolean existsAndIsDir(File f)
	{
		return f.exists() && f.isDirectory();
	}

}
