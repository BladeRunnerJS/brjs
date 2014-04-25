package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NodeMap<N extends Node>
{
	public final Map<String, N> nodes = new HashMap<>();
	public final Class<N> nodeClass;
	
	private final List<NodeMapLocator> nodeMapLocators = new ArrayList<>();
	private final File dir;
	private final RootNode rootNode;
	
	public NodeMap(Node node, Class<N> nodeClass, String subDirPath, String dirNameFilter)
	{
		this.nodeClass = nodeClass;
		dir = node.dir();
		rootNode = node.root();
		nodeMapLocators.add(new DirNodeMapLocator(rootNode, subDirPath, dirNameFilter));
	}
	
	public void addAlternateLocation(String subDirPath, String dirNameFilter)
	{
		nodeMapLocators.add(new DirNodeMapLocator(rootNode, subDirPath, dirNameFilter));
	}
	
	public void addAdditionalNamedLocation(String itemName, String subDirPath)
	{
		nodeMapLocators.add(new SingleDirNodeMapLocator(itemName, subDirPath));
	}
	
	public List<String> getLocatorNames()
	{
		Set<String> visitedLocatorNames = new LinkedHashSet<>();
		List<String> locatorNames = new ArrayList<>();
		
		for(NodeMapLocator nodeMapLocator : nodeMapLocators)
		{
			List<String> names = nodeMapLocator.getDirs(dir);
			
			for(String name : names)
			{
				if(visitedLocatorNames.contains(name))
				{
					throw new BladeRunnerDirectoryException("There are two directories that both have the logical name '" + name + "' within the directory '" + dir.getPath() + "'");
				}
				
				visitedLocatorNames.add(name);
			}
			
		}
		locatorNames.addAll(visitedLocatorNames);
		
		return locatorNames;
	}
	
	public File getDir(String childName)
	{
		List<String> possibleDirNames = getPossibleDirNames(childName);
		File childDir = null;
		
		for(String dirName : possibleDirNames)
		{
			File nextDir = new File(dir, dirName);
			
			if(nextDir.exists())
			{
				childDir = nextDir;
				break;
			}
		}
		
		if(childDir == null) {
			childDir = new File(dir, possibleDirNames.get(0));
		}
		
		return childDir;
	}
	
	private List<String> getPossibleDirNames(String childName)
	{
		List<String> dirNames = new ArrayList<String>();
		
		for(NodeMapLocator nodeMapLocator : nodeMapLocators)
		{
			if(nodeMapLocator.canHandleName(childName))
			{
				dirNames.add(nodeMapLocator.getDirName(childName));
			}
		}
		
		return dirNames;
	}
}
