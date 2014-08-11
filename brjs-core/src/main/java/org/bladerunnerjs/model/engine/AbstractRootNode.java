package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.model.events.NodeDiscoveredEvent;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.model.exception.MultipleNodesForPathException;
import org.bladerunnerjs.model.exception.NodeAlreadyRegisteredException;


public abstract class AbstractRootNode extends AbstractNode implements RootNode
{
	// TODO: remove this flag once we delete all old BladerRunner code
	public static boolean allowInvalidRootDirectories = true;
	
	private Map<String, Map<String, Node>> nodeCache = new TreeMap<>();
	private LoggerFactory loggerFactory;
	
	public AbstractRootNode(File dir, LoggerFactory loggerFactory) throws InvalidSdkDirectoryException
	{
		super();
		
		File rootDir = locateRootDir(dir);
		
		if(rootDir == null) {
			if(!allowInvalidRootDirectories) {
				throw new InvalidSdkDirectoryException("'" + dir.getPath() + "' is not a valid SDK directory");
			}
			
			rootDir = dir;
		}
		
		this.dir = new File(getNormalizedPath(rootDir));
		this.loggerFactory = loggerFactory;
		
		try
		{
			registerNode(this);
		}
		catch (NodeAlreadyRegisteredException e)
		{
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public Logger logger(Class<?> clazz)
	{
		return loggerFactory.getLogger(clazz);
	}
	
	@Override
	public void registerNode(Node node) throws NodeAlreadyRegisteredException
	{
		Map<String, Node> nodesForPath = getRegisteredNodesMap(node.dir());
		
		if (nodesForPath.containsKey(node.getClass().getSimpleName())) {
			throw new NodeAlreadyRegisteredException("A node of type '" + node.getClass().getSimpleName() + 
					"' has already been registered for path '" + getNormalizedPath(node.dir()) + "'");
		}

		notifyObservers(new NodeDiscoveredEvent(), node);

		if (node.dir().exists()) {
			node.ready();
		}
		
		nodesForPath.put(node.getClass().getSimpleName(), node);
	}
	
	@Override
	public void clearRegisteredNode(Node node) {
		String normalizedPath = getNormalizedPath(node.dir());
		Map<String, Node> nodesForPath = nodeCache.get(normalizedPath);
		nodesForPath.remove(node.getClass().getSimpleName());
	}
	
	@Override
	public List<Node> getRegisteredNodes(File childPath)
	{
		return new ArrayList<>( getRegisteredNodesMap(childPath).values() );
	}
	
	@Override
	public Node getRegisteredNode(File childPath) throws MultipleNodesForPathException
	{
		List<Node> nodes = getRegisteredNodes(childPath);
		if (nodes.size() == 0) {
			return null;
		}
		if (nodes.size() <= 1) {
			return nodes.get(0);
		}
		throw new MultipleNodesForPathException(childPath, "getRegisteredNodes()");
	}
	
	@Override
	public Node locateFirstAncestorNode(File file)
	{
		Node node = locateFirstCachedNode(file);
		
		if(node != null)
		{
			node.discoverAllChildren();
			node = locateFirstCachedNode(file);
		}
		
		return node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <N extends Node> N locateAncestorNodeOfClass(File file, Class<N> nodeClass)
	{
		Node firstCachedNode = locateFirstCachedNode(file, nodeClass);
		Node node = null;
		
		if(firstCachedNode != null)
		{
			node = locateExistentAncestorNodeOfClass(firstCachedNode, nodeClass);
			
			if(node == null)
			{
				firstCachedNode.discoverAllChildren();
				node = locateExistentAncestorNodeOfClass(locateFirstCachedNode(file), nodeClass);
			}
		}
		
		return (N) node;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <N extends Node> N locateAncestorNodeOfClass(Node node, Class<N> nodeClass) {
		if (node == null)
		{
			return null;
		}
		
		if (nodeClass.isAssignableFrom(node.getClass()))
		{
			return (N) node;
		}
		
		return locateAncestorNodeOfClass(node.parentNode(), nodeClass);
	}
	
	
	private Map<String, Node> getRegisteredNodesMap(File childPath)
	{
		String normalizedPath = getNormalizedPath(childPath);
		if (!nodeCache.containsKey(normalizedPath)) {
			nodeCache.put( normalizedPath, new TreeMap<>() );
		}
		return nodeCache.get(normalizedPath);
	}
	
	@SuppressWarnings("unchecked")
	private <N extends Node> N locateExistentAncestorNodeOfClass(Node node, Class<N> nodeClass)
	{
		if(nodeClass != null)
		{
			while((node != null) && !nodeClass.isInstance(node))
			{
				node = node.parentNode();
			}
		}

		return (N) node;
	}
	
	private File locateRootDir(File dir)
	{
		while((dir != null) && (isRootDir(dir) == false))
		{
			dir = dir.getParentFile();
		}
		
		return dir;
	}
	
	private Node locateFirstCachedNode(File file) {
		return locateFirstCachedNode(file, null);
	}
	
	private Node locateFirstCachedNode(File file, Class<? extends Node> nodeClass)
	{
		Node node = null;
		File nextFile = file;
		
		do
		{	
			Map<String, Node> nodesForFile = getRegisteredNodesMap(nextFile);
			if (nodeClass != null && nodesForFile.containsKey(nodeClass.getSimpleName())) {
				node = nodesForFile.get(nodeClass.getSimpleName());
			} else if (nodesForFile.size() >= 1) {
				node = nodesForFile.values().toArray(new Node[0])[0];				
			} else {
				nextFile = nextFile.getParentFile();
			}
		} while((node == null) && (nextFile != null));
		
		return node;
	}
}
