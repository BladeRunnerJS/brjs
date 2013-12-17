package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;
import org.bladerunnerjs.utility.EmptyTrieKeyException;
import org.bladerunnerjs.utility.ObserverList;
import org.bladerunnerjs.utility.Trie;
import org.bladerunnerjs.utility.TrieKeyAlreadyExistsException;


public class CachedAssetLocation implements AssetLocation
{
	
	private AssetLocation assetLocation;
	private List<File> files;
	private String jsStyle;
	private List<LinkedAsset> seedResources;
	private Trie<List<LinkedAsset>> seedResourcesForExtension = new Trie<>();
	private Trie<List<Asset>> bundleResourcesForExtension = new Trie<>();
	
	
	public CachedAssetLocation(AssetLocation assetLocation)
	{
		this.assetLocation = assetLocation;
	}

	@Override
	public BRJS root()
	{
		return assetLocation.root();
	}

	@Override
	public void populate() throws InvalidNameException, ModelUpdateException
	{
		assetLocation.populate();
	}

	@Override
	public String getTemplateName()
	{
		return assetLocation.getTemplateName();
	}

	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
		assetLocation.addTemplateTransformations(transformations);
	}

	@Override
	public Node parentNode()
	{
		return assetLocation.parentNode();
	}

	@Override
	public File dir()
	{
		return assetLocation.dir();
	}

	@Override
	public File file(String filePath)
	{
		return assetLocation.file(filePath);
	}

	@Override
	public boolean dirExists()
	{
		return assetLocation.dirExists();
	}

	@Override
	public boolean containsFile(String filePath)
	{
		return assetLocation.containsFile(filePath);
	}

	@Override
	public void create() throws InvalidNameException, ModelUpdateException
	{
		assetLocation.create();
	}

	@Override
	public void ready()
	{
		assetLocation.ready();
	}

	@Override
	public void delete() throws ModelUpdateException
	{
		assetLocation.delete();
	}

	@Override
	public File storageDir(String pluginName)
	{
		return assetLocation.storageDir(pluginName);
	}

	@Override
	public File storageFile(String pluginName, String filePath)
	{
		return assetLocation.storageFile(pluginName,  filePath);
	}

	@Override
	public NodeProperties nodeProperties(String pluginName)
	{
		return assetLocation.nodeProperties(pluginName);
	}

	@Override
	public void addObserver(EventObserver observer)
	{
		assetLocation.addObserver(observer);
	}

	@Override
	public void addObserver(Class<? extends Event> eventType, EventObserver observer)
	{
		assetLocation.addObserver(eventType, observer);
	}

	@Override
	public void notifyObservers(Event event, Node notifyForNode)
	{
		assetLocation.notifyObservers(event, notifyForNode);
	}

	@Override
	public ObserverList getObservers()
	{
		return assetLocation.getObservers();
	}

	@Override
	public void discoverAllChildren()
	{
		assetLocation.discoverAllChildren();
	}

	@Override
	public String getJsStyle()
	{
		if (jsStyle == null)
		{
			jsStyle = assetLocation.getJsStyle();
		}
		return jsStyle;
	}

	@Override
	public String requirePrefix() throws RequirePathException
	{
		return assetLocation.requirePrefix();
	}

	@Override
	public AliasDefinitionsFile aliasDefinitionsFile()
	{
		return assetLocation.aliasDefinitionsFile();
	}

	@Override
	public List<LinkedAsset> seedResources()
	{
		if (seedResources == null)
		{
			seedResources = assetLocation.seedResources();
		}
		return seedResources;
	}

	@Override
	public List<LinkedAsset> seedResources(String fileExtension)
	{
		if (seedResourcesForExtension.get(fileExtension) == null)
		{
			try
			{
				seedResourcesForExtension.add(fileExtension, assetLocation.seedResources(fileExtension));
			}
			catch (EmptyTrieKeyException | TrieKeyAlreadyExistsException e)
			{
				return seedResourcesForExtension.get(fileExtension);				
			}
		}
		return seedResourcesForExtension.get(fileExtension);
	}

	@Override
	public List<Asset> bundleResources(String fileExtension)
	{
		if (bundleResourcesForExtension.get(fileExtension) == null)
		{
			try
			{
				bundleResourcesForExtension.add(fileExtension, assetLocation.bundleResources(fileExtension));
			}
			catch (EmptyTrieKeyException | TrieKeyAlreadyExistsException e)
			{
				return bundleResourcesForExtension.get(fileExtension);				
			}
		}
		return bundleResourcesForExtension.get(fileExtension);
	}

	@Override
	public AssetContainer getAssetContainer()
	{
		return assetLocation.getAssetContainer();
	}

	@Override
	public List<AssetLocation> getDependentAssetLocations()
	{
		return assetLocation.getDependentAssetLocations();
	}

	@Override
	public List<File> getFiles()
	{
		if (files == null)
		{
			files = assetLocation.getFiles();
		}
		return files;
	}
	
	public AssetLocation getWrappedAssetLocation()
	{
		return assetLocation;
	}

}
