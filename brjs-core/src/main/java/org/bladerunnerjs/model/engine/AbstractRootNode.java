package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.logging.LoggerFactory;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.events.NodeDiscoveredEvent;
import org.bladerunnerjs.api.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.api.model.exception.MultipleNodesForPathException;
import org.bladerunnerjs.api.model.exception.NodeAlreadyRegisteredException;


public abstract class AbstractRootNode extends AbstractNode implements RootNode
{
	// TODO: remove this flag once we delete all old BladerRunner code
	public static boolean allowInvalidRootDirectories = true;
	
	private Map<String, List<Node>> nodeCache = new TreeMap<>();
	protected LoggerFactory loggerFactory;
	
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
		
		setNodeDir(rootDir);
		this.loggerFactory = loggerFactory;
	}
	
	@Override
	public Logger logger(Class<?> clazz)
	{
		return loggerFactory.getLogger(clazz);
	}
	
	@Override
	public boolean isNodeRegistered(Node node) {
		Node registeredNode;
		try
		{
			registeredNode = getRegisteredNode(node.dir(), node.getClass());
			return registeredNode != null && registeredNode == node;
		}
		catch (MultipleNodesForPathException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	@Override
	public void registerNode(Node node) throws NodeAlreadyRegisteredException
	{
		List<Node> nodesForPath = getRegisteredNodes(node.dir());
		
		boolean nodeExistsForPath = findFirstNodeOfClass(nodesForPath, node.getClass()) != null;
		
		if (nodeExistsForPath) {
			throw new NodeAlreadyRegisteredException("A node of type '" + node.getTypeName() + 
					"' has already been registered for path '" + node.dir() + "'");
		}

		notifyObservers(new NodeDiscoveredEvent(), node);

		if (node.dir().exists()) {
			node.ready();
		}
		
		nodesForPath.add(node);
	}
	
	@Override
	public void clearRegisteredNode(Node node) {
		String normalizedPath = node.dir().getAbsolutePath();
		List<Node> nodesForPath = nodeCache.get(normalizedPath);
		nodesForPath.remove(node.getTypeName());
	}
	
	@Override
	public List<Node> getRegisteredNodes(MemoizedFile childPath)
	{
		String normalizedPath = childPath.getAbsolutePath();
		if (!nodeCache.containsKey(normalizedPath)) {
			nodeCache.put( normalizedPath, new LinkedList<>() );
		}
		return nodeCache.get(normalizedPath);
	}
	
	@Override
	public Node getRegisteredNode(MemoizedFile childPath) throws MultipleNodesForPathException
	{
		return getRegisteredNode(childPath, null);
	}
	
	@Override
	public Node getRegisteredNode(MemoizedFile childPath, Class<? extends Node> nodeClass) throws MultipleNodesForPathException
	{
		List<Node> nodes = getRegisteredNodes(childPath);
		if (nodes.size() == 0) {
			return null;
		}
		if (nodes.size() <= 1) {
			return nodes.get(0);
		}
		if (nodeClass != null) {
			return findFirstNodeOfClass(nodes, nodeClass);
		}
		throw new MultipleNodesForPathException(childPath, "getRegisteredNodes()");
	}
	
	@Override
	public Node locateFirstAncestorNode(MemoizedFile file)
	{
		return locateFirstAncestorNode(file, null);
	}
	
	@Override
	public Node locateFirstAncestorNode(MemoizedFile file, Class<? extends Node> nodeClass)
	{
		Node node = locateFirstCachedNode(file, nodeClass);
		
		if(node != null)
		{
			node.discoverAllChildren();
			node = locateFirstCachedNode(file, nodeClass);
		}
		
		return node;
	}
		
	@Override
	public <N extends Node> N locateAncestorNodeOfClass(File file, Class<N> nodeClass)
	{
		return locateAncestorNodeOfClass(getMemoizedFile(file), nodeClass);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <N extends Node> N locateAncestorNodeOfClass(MemoizedFile file, Class<N> nodeClass)
	{
		Node firstCachedNode = locateFirstCachedNode(file, nodeClass);
		Node node = null;
		
		if (firstCachedNode != null)
		{			
			node = locateExistentAncestorNodeOfClass(firstCachedNode, nodeClass);
			
			if(node == null)
			{
				node = locateExistentAncestorNodeOfClass(locateFirstCachedNode(file), nodeClass);
			}
		}
		
		if (node == null) {
			root().discoverAllChildren();
			node = locateFirstCachedNode(file, nodeClass);
		}
		
		return (N) findFirstNodeOfClass(Arrays.asList(node), nodeClass);
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
		
		return (dir == null) ? dir : dir.getAbsoluteFile();
	}
	
	private Node locateFirstCachedNode(MemoizedFile file) {
		return locateFirstCachedNode(file, null);
	}
	
	private Node locateFirstCachedNode(MemoizedFile file, Class<? extends Node> nodeClass)
	{
		MemoizedFile nextFile = file;
		
		do
		{	
			List<Node> nodesForFile = getRegisteredNodes(nextFile);
			
			Node locatedNode = findFirstNodeOfClass(nodesForFile, nodeClass);
			
			if (locatedNode != null) {
				return locatedNode;
			} else if (nodesForFile.size() > 0) {
				return nodesForFile.get(0);
			}
			
			nextFile = nextFile.getParentFile();
		} while(nextFile != null);
		
		return null;
	}
	
	private Node findFirstNodeOfClass(List<Node> nodes, Class<? extends Node> nodeClass) {
		for (Node n : nodes) {
			if ( nodeClass == null || (n != null && nodeClass.isAssignableFrom(n.getClass())) ) {
				return n;
			}
		}
		return null;
	}
	
}
