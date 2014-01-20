package org.bladerunnerjs.model.engine;

import java.io.File;

import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.utility.FileIterator;
import org.bladerunnerjs.utility.ObserverList;
import org.bladerunnerjs.utility.filemodification.PessimisticFileModificationService;


public class MockRootNode implements RootNode
{

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
	public boolean dirExists()
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
	public Logger logger(LoggerType type, Class<?> clazz)
	{
		return new Logger()
		{

			@Override
			public String getName()
			{
				return null;
			}

			@Override
			public void fatal(String message, Object... params)
			{
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
		};
	}

	@Override
	public ConsoleWriter getConsoleWriter()
	{
		return null;
	}

	@Override
	public void setConsoleWriter(ConsoleWriter consoleWriter)
	{
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
	public Node getRegisteredNode(File childPath)
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
	public FileIterator getFileIterator(File dir) {
		return new FileIterator(this, new PessimisticFileModificationService(), null);
	}
}
