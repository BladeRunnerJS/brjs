package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.api.plugin.Event;
import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeProperties;


public class LocaleForwarderAspectWrapper implements BundlableNode
{
	
	private Aspect aspect;

	public LocaleForwarderAspectWrapper(Aspect aspect)
	{
		this.aspect = aspect;
	}

	@Override
	public Node parentNode()
	{
		return aspect.parentNode();
	}

	@Override
	public MemoizedFile dir()
	{
		return aspect.dir();
	}

	@Override
	public MemoizedFile file(String filePath)
	{
		return aspect.file(filePath);
	}

	@Override
	public MemoizedFile[] memoizedScopeFiles()
	{
		return aspect.memoizedScopeFiles();
	}

	@Override
	public String getTypeName()
	{
		return aspect.getTypeName();
	}

	@Override
	public boolean dirExists()
	{
		return aspect.dirExists();
	}

	@Override
	public boolean exists()
	{
		return aspect.exists();
	}

	@Override
	public boolean containsFile(String filePath)
	{
		return aspect.containsFile(filePath);
	}

	@Override
	public void create() throws InvalidNameException, ModelUpdateException
	{
		aspect.create();
	}

	@Override
	public void ready()
	{
		aspect.ready();
	}

	@Override
	public void delete() throws ModelUpdateException
	{
		aspect.delete();
	}

	@Override
	public MemoizedFile storageDir(String pluginName)
	{
		return aspect.storageDir(pluginName);
	}

	@Override
	public MemoizedFile storageFile(String pluginName, String filePath)
	{
		return aspect.storageFile(pluginName, filePath);
	}

	@Override
	public NodeProperties nodeProperties(String pluginName)
	{
		return aspect.nodeProperties(pluginName);
	}

	@Override
	public void addObserver(EventObserver observer)
	{
		aspect.addObserver(observer);
	}

	@Override
	public void addObserver(Class<? extends Event> eventType, EventObserver observer)
	{
		aspect.addObserver(eventType, observer);
	}

	@Override
	public void notifyObservers(Event event, Node notifyForNode)
	{
		aspect.notifyObservers(event, notifyForNode);
	}

	@Override
	public ObserverList getObservers()
	{
		return aspect.getObservers();
	}

	@Override
	public void discoverAllChildren()
	{
		aspect.discoverAllChildren();
	}

	@Override
	public void incrementFileVersion()
	{
		aspect.incrementFileVersion();
	}

	@Override
	public void incrementChildFileVersions()
	{
		aspect.incrementChildFileVersions();
	}

	@Override
	public App app()
	{
		return aspect.app();
	}

	@Override
	public String requirePrefix()
	{
		return aspect.requirePrefix();
	}

	@Override
	public boolean isNamespaceEnforced()
	{
		return aspect.isNamespaceEnforced();
	}

	@Override
	public Set<Asset> assets()
	{
		return aspect.assets();
	}

	@Override
	public Asset asset(String requirePath)
	{
		return aspect.asset(requirePath);
	}

	@Override
	public String canonicaliseRequirePath(Asset asset, String requirePath) throws RequirePathException
	{
		return aspect.canonicaliseRequirePath(asset, requirePath);
	}

	@Override
	public List<AssetContainer> scopeAssetContainers()
	{
		return aspect.scopeAssetContainers();
	}

	@Override
	public BRJS root()
	{
		return aspect.root();
	}

	@Override
	public void populate(String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException
	{
		aspect.populate(templateGroup);
	}

	@Override
	public String getTemplateName()
	{
		return aspect.getTemplateName();
	}

	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
		aspect.addTemplateTransformations(transformations);
	}

	@Override
	public LinkedAsset getLinkedAsset(String requirePath) throws RequirePathException
	{
		return aspect.getLinkedAsset(requirePath);
	}

	@Override
	public List<LinkedAsset> seedAssets()
	{
		List<LinkedAsset> seedAssets = new ArrayList<>();
		for (Asset asset : aspect.app().jsLib("br-locale").assets()) {
			if (asset instanceof LinkedAsset) {
				seedAssets.add( (LinkedAsset) asset );
			}
		}
		return seedAssets;
	}

	@Override
	public BundleSet getBundleSet() throws ModelOperationException
	{
		return aspect.getBundleSet();
	}

	@Override
	public ResponseContent handleLogicalRequest(String logicalRequestPath, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException
	{
		return aspect.handleLogicalRequest(logicalRequestPath, contentAccessor, version);
	}

	@Override
	public List<Asset> assets(Asset asset, List<String> requirePaths) throws RequirePathException
	{
		return aspect.assets(asset, requirePaths);
	}

}
