package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.aliasing.AliasDefinition;
import org.bladerunnerjs.api.aliasing.AliasException;
import org.bladerunnerjs.api.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.plugin.AssetLocationPlugin;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.BundleSetRequestHandler;

public abstract class AbstractBundlableNode extends AbstractAssetContainer implements BundlableNode {
	private AliasesFile aliasesFile;
	private final MemoizedValue<BundleSet> bundleSet = new MemoizedValue<>("BundlableNode.bundleSet", root(), root().dir());
	private final MemoizedValue<List<AliasDefinitionsFile>> aliasDefinitionFilesList = new MemoizedValue<>("BundlableNode.aliasDefinitionFilesList", root(), root().dir());
	
	public AbstractBundlableNode(RootNode rootNode, Node parent, MemoizedFile dir) {
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
		RequirePlugin requirePlugin;
		String requirePathSuffix;
		
		if(requirePath.contains("!")) {
			String[] parts = requirePath.split("!");
			String pluginName = parts[0];
			requirePathSuffix = parts[1];
			requirePlugin = root().plugins().requirePlugin(pluginName);
		}
		else {
			requirePlugin = root().plugins().requirePlugin("default");
			requirePathSuffix = requirePath;
		}
		
		return (LinkedAsset) requirePlugin.getAsset(this, requirePathSuffix);
	}
	
	@Override
	public AliasDefinition getAlias(String aliasName) throws AliasException, ContentFileProcessingException {
		return aliasesFile().getAlias(aliasName);
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
