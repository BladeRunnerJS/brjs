package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NodeItem<N extends Node>
{
	public N item;
	public Class<N> nodeClass;
	
	private List<NodeItemLocator> nodeItemLocators = new ArrayList<>();
	
	public NodeItem(Class<N> nodeClass, String subDirPath)
	{
		this.nodeClass = nodeClass;
		nodeItemLocators.add(new DirNodeItemLocator(subDirPath));
	}
	
	public void addLegacyLocation(String subDirPath)
	{
		nodeItemLocators.add(new DirNodeItemLocator(subDirPath));
	}
	
	public File getItemDir(File sourceDir)
	{
		File itemDir = null;
		
		for(NodeItemLocator nodeItemLocator : nodeItemLocators)
		{
			File nextItemDir = nodeItemLocator.getDir(sourceDir);
			
			if(nextItemDir.exists())
			{
				if(itemDir == null)
				{
					itemDir = nextItemDir;
				}
				else
				{
					throw new BladeRunnerDirectoryException("Directory ambiguity: new directory '" + itemDir.getAbsolutePath() +
						"' and legacy directory '" + nextItemDir.getAbsolutePath() + "' can't both exist at the same time.");
				}
			}
		}
		
		// if the directory doesn't presently exist at any of the potential locations then ensure it will be created in the
		// currently recommended location if create() or populate() are ever called
		if(itemDir == null)
		{
			itemDir = nodeItemLocators.get(0).getDir(sourceDir);
		}
		
		return itemDir;
	}
}
