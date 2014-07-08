package com.caplin.cutlass.structure.model;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.structure.model.node.*;
import com.caplin.cutlass.structure.model.path.*;

public class SdkModel
{
	
	private static final Map<String,Node> nodeMap = Collections.synchronizedMap(new HashMap<String,Node>());
	
	public static RootNode getRootNode(File path)
	{
		return (RootNode) getNodeOfType(path, NodeType.ROOT);
	}
	
	public static AppsRootNode getAppsRootNode(File path)
	{
		return (AppsRootNode) getNodeOfType(path, NodeType.APPS_ROOT);
	}
	
	public static AppNode getAppNode(File path)
	{
		return (AppNode) getNodeOfType(path, NodeType.APP);
	}
	
	public static AspectNode getAspectNode(File path)
	{
		return (AspectNode) getNodeOfType(path, NodeType.ASPECT);
	}
	
	public static BladesetNode getBladesetNode(File path)
	{
		return (BladesetNode) getNodeOfType(path, NodeType.BLADESET);
	}
	
	public static BladeNode getBladeNode(File path)
	{
		return (BladeNode) getNodeOfType(path, NodeType.BLADE);
	}
	
	public static ConfigNode getConfigNode(File path)
	{
		return (ConfigNode) getNodeOfType(path, NodeType.CONFIG);
	}
	
	public static DatabaseNode getDatabaseNode(File path)
	{
		return (DatabaseNode) getNodeOfType(path, NodeType.DATABASE);
	}
	
	public static SdkNode getSdkNode(File path)
	{
		return (SdkNode) getNodeOfType(path, NodeType.SDK);
	}
	
	public static SdkPath getSdkPath(File path)
	{
		RootNode rootNode = getRootNode(path);
		return (rootNode == null) ? new SdkPath((File) null) : rootNode.getSdkNode().getPath();
	}
	
	public static UserLibNode getUserLibsNode(File path)
	{
		return (UserLibNode) getNodeOfType(path, NodeType.LIB);
	}
	
	public static WorkbenchNode getWorkbenchNode(File path)
	{
		return (WorkbenchNode) getNodeOfType(path, NodeType.WORKBENCH);
	}
	
	public static ThirdpartyLibNode getThirdpartyNode(File path)
	{
		return (ThirdpartyLibNode) getNodeOfType(path, NodeType.THIRDPARTY_LIB);
	}
	
	public static File getThirdpartyDir(File path)
	{
		ThirdpartyLibNode thirdpartyLibNode = getThirdpartyNode(path);
		return (thirdpartyLibNode == null) ? null : thirdpartyLibNode.getDir();
	}
	
	private static Node getNodeOfType(File path, NodeType type)
	{
		Node node = getNode(path);
		return (node == null) ? null : node.getAncestorOfType(type);
	}
	
	public static Node getNode(File path)
	{
		if (path == null) { return null; }
		Node foundNode = getNodeFromMap(path);
		if (foundNode == null)
		{
			foundNode = findNodeForPath(path);
			if ((foundNode != null) && !foundNode.getDir().equals(path))
			{
				registerNodeForPath(foundNode, path);
			}
		}
		return foundNode;
	}
	
	public static void registerNode(Node node)
	{
		registerNodeForPath(node, node.getDir());
	}
	
	public static void registerNodeForPath(Node node, File path)
	{
		String absolutePath = path.getAbsolutePath();
		nodeMap.put(absolutePath, node);
	}
	
	public static void unregisterNode(Node node)
	{
		String absolutePath = node.getDir().getAbsolutePath();
		nodeMap.remove(absolutePath);
	}
	
	public static boolean nodeForLocationIsOfType(File location, NodeType type)
	{
		Node nodeForLocation = getNode(location);
		if (nodeForLocation == null)
		{
			return false;
		}
		else
		{
			return nodeForLocation.getNodeType() == type;
		}
	}
	
	public static void removeAllNodes()
	{
		nodeMap.clear();
	}
	
	
	/* private stuff */
	
	private static Node getNodeFromMap(File path)
	{
		String absolutePath = path.getAbsolutePath();
		return nodeMap.get(absolutePath);
	}
	
	private static Node findNodeForPath(File path)
	{
		Node node = null;
		Node prevNode = null;
		
		do
		{
			prevNode = node;
			if(prevNode != null)
			{
				prevNode.calculateChildNodes();
			}
			
			node = findDeepestNode(path);
		} while(node != prevNode);
		
		return node;
	}
	
	private static Node findDeepestNode(File path)
	{
		File nodePath = path;
		while (nodePath != null)
		{
			Node foundNode = getNodeFromMap(nodePath);
			if (foundNode != null)
			{
				return foundNode;
			}
			if (isInstallRootDir(nodePath))
			{
				return NodeFactory.createNodeOfType(NodeType.ROOT, null, nodePath);
			}
			nodePath = nodePath.getParentFile();
		}
		return null;
	}
	
	private static boolean isInstallRootDir(File dir)
	{
		boolean containsSdkDir = false;
		boolean containsAppDir = false;
		if (dir.exists() && dir.isDirectory())
		{
			for (File child : dir.listFiles())
			{
				if (child.isDirectory() && !child.isHidden())
				{
					containsSdkDir = (containsSdkDir == false) ? child.getName().equals(CutlassConfig.SDK_DIR) : containsSdkDir;
					//TODO: decide whether a root dir is a root dir if it contains /sdk AND /apps - this currently stops bladerunner from starting
					containsAppDir = true;//(containsAppDir == false) ? child.getName().equals(CutlassConfig.APPLICATIONS_DIR) : containsAppDir;
				}
			}
		}
		
		return containsSdkDir && containsAppDir;
	}
}
