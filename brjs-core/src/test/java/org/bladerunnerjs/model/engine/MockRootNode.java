package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.List;

import org.apache.commons.io.filefilter.FalseFileFilter;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.FileModificationRegistry;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedFileAccessor;
import org.bladerunnerjs.api.model.exception.MultipleNodesForPathException;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.plugin.Event;
import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.model.IO;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.ObserverList;


public class MockRootNode implements RootNode
{
	private FileModificationRegistry fileModificationRegistry = new FileModificationRegistry(new File("."), FalseFileFilter.INSTANCE);
	private MemoizedFileAccessor memoizedFileAccessor = new MemoizedFileAccessor(this);
	private IO io = new IO( FalseFileFilter.INSTANCE );
	
	@Override
	public Node parentNode()
	{
		return null;
	}

	@Override
	public MemoizedFile dir()
	{
		return null;
	}

	@Override
	public MemoizedFile file(String filePath)
	{
		return null;
	}
	
	@Override
	public MemoizedFile[] memoizedScopeFiles()
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
	public MemoizedFile storageDir(String pluginName)
	{
		return null;
	}

	@Override
	public MemoizedFile storageFile(String pluginName, String filePath)
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
	public Node locateFirstAncestorNode(MemoizedFile file)
	{
		return null;
	}
	
	@Override
	public <N extends Node> N locateAncestorNodeOfClass(File file, Class<N> nodeClass)
	{
		return null;
	}
	
	@Override
	public <N extends Node> N locateAncestorNodeOfClass(MemoizedFile file, Class<N> nodeClass)
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
	public Node getRegisteredNode(MemoizedFile childPath)
	{
		return null;
	}
	
	@Override
	public List<Node> getRegisteredNodes(MemoizedFile childPath)
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
	public Node getRegisteredNode(MemoizedFile childPath, Class<? extends Node> nodeClass) throws MultipleNodesForPathException
	{
		return null;
	}

	@Override
	public Node locateFirstAncestorNode(MemoizedFile file, Class<? extends Node> nodeClass)
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
	public void incrementFileVersion()
	{		
	}
	
	@Override
	public void incrementChildFileVersions()
	{		
	}
	
	@Override
	public MemoizedFile getMemoizedFile(File file)
	{
		return memoizedFileAccessor.getMemoizedFile(file);
	}

	@Override
	public MemoizedFile getMemoizedFile(File dir, String filePath)
	{
		return getMemoizedFile( new File(dir, filePath) );
	}
}
