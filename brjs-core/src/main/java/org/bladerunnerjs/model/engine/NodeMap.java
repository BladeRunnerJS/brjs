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
	public Map<String, N> nodes = new HashMap<>();
	public Class<N> nodeClass;
	
	private List<NodeMapLocator> nodeMapLocators = new ArrayList<>();
	private final RootNode rootNode;
	
	public NodeMap(RootNode rootNode, Class<N> nodeClass, String subDirPath, String dirNameFilter)
	{
		this.nodeClass = nodeClass;
		nodeMapLocators.add(new DirNodeMapLocator(rootNode, subDirPath, dirNameFilter));
		this.rootNode = rootNode;
	}
	
	public void addAlternateLocation(String subDirPath, String dirNameFilter)
	{
		nodeMapLocators.add(new DirNodeMapLocator(rootNode, subDirPath, dirNameFilter));
	}
	
	public void addAdditionalNamedLocation(String itemName, String subDirPath)
	{
		nodeMapLocators.add(new SingleDirNodeMapLocator(itemName, subDirPath));
	}
	
	public List<String> getLocatorNames(File dir)
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
					// TODO: this is a really bad exception since it doesn't point to the actual directories -- consider how it can be fixed
					throw new BladeRunnerDirectoryException("There are two directories that both have the logical name '" + name + "'");
				}
				
				visitedLocatorNames.add(name);
			}
			
		}
		locatorNames.addAll(visitedLocatorNames);
		
		return locatorNames;
	}
	
	public File getDir(File dir, String childName)
	{
		List<String> possibleDirNames = getPossibleDirNames(childName);
		// TODO: can this be initialized to null?
		File childDir = new File(dir, possibleDirNames.get(0));
		
		for(String dirName : possibleDirNames)
		{
			File nextDir = new File(dir, dirName);
			
			if(nextDir.exists())
			{
				childDir = nextDir;
				break;
			}
		}
		
		return childDir;
	}
	
	public List<String> getPossibleDirNames(String childName)
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
