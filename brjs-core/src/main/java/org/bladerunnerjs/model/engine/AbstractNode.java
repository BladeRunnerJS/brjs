package org.bladerunnerjs.model.engine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.PluginProperties;
import org.bladerunnerjs.model.events.NodeReadyEvent;
import org.bladerunnerjs.model.exception.modelupdate.DirectoryAlreadyExistsException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.modelupdate.NoSuchDirectoryException;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.utility.NodePathGenerator;
import org.bladerunnerjs.utility.ObserverList;


public abstract class AbstractNode implements Node
{
	public class Messages {
		public static final String NODE_CREATED_LOG_MSG = "%s created at '%s'";
		public static final String NODE_CREATION_FAILED_LOG_MSG = "creation of %s at '%s' failed";
		public static final String NODE_DELETED_LOG_MSG = "%s deleted at '%s'";
		public static final String NODE_DELETION_FAILED_LOG_MSG = "deletion of %s at '%s' failed";
	}
	
	private ObserverList observers = new ObserverList();
	private Map<String, NodeProperties> propertiesMap = new HashMap<String,NodeProperties>();
	
	private RootNode rootNode;
	private Node parent;
	private File dir;
	
	@Override
	public RootNode root()
	{
		return rootNode;
	}
	
	@Override
	public Node parentNode()
	{
		return parent;
	}
	
	@Override
	public File dir()
	{
		return dir;
	}
	
	@Override
	public boolean dirExists()
	{
		return (dir == null) ? false : dir.exists();
	}
	
	@Override
	public File file(String filePath)
	{
		return new File(dir, filePath);
	}
	
	@Override
	public boolean containsFile(String filePath)
	{
		return (dir == null) ? false : new File(dir, filePath).exists();
	}
	
	@Override
	public void create() throws InvalidNameException, ModelUpdateException
	{
		Logger logger = rootNode.logger(LoggerType.CORE, Node.class);
		
		try {
			if(dirExists()) throw new DirectoryAlreadyExistsException(this);
			if(this instanceof NamedNode) ((NamedNode) this).assertValidName();
			
			try {
				FileUtils.forceMkdir(dir);
				logger.debug(Messages.NODE_CREATED_LOG_MSG, getClass().getSimpleName(), dir().getPath());
			}
			catch(IOException e) {
				throw new ModelUpdateException(e);
			}
		}
		catch(Exception e) {
			logger.error(Messages.NODE_CREATION_FAILED_LOG_MSG, getClass().getSimpleName(), dir().getPath());
			throw e;
		}
	}
	
	@Override
	public void ready()
	{
		notifyObservers(new NodeReadyEvent(), this);
	}
	
	@Override
	public void delete() throws ModelUpdateException
	{
		Logger logger = rootNode.logger(LoggerType.CORE, Node.class);
		
		try {
			if(!dirExists()) throw new NoSuchDirectoryException(this);
			
			try {
				FileUtils.deleteDirectory(dir);
				logger.debug(Messages.NODE_DELETED_LOG_MSG, getClass().getSimpleName(), dir.getAbsolutePath());
			}
			catch(IOException e) {
				throw new ModelUpdateException(e);
			}
		}
		catch(Exception e) {
			logger.error(Messages.NODE_DELETION_FAILED_LOG_MSG, getClass().getSimpleName(), dir().getPath());
			throw e;
		}
	}
	
	@Override
	public File storageDir(String pluginName)
	{
		return new File(rootNode.dir(), "generated/" + NodePathGenerator.generatePath(this) + "/" + pluginName);
	}
	
	@Override
	public File storageFile(String pluginName, String filePath)
	{
		return new File(storageDir(pluginName), filePath);
	}
	
	@Override
	public NodeProperties nodeProperties(String pluginName)
	{
		NodeProperties properties = propertiesMap.get(pluginName);
		if (properties == null)
		{
			properties = new PluginProperties(this, pluginName);
			propertiesMap.put(pluginName, properties);
		}
		return properties;
	}
	
	@Override
	public void addObserver(EventObserver observer)
	{
		getObservers().add(Event.class, observer);
	}
	
	@Override
	public void addObserver(Class<? extends Event> eventType, EventObserver observer)
	{
		getObservers().add(eventType, observer);
	}
	
	@Override
	public ObserverList getObservers()
	{
		return observers;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void discoverAllChildren()
	{
		try
		{
			for(Field field : getAllFields(getClass()))
			{
				if(field.getType() == NodeMap.class)
				{
					discoverAllChildren(children((NodeMap<Node>) field.get(this)));
				}
				else if(field.getType() == NodeItem.class)
				{
					discoverAllChildren(items((NodeItem<Node>) field.get(this)));
				}
			}
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	protected void init(RootNode rootNode, Node parent, File dir)
	{
		this.rootNode = rootNode;
		this.parent = parent;
		this.dir = dir;
		
		if(dir != null)
		{
			rootNode.registerNode(this);
			
			if (dir.exists())
			{
				ready();
			}
		}
	}
	
	protected <N extends Node> List<N> children(NodeMap<N> children)
	{
		List<N> childList = new ArrayList<>();
		List<String> locatorNames = children.getLocatorNames(dir);
		
		for(String locatorName : locatorNames)
		{
			childList.add(child(children, locatorName));
		}
		
		return childList;
	}
	
	@SuppressWarnings("unchecked")
	protected <N extends Node> N child(NodeMap<N> children, String childName)
	{
		if(!children.nodes.containsKey(childName))
		{
			File childPath = children.getDir(dir, childName);
			N child = (N) rootNode.getRegisteredNode(childPath);
			
			if(child == null)
			{
				child = (N) NodeCreator.createNode(rootNode, this, childPath, childName, children.nodeClass);
			}
			
			children.nodes.put(childName, child);
		}
		
		return children.nodes.get(childName);
	}
	
	protected <N extends Node> List<N> items(NodeItem<N> nodeItem)
	{
		File itemDir = nodeItem.getItemDir(dir);
		List<N> itemNodes = new ArrayList<>();
		
		if(itemDir.exists())
		{
			itemNodes.add(item(nodeItem));
		}
		
		return itemNodes;
	}
	
	protected <N extends Node> N item(NodeItem<N> nodeItem)
	{
		if(nodeItem.item == null)
		{
			try
			{
				Constructor<N> classConstructor = nodeItem.nodeClass.getConstructor(RootNode.class, Node.class, File.class);
				nodeItem.item = classConstructor.newInstance(rootNode, this, nodeItem.getItemDir(dir));
			}
			catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException |
				NoSuchMethodException | SecurityException e)
			{
				throw new RuntimeException(e);
			}
		}
		
		return nodeItem.item;
	}
	
	private void discoverAllChildren(List<Node> nodes)
	{
		for(Node node : nodes)
		{
			node.discoverAllChildren();
		}
	}
	
	private List<Field> getAllFields(Class<?> type)
	{
		return getAllFields(new ArrayList<Field>(), type);
	}
	
	private List<Field> getAllFields(List<Field> fields, Class<?> type)
	{
		for (Field field: type.getDeclaredFields())
		{
			if(!field.isAccessible())
			{
				field.setAccessible(true);
			}
			
			fields.add(field);
		}
		
		if (type.getSuperclass() != null)
		{
			fields = getAllFields(fields, type.getSuperclass());
		}
		
		return fields;
	}
	
	@Override
	public void notifyObservers(Event event, Node notifyForNode)
	{ 
		notifyObservers(event, notifyForNode, this);
	}
	
	private void notifyObservers(Event event, Node notifyForNode, Node node)
	{
		if (node == null)
		{
			return;
		}
		ObserverList observers = node.getObservers();
		if (observers != null) { observers.eventEmitted(event, notifyForNode); }
		notifyObservers(event, notifyForNode, node.parentNode());
	}
}
