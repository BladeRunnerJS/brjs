package org.bladerunnerjs.model.engine;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.exception.NodeAlreadyRegisteredException;

public class NodeItem<N extends Node>
{
	private final Node node;
	private final Class<N> nodeClass;
	private final List<NodeLocator> nodeItemLocators = new ArrayList<>();
	public N item;
	
	public NodeItem(Node node, Class<N> nodeClass, String subDirPath)
	{
		this.node = node;
		this.nodeClass = nodeClass;
		nodeItemLocators.add(new DirectoryNodeLocator(subDirPath));
	}
	
	public void addLegacyLocation(String subDirPath)
	{
		nodeItemLocators.add(new DirectoryNodeLocator(subDirPath));
	}
	
	public boolean itemExists()
	{
		return getNodeDir(node.dir()).exists();
	}
	
	public N item()
	{
		return item(true);
	}
	
	public N item(boolean registerNode)
	{
		if(item == null)
		{
			try
			{
				Constructor<N> classConstructor = nodeClass.getConstructor(RootNode.class, Node.class, File.class);
				item = classConstructor.newInstance(node.root(), node, getNodeDir(node.dir()));
				if (registerNode) {
					item.root().registerNode(item);
				}
			}
			catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
				NoSuchMethodException | SecurityException | NodeAlreadyRegisteredException e)
			{
				throw new RuntimeException(e);
			}
		}
		
		return item;
	}
	
	private File getNodeDir(File dir)
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
