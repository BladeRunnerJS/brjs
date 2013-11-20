package org.bladerunnerjs.model.engine;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.core.console.ConsoleWriter;
import org.bladerunnerjs.core.console.PrintStreamConsoleWriter;
import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerFactory;
import org.bladerunnerjs.core.log.LoggerType;


public abstract class AbstractRootNode extends AbstractNode implements RootNode
{
	private Map<String, Node> nodeCache = new HashMap<>();
	private LoggerFactory loggerFactory;
	private ConsoleWriter consoleWriter;
	
	public AbstractRootNode(File dir, LoggerFactory loggerFactory, ConsoleWriter consoleWriter)
	{
		init(this, null, locateRootDir(dir));
		this.loggerFactory = loggerFactory;
		this.consoleWriter = consoleWriter;
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
	public void registerNode(Node node)
	{
		nodeCache.put(node.dir().getAbsolutePath(), node);
		try {
			nodeCache.put(node.dir().getCanonicalPath(), node);
		}
		catch (IOException ex)
		{
			root().logger(LoggerType.CORE, this.getClass() ).warn("Unable to get canonical path for dir %s, exception was: '%s'", dir(), ex);
		}
	}
	
	@Override
	public Node getRegisteredNode(File childPath)
	{
		return nodeCache.get(childPath.getAbsolutePath());
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
			String filePath = nextFile.getAbsolutePath();
			
			if(nodeCache.containsKey(filePath))
			{
				node = nodeCache.get(filePath);
			}
			else
			{
				nextFile = nextFile.getParentFile();
			}
		} while((node == null) && (nextFile != null));
		
		return node;
	}
	
}
