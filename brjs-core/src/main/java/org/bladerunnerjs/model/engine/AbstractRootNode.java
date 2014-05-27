package org.bladerunnerjs.model.engine;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;
import java.util.TreeMap;

import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.console.PrintStreamConsoleWriter;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.model.exception.NodeAlreadyRegisteredException;


public abstract class AbstractRootNode extends AbstractNode implements RootNode
{
	// TODO: remove this flag once we delete all old BladerRunner code
	public static boolean allowInvalidRootDirectories = true;
	
	private Map<String, Node> nodeCache = new TreeMap<>();
	private LoggerFactory loggerFactory;
	private ConsoleWriter consoleWriter;
	
	public AbstractRootNode(File dir, LoggerFactory loggerFactory, ConsoleWriter consoleWriter) throws InvalidSdkDirectoryException
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
		this.consoleWriter = consoleWriter;
		
		// TODO: we should never call registerInitializedNode() from a non-final class
		registerInitializedNode();
	}
	
	@Override
	public Logger logger(LoggerType type, Class<?> clazz)
	{
		return loggerFactory.getLogger(type, clazz);
	}
	
	@Override
	public ConsoleWriter getConsoleWriter()
	{
		return consoleWriter;
	}
	
	@Override
	public void setConsoleWriter(ConsoleWriter consoleWriter)
	{
		this.consoleWriter = consoleWriter;
	}
	
	public void setConsoleWriter(PrintStream printStream)
	{
		this.consoleWriter = new PrintStreamConsoleWriter(printStream);
	}
	
	@Override
	public void registerNode(Node node) throws NodeAlreadyRegisteredException
	{
		String normalizedPath = node.dir().getPath();
		
		if(nodeCache.containsKey(normalizedPath)) {
			throw new NodeAlreadyRegisteredException("A node has already been registered for path '" + normalizedPath + "'");
		}
		
		nodeCache.put(normalizedPath, node);
	}
	
	@Override
	public void clearRegisteredNode(Node node) {
		String normalizedPath = node.dir().getPath();
		nodeCache.remove(normalizedPath);
	}
	
	@Override
	public Node getRegisteredNode(File childPath)
	{
		return nodeCache.get(getNormalizedPath(childPath));
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
		Node firstCachedNode = locateFirstCachedNode(file);
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
		
		if (node.getClass() == nodeClass)
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
		
		return dir;
	}
	
	private Node locateFirstCachedNode(File file)
	{
		Node node = null;
		File nextFile = file;
		
		do
		{
			String normalizedFilePath = getNormalizedPath(nextFile);
			
			if(nodeCache.containsKey(normalizedFilePath))
			{
				node = nodeCache.get(normalizedFilePath);
			}
			else
			{
				nextFile = nextFile.getParentFile();
			}
		} while((node == null) && (nextFile != null));
		
		return node;
	}
}
