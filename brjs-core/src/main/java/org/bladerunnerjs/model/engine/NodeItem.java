package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NodeItem<N extends Node>
{
	public N item;
	public Class<N> nodeClass;
	
	private List<NodeLocator> nodeItemLocators = new ArrayList<>();
	
	public NodeItem(Class<N> nodeClass, String subDirPath)
	{
		this.nodeClass = nodeClass;
		nodeItemLocators.add(new DirectoryNodeLocator(subDirPath));
	}
	
	public void addLegacyLocation(String subDirPath)
	{
		nodeItemLocators.add(new DirectoryNodeLocator(subDirPath));
	}
	
	public File getNodeDir(File dir)
	{
		File nodeDir = null;
		
		for(NodeLocator nodeItemLocator : nodeItemLocators)
		{
			File nextNodeDir = nodeItemLocator.getNodeDir(dir);
			
			if(nextNodeDir.exists())
			{
				if(nodeDir == null)
				{
					nodeDir = nextNodeDir;
				}
				else
				{
					throw new BladeRunnerDirectoryException("Directory ambiguity: new directory '" + nodeDir.getAbsolutePath() +
						"' and legacy directory '" + nextNodeDir.getAbsolutePath() + "' can't both exist at the same time.");
				}
			}
		}
		
		// if the directory doesn't presently exist at any of the potential locations then ensure it will be created in the
		// currently recommended location if create() or populate() are ever called
		if(nodeDir == null)
		{
			nodeDir = nodeItemLocators.get(0).getNodeDir(dir);
		}
		
		return nodeDir;
	}
}
