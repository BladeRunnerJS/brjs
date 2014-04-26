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
	public final Map<String, N> namedNodes = new HashMap<>();
	public final Class<N> nodeClass;
	
	private final List<NamedNodeLocator> namedNodeLocators = new ArrayList<>();
	private final File dir;
	private final RootNode rootNode;
	
	public NodeMap(Node node, Class<N> nodeClass, String subDirPath, String dirNameFilter)
	{
		this.nodeClass = nodeClass;
		dir = node.dir();
		rootNode = node.root();
		namedNodeLocators.add(new DirectoryContentsNamedNodeLocator(rootNode, subDirPath, dirNameFilter));
	}
	
	public void addAlternateLocation(String subDirPath, String dirNameFilter)
	{
		namedNodeLocators.add(new DirectoryContentsNamedNodeLocator(rootNode, subDirPath, dirNameFilter));
	}
	
	public void addAdditionalNamedLocation(String itemName, String subDirPath)
	{
		namedNodeLocators.add(new SingleDirectoryNamedNodeLocator(itemName, subDirPath));
	}
	
	public List<String> getLogicalNodeNames()
	{
		Set<String> combinedLogicalNodeNames = new LinkedHashSet<>();
		
		for(NamedNodeLocator namedNodeLocator : namedNodeLocators)
		{
			List<String> logicalNodeNames = namedNodeLocator.getLogicalNodeNames(dir);
			
			for(String logicalNodeName : logicalNodeNames)
			{
				if(combinedLogicalNodeNames.contains(logicalNodeName))
				{
					throw new BladeRunnerDirectoryException("There are two directories that both have the logical name '" + logicalNodeName + "' within the directory '" + dir.getPath() + "'");
				}
				
				combinedLogicalNodeNames.add(logicalNodeName);
			}
			
		}
		
		return new ArrayList<>(combinedLogicalNodeNames);
	}
	
	public File getNodeDir(String logicalNodeName)
	{
		List<String> possibleDirNames = getPossibleDirNames(logicalNodeName);
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
	
	private List<String> getPossibleDirNames(String logicalNodeName)
	{
		List<String> possibleDirNames = new ArrayList<String>();
		
		for(NamedNodeLocator namedNodeLocator : namedNodeLocators)
		{
			if(namedNodeLocator.couldSupportLogicalNodeName(logicalNodeName))
			{
				possibleDirNames.add(namedNodeLocator.getDirName(logicalNodeName));
			}
		}
		
		return possibleDirNames;
	}
}
