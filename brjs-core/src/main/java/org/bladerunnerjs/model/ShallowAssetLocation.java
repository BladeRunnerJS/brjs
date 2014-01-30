package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.InvalidRequirePathException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.UnresolvableRelativeRequirePathException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.utility.JsStyleUtility;
import org.bladerunnerjs.utility.RelativePathUtility;

public class ShallowAssetLocation extends InstantiatedBRJSNode implements AssetLocation {
	protected AssetContainer assetContainer;
	private AliasDefinitionsFile aliasDefinitionsFile;
	private Map<String, SourceModule> sourceModules = new HashMap<>();
	
	public ShallowAssetLocation(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, parent, dir);
		this.assetContainer = (AssetContainer) parent;
	}
	
	@Override
	public File dir() {
		return super.dir();
	}
	
	@Override
	public String getJsStyle() {
		return JsStyleUtility.getJsStyle(dir());
	}
	
	@Override
	public String requirePrefix() throws RequirePathException {
		String relativeRequirePath = RelativePathUtility.get(assetContainer.dir(), dir());
		
		return assetContainer.requirePrefix() + "/" + relativeRequirePath;
	}
	
	@Override
	public String getNamespace() throws RequirePathException {
		return requirePrefix().replace("/", ".");
	}
	
	@Override
	public List<SourceModule> getSourceModules()
	{
		List<SourceModule> sourceModules = new ArrayList<SourceModule>();
		
		for (AssetContainer assetContainer : getAssetContainer().getApp().getAllAssetContainers())
		{
			sourceModules.addAll( assetContainer.sourceModules() );
		}
		
		return sourceModules;
	}
	
	@Override
	public SourceModule getSourceModuleWithRequirePath(String requirePath) throws RequirePathException
	{
		String canonicalRequirePath = canonicaliseRequirePath(requirePrefix(), requirePath);

		SourceModule sourceModule;
		if (getAssetContainer() instanceof TestPack)
		{
			TestPack testPack = (TestPack) getAssetContainer();
			sourceModule = findSourceModuleWithRequirePath(testPack.getAssetContainers(), canonicalRequirePath);
		}
		else if (!sourceModules.containsKey(requirePath)) 
		{
			sourceModule = findSourceModuleWithRequirePath(getAssetContainer().getApp().getAllAssetContainers(), canonicalRequirePath);
			if (sourceModule != null)
			{
				sourceModules.put(requirePath, sourceModule);
			}
		}
		else
		{
			sourceModule = sourceModules.get(requirePath);
		}
		
		if (sourceModule != null)
		{
			return sourceModule;
		}
		
		
		
		throw new InvalidRequirePathException("Unable to find SourceModule for require path '"+requirePath+"'. It either does not exist or it is outside of the scope for this request.");
	}

	private SourceModule findSourceModuleWithRequirePath(List<AssetContainer> assetContainers, String requirePath)
	{
		for (AssetContainer assetContainer : assetContainers)
		{
			for (SourceModule sourceModule : assetContainer.sourceModules())
			{
				if (sourceModule.getRequirePath().equals(requirePath))
				{
					sourceModules.put(requirePath, sourceModule);
					return sourceModule;
				}
			}
		}
		return null;
	}
	
	
	@Override
	public AliasDefinitionsFile aliasDefinitionsFile() {		
		if(aliasDefinitionsFile == null) {
			aliasDefinitionsFile = new AliasDefinitionsFile(assetContainer, dir(), "aliasDefinitions.xml");
		}
		
		return aliasDefinitionsFile;
	}
		
	@Override
	public List<LinkedAsset> seedResources() {
		List<LinkedAsset> seedResources = new LinkedList<LinkedAsset>();
			
		for(AssetPlugin assetPlugin : root().plugins().assetProducers()) {
			seedResources.addAll(assetPlugin.getLinkedAssets(this));
		}
		
		return seedResources;
	}
	
	
	@Override
	public List<LinkedAsset> seedResources(String fileExtension) {
		List<LinkedAsset> typedSeedResources = new ArrayList<>();
		
		for(LinkedAsset seedResource : seedResources()) {
			if(seedResource.getAssetName().endsWith("." + fileExtension)) {
				typedSeedResources.add(seedResource);
			}
		}
		
		return typedSeedResources;
	}
	
	@Override
	public List<Asset> bundleResources(String fileExtension) {
		List<Asset> bundleResources = new LinkedList<Asset>();
		
		for (AssetPlugin assetPlugin : root().plugins().assetProducers()) {
			for (Asset asset: assetPlugin.getAssets(this))
			{
				if (asset.getAssetName().endsWith("." + fileExtension)) 
				{
					bundleResources.add(asset);
				}
			}
		}
		return bundleResources;
	}

	@Override
	public AssetContainer getAssetContainer()
	{
		return assetContainer;
	}

	@Override
	public List<AssetLocation> getDependentAssetLocations()
	{
    	return new ArrayList<>();
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException {
		// do nothing
	}
	
	private String canonicaliseRequirePath(String requirePrefix, String requirePath) throws RequirePathException
	{
		List<String> requirePrefixParts = new LinkedList<String>( Arrays.asList(requirePrefix.split("/")) );
		List<String> requirePathParts = new LinkedList<String>( Arrays.asList(requirePath.split("/")) );
		
		if (!requirePath.contains("../") && !requirePath.contains("./"))
		{
			return requirePath;
		}
		
		Iterator<String> requirePathPartsIterator = requirePathParts.iterator();
		while(requirePathPartsIterator.hasNext())
		{
			String pathPart = requirePathPartsIterator.next();
			switch (pathPart)
			{
				case ".":
					requirePathPartsIterator.remove();
					break;
				case "..":
					requirePathPartsIterator.remove();
					if (requirePrefixParts.size() > 0)
					{
						requirePrefixParts.remove( requirePrefixParts.size()-1 );						
					}
					else
					{
						String msg = String.format("Unable to continue up to parent require path, no more parents remaining. Require path of container was '%s', relative require path was '%s'", requirePrefix, requirePath);
						throw new UnresolvableRelativeRequirePathException(msg);
					}
					break;
				default:
					break;
			}
		}
		return StringUtils.join(requirePrefixParts, "/") + "/" + StringUtils.join(requirePathParts, "/");
	}
}
