package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.core.plugin.Event;
import org.bladerunnerjs.core.plugin.EventObserver;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.utility.ObserverList;


public class AppJsLibWrapper implements JsLib
{

	private App jsLibApp;
	private JsLib wrappedJsLib;
	
	public AppJsLibWrapper(App jsLibApp, JsLib jsLib)
	{
		this.jsLibApp = jsLibApp;
		this.wrappedJsLib = jsLib;
	}

	@Override
	public App getApp()
	{
		return jsLibApp;
	}
	
	public JsLib getWrappedLib()
	{
		return wrappedJsLib;
	}

	@Override
	public String namespace()
	{
		return wrappedJsLib.namespace();
	}

	@Override
	public String requirePrefix()
	{
		return wrappedJsLib.requirePrefix();
	}

	@Override
	public List<SourceFile> sourceFiles()
	{
		return wrappedJsLib.sourceFiles();
	}

	@Override
	public SourceFile sourceFile(String requirePath)
	{
		return wrappedJsLib.sourceFile(requirePath);
	}

	@Override
	public AssetLocation src()
	{
		return wrappedJsLib.src();
	}

	@Override
	public AssetLocation resources()
	{
		return wrappedJsLib.resources();
	}

	@Override
	public List<AssetLocation> getAllAssetLocations()
	{
		return wrappedJsLib.getAllAssetLocations();
	}

	@Override
	public BRJS root()
	{
		return wrappedJsLib.root();
	}

	@Override
	public void populate() throws InvalidNameException, ModelUpdateException
	{
		wrappedJsLib.populate();
	}

	@Override
	public String getTemplateName()
	{
		return wrappedJsLib.getTemplateName();
	}

	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
		wrappedJsLib.addTemplateTransformations(transformations);
	}

	@Override
	public Node parentNode()
	{
		return wrappedJsLib.parentNode();
	}

	@Override
	public File dir()
	{
		return wrappedJsLib.dir();
	}

	@Override
	public File file(String filePath)
	{
		return wrappedJsLib.file(filePath);
	}

	@Override
	public boolean dirExists()
	{
		return wrappedJsLib.dirExists();
	}

	@Override
	public boolean containsFile(String filePath)
	{
		return wrappedJsLib.containsFile(filePath);
	}

	@Override
	public void create() throws InvalidNameException, ModelUpdateException
	{
		wrappedJsLib.create();
	}

	@Override
	public void ready()
	{
		wrappedJsLib.ready();
	}

	@Override
	public void delete() throws ModelUpdateException
	{
		wrappedJsLib.delete();
	}

	@Override
	public File storageDir(String pluginName)
	{
		return wrappedJsLib.storageDir(pluginName);
	}

	@Override
	public File storageFile(String pluginName, String filePath)
	{
		return wrappedJsLib.storageFile(pluginName, filePath);
	}

	@Override
	public NodeProperties nodeProperties(String pluginName)
	{
		return wrappedJsLib.nodeProperties(pluginName);
	}

	@Override
	public void addObserver(EventObserver observer)
	{
		wrappedJsLib.addObserver(observer);
	}

	@Override
	public void addObserver(Class<? extends Event> eventType, EventObserver observer)
	{
		wrappedJsLib.addObserver(eventType, observer);
	}

	@Override
	public void notifyObservers(Event event, Node notifyForNode)
	{
		wrappedJsLib.notifyObservers(event, notifyForNode);
	}

	@Override
	public ObserverList getObservers()
	{
		return wrappedJsLib.getObservers();
	}

	@Override
	public void discoverAllChildren()
	{
		wrappedJsLib.discoverAllChildren();
	}

	@Override
	public String getName()
	{
		return wrappedJsLib.getName();
	}

	@Override
	public boolean isValidName()
	{
		return wrappedJsLib.isValidName();
	}

	@Override
	public void assertValidName() throws InvalidNameException
	{
		wrappedJsLib.assertValidName();
	}

	@Override
	public void populate(String libNamespace) throws InvalidNameException, ModelUpdateException
	{
		wrappedJsLib.populate(libNamespace);
	}
	
}
