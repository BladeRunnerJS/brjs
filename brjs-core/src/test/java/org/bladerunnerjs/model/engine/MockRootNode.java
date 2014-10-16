package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.memoization.FileModificationRegistry;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.memoization.MemoizedFileAccessor;
import org.bladerunnerjs.model.IO;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.MultipleNodesForPathException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.utility.ObserverList;


public class MockRootNode implements RootNode
{
	private FileModificationRegistry fileModificationRegistry = new FileModificationRegistry(new File("."));
	private MemoizedFileAccessor memoizedFileAccessor = new MemoizedFileAccessor(this);
	private IO io = new IO();
	
	@Override
	public Node parentNode()
	{
		return null;
	}

	@Override
	public File dir()
	{
		return null;
	}

	@Override
	public File file(String filePath)
	{
		return null;
	}
	
	@Override
	public File[] memoizedScopeFiles()
	{
		return null;
	}
	
	@Override
	public boolean dirExists()
	{
		return false;
	}
	
	@Override
	public boolean exists()
	{
		return false;
	}

	@Override
	public boolean containsFile(String filePath)
	{
		return false;
	}

	@Override
	public void create() throws ModelUpdateException
	{

	}

	@Override
	public void delete() throws ModelUpdateException
	{
	}

	@Override
	public File storageDir(String pluginName)
	{
		return null;
	}

	@Override
	public File storageFile(String pluginName, String filePath)
	{
		return null;
	}

	@Override
	public void discoverAllChildren()
	{
	}

	@Override
	public Logger logger(Class<?> clazz)
	{
		return new Logger()
		{

			@Override
			public String getName()
			{
				return null;
			}

			@Override
			public void error(String message, Object... params)
			{
			}

			@Override
			public void warn(String message, Object... params)
			{
			}

			@Override
			public void info(String message, Object... params)
			{
			}

			@Override
			public void debug(String message, Object... params)
			{
			}

			@Override
			public void println(String message, Object... params)
			{
			}

			@Override
			public void console(String message, Object... params)
			{	
			}
		};
	}

	@Override
	public boolean isRootDir(File dir)
	{
		return false;
	}

	@Override
	public Node locateFirstAncestorNode(File file)
	{
		return null;
	}
	
	@Override
	public <N extends Node> N locateAncestorNodeOfClass(File file, Class<N> nodeClass)
	{
		return null;
	}

	@Override
	public void registerNode(Node node)
	{
	}	
	
	@Override
	public void clearRegisteredNode(Node node)
	{
	}
	
	@Override
	public Node getRegisteredNode(File childPath)
	{
		return null;
	}
	
	@Override
	public List<Node> getRegisteredNodes(File childPath)
	{
		return null;
	}

	@Override
	public void addObserver(EventObserver observer)
	{
	}

	@Override
	public NodeProperties nodeProperties(String pluginName)
	{
		return null;
	}

	@Override
	public void ready()
	{
	}

	@Override
	public ObserverList getObservers()
	{
		return null;
	}

	@Override
	public RootNode root()
	{
		return null;
	}

	@Override
	public void addObserver(Class<? extends Event> eventType, EventObserver observer)
	{
	}

	@Override
	public void notifyObservers(Event event, Node notifyForNode)
	{
	}
	
	@Override
	public <N extends Node> N locateAncestorNodeOfClass(Node node, Class<N> nodeClass)
	{
		return null;
	}

	@Override
	public IO io() {
		return io;
	}

	@Override
	public Node getRegisteredNode(File childPath, Class<? extends Node> nodeClass) throws MultipleNodesForPathException
	{
		return null;
	}

	@Override
	public Node locateFirstAncestorNode(File file, Class<? extends Node> nodeClass)
	{
		return null;
	}

	@Override
	public boolean isNodeRegistered(Node node)
	{
		return false;
	}

	@Override
	public String getTypeName()
	{
		return this.getClass().getSimpleName();
	}

	@Override
	public FileModificationRegistry getFileModificationRegistry()
	{
		return fileModificationRegistry;
	}
	
	@Override
	public void updateLastModified()
	{		
	}
	
	@Override
	public MemoizedFile getMemoizedFile(File file)
	{
		return memoizedFileAccessor.getMemoizedFile(file);
	}
}
