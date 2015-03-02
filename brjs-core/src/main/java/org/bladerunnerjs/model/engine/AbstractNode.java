package org.bladerunnerjs.model.engine;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.events.NodeCreatedEvent;
import org.bladerunnerjs.api.model.events.NodeDeletedEvent;
import org.bladerunnerjs.api.model.events.NodeReadyEvent;
import org.bladerunnerjs.api.model.exception.modelupdate.DirectoryAlreadyExistsModelException;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.modelupdate.NoSuchDirectoryException;
import org.bladerunnerjs.api.plugin.Event;
import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.model.PluginProperties;
import org.bladerunnerjs.utility.FileUtils;
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
	private Map<String, NodeProperties> propertiesMap = new TreeMap<String,NodeProperties>();
	private Map<String, MemoizedFile> filesMap = new TreeMap<>();
	
	
	
	protected RootNode rootNode;
	private Node parent;
	protected File dir;
	private MemoizedFile memoizedDir;
	private MemoizedFile[] scopeFiles;
	
	public AbstractNode(RootNode rootNode, Node parent, File dir) {
		this.rootNode = rootNode;
		this.parent = parent;
		if (dir == null) throw new RuntimeException("dir must not be null");		
		this.dir = dir;
	}
	
	public AbstractNode() {
		this.rootNode = (RootNode) this;
	}
	
	protected void setNodeDir(File file) {
		this.dir = file;
	}
	
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
	public MemoizedFile dir()
	{
		if (memoizedDir == null) {
			memoizedDir = rootNode.getMemoizedFile(dir);
		}
		return memoizedDir;
	}
	
	@Override
	public MemoizedFile[] memoizedScopeFiles()
	{
		if (scopeFiles == null) {
			scopeFiles = new MemoizedFile[] {dir()};
		}
		return scopeFiles;
	}
	
	@Override
	public String getTypeName()
	{
		return this.getClass().getSimpleName();
	}
	
	@Override
	public boolean dirExists()
	{
		return dir().exists();
	}
	
	@Override
	public boolean exists()
	{
		return dirExists();
	}
	
	@Override
	public MemoizedFile file(String filePath)
	{
		MemoizedFile cachedFile = filesMap.get(filePath);
		if (cachedFile == null)
		{
			cachedFile = rootNode.getMemoizedFile(dir(), filePath);
			filesMap.put(filePath, cachedFile);
		}
		return cachedFile;
	}
	
	@Override
	public boolean containsFile(String filePath)
	{
		return new File(dir(), filePath).exists();
	}
	
	@Override
	public void create() throws InvalidNameException, ModelUpdateException
	{
		Logger logger = rootNode.logger(Node.class);
		
		try {
			if(dirExists()) throw new DirectoryAlreadyExistsModelException(this);
			if(this instanceof NamedNode) ((NamedNode) this).assertValidName();
			
			try {
				FileUtils.forceMkdir(dir());
				notifyObservers(new NodeCreatedEvent(), this);
				logger.debug(Messages.NODE_CREATED_LOG_MSG, getTypeName(), dir().getPath());
				
				incrementChildFileVersions();
			}
			catch(IOException e) {
				throw new ModelUpdateException(e);
			}
		}
		catch(Exception e) {
			logger.error(Messages.NODE_CREATION_FAILED_LOG_MSG, getTypeName(), dir().getPath());
			throw e;
		}
	}
	
	protected void createDefaultNode() throws InvalidNameException, ModelUpdateException
	{
		Logger logger = rootNode.logger(Node.class);
		
		try {
			if(this instanceof NamedNode) ((NamedNode) this).assertValidName();
			
			notifyObservers(new NodeCreatedEvent(), this);
			logger.debug(Messages.NODE_CREATED_LOG_MSG, getTypeName(), dir().getPath());
				
			incrementChildFileVersions();
		}
		catch(Exception e) {
			logger.error(Messages.NODE_CREATION_FAILED_LOG_MSG, getTypeName(), dir().getPath());
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
		Logger logger = rootNode.logger(Node.class);
		
		try {
			if(!dirExists()) throw new NoSuchDirectoryException(this);
			
			try {
				FileUtils.deleteDirectory(dir());
				logger.debug(Messages.NODE_DELETED_LOG_MSG, getTypeName(), dir().getPath());
				notifyObservers(new NodeDeletedEvent(), this);
				incrementFileVersion();
			}
			catch(IOException e) {
				throw new ModelUpdateException(e);
			}
		}
		catch(Exception e) {
			logger.error(Messages.NODE_DELETION_FAILED_LOG_MSG, getTypeName(), dir().getPath());
			throw e;
		}
	}
	
	@Override
	public MemoizedFile storageDir(String pluginName)
	{
		return rootNode.getMemoizedFile(rootNode.dir(), "generated/" + NodePathGenerator.generatePath(this) + "/" + pluginName);
	}
	
	@Override
	public MemoizedFile storageFile(String pluginName, String filePath)
	{
		return rootNode.getMemoizedFile(storageDir(pluginName), filePath);
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
				if(field.getType() == NodeList.class)
				{
					NodeList<Node> nodeList = (NodeList<Node>) field.get(this);
				
					discoverAllChildren(nodeList.list());
				}
				else if(field.getType() == NodeItem.class)
				{
					NodeItem<Node> nodeItem = (NodeItem<Node>) field.get(this);
					
					if(nodeItem.itemExists()) {
						List<Node> nodeItems = new ArrayList<>();
						nodeItems.add(nodeItem.item());
						discoverAllChildren(nodeItems);
					}
				}
			}
		}
		catch(RuntimeException e) {
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
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
		return getAllFields(new ArrayList<Field>(), type, Collections.emptyList());
	}
	
	private List<Field> getAllFields(List<Field> fields, Class<?> type, List<String> subclassFieldNames)
	{
		List<String> thisClassFieldNames = new ArrayList<>(subclassFieldNames);
		for (Field field: type.getDeclaredFields())
		{
			if (!subclassFieldNames.contains(field.getName())) {
    			if(!field.isAccessible())
    			{
    				field.setAccessible(true);
    			}
    			
    			fields.add(field);
    			thisClassFieldNames.add(field.getName());
			}
		}
		
		if (type.getSuperclass() != null)
		{
			return getAllFields(fields, type.getSuperclass(), thisClassFieldNames);
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
	
	@Override
	public String toString()
	{
		if (root() instanceof BRJS) { // check the type since root() is a TestRootNode in some tests
			return getTypeName()+", dir: " + root().dir().getRelativePath(dir());
		}
		return getTypeName()+", dir: " + dir().getPath();
	}
	
	@Override
	public void incrementFileVersion()
	{
    	dir().incrementFileVersion();
	}
	
	@Override
	public void incrementChildFileVersions()
	{
		dir().incrementChildFileVersions();
	}
}
