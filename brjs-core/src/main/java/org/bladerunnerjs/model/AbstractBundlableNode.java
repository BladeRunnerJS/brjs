package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasException;
import org.bladerunnerjs.aliasing.UnresolvableAliasException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.AmbiguousRequirePathException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.plugin.AssetLocationPlugin;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.utility.BundleSetRequestHandler;

public abstract class AbstractBundlableNode extends AbstractAssetContainer implements BundlableNode {
	private AliasesFile aliasesFile;
	private final MemoizedValue<BundleSet> bundleSet = new MemoizedValue<>("BundlableNode.bundleSet", root(), root().dir());
	private final MemoizedValue<List<AliasDefinitionsFile>> aliasDefinitionFilesList = new MemoizedValue<>("BundlableNode.aliasDefinitionFilesList", root(), root().dir());
	
	public AbstractBundlableNode(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	protected abstract List<LinkedAsset> modelSeedAssets();
	
	@Override
	public List<AssetLocation> seedAssetLocations() {
		List<AssetLocation> seedAssetLocations = new ArrayList<>();
		
		for(AssetLocationPlugin assetLocationPlugin : root().plugins().assetLocationPlugins()) {
			if(assetLocationPlugin.getAssetLocationDirectories(this).size() > 0) {
				for(String seedAssetLocationName : assetLocationPlugin.getSeedAssetLocationDirectories(this)) {
					AssetLocation seedAssetLocation = assetLocation(seedAssetLocationName);
					
					if (seedAssetLocation != null) {
						seedAssetLocations.add(seedAssetLocation);
					}
				}
				
				if(!assetLocationPlugin.allowFurtherProcessing()) {
					break;
				}
			}
		}
		
		return seedAssetLocations;
	}
	
	@Override
	public List<LinkedAsset> seedAssets() {
		List<LinkedAsset> seedFiles = new ArrayList<>(modelSeedAssets());
		
		for(AssetLocation seedAssetLocation : seedAssetLocations()) {
			seedFiles.addAll(seedAssetLocation.linkedAssets());
//			seedFiles.addAll(seedAssetLocation.sourceModules()); // TODO: add extra coverage so this can be fixed without causing only js breakage
		}
		
		return seedFiles;
	}
	
	@Override
	public AliasesFile aliasesFile() {
		if(aliasesFile == null) {
			aliasesFile = new AliasesFile(dir(), "resources/aliases.xml", this);
		}
		
		return aliasesFile;
	}
	
	@Override
	public LinkedAsset getLinkedAsset(String requirePath) throws RequirePathException {
		LinkedAsset asset = null;
		for(AssetContainer assetContainer : scopeAssetContainers()) {
			LinkedAsset locationAsset = assetContainer.linkedAsset(requirePath);
			
			if(locationAsset != null) {
				if(asset == null) {
					asset = locationAsset;
				}
				else {
					throw new AmbiguousRequirePathException("'" + asset.getAssetPath() + "' and '" +
						locationAsset.getAssetPath() + "' source files both available via require path '" +
						requirePath + "'.");
				}
			}			
		}		
		
		if(asset == null) {
			throw new UnresolvableRequirePathException(requirePath);
		}
		
		return asset;
	}
	
	@Override
	public AliasDefinition getAlias(String aliasName) throws AliasException, ContentFileProcessingException {
		
		//TODO: remove the hack that differs in behaviour if an alias starts with "SERVICE!"
		
		boolean isService = aliasName.startsWith("SERVICE!");
		if (isService)
		{
			aliasName = StringUtils.substringAfter(aliasName, "SERVICE!");
		}
		
		try
		{
			return aliasesFile().getAlias(aliasName);
		}
		catch (UnresolvableAliasException ex)
		{
			if (isService)
			{
				// do nothing with the exception since a service might be configured at runtime
				return null;
			}
			throw ex;
		}
	}
	
	@Override
	public BundleSet getBundleSet() throws ModelOperationException {
		return bundleSet.value(() -> {
			return BundleSetCreator.createBundleSet(this);
		});
	}
	
	@Override
	public List<AliasDefinitionsFile> aliasDefinitionFiles() {
		return aliasDefinitionFilesList.value(() -> {
			List<AliasDefinitionsFile> aliasDefinitionFiles = new ArrayList<>();
			
			for(AssetContainer assetContainer : scopeAssetContainers()) {
				for(AssetLocation assetLocation : assetContainer.assetLocations()) {
					aliasDefinitionFiles.addAll( assetLocation.aliasDefinitionsFiles() );
				}
			}
			
			return aliasDefinitionFiles;
		});
	}
	
	@Override
	public ResponseContent handleLogicalRequest(String logicalRequestPath, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		try {
			return BundleSetRequestHandler.handle(this.getBundleSet(), logicalRequestPath, contentAccessor, version);
		}
		catch (ModelOperationException e) {
			throw new ContentProcessingException(e);
		}
	}
	
	@Override
	public List<Asset> getLinkedAssets(AssetLocation assetLocation, List<String> requirePaths) throws RequirePathException {
		List<Asset> assets = new ArrayList<Asset>();
		
		for(String requirePath : requirePaths) {				
			String canonicalRequirePath = assetLocation.canonicaliseRequirePath(requirePath);
			assets.add(getLinkedAsset(canonicalRequirePath));
		}
		
		return assets;
	}
	
	
}
