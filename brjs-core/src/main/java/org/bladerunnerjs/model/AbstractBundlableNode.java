package org.bladerunnerjs.model;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.aliasing.IncompleteAliasException;
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
import org.bladerunnerjs.utility.LogicalRequestHandler;

public abstract class AbstractBundlableNode extends AbstractAssetContainer implements BundlableNode {
	private AliasesFile aliasesFile;
	private LogicalRequestHandler requestHandler;
	private final MemoizedValue<BundleSet> bundleSet = new MemoizedValue<>("BundlableNode.bundleSet", root(), root().dir());
	private final MemoizedValue<List<AliasDefinitionsFile>> aliasDefinitionFilesList = new MemoizedValue<>("BundlableNode.aliasDefinitionFilesList", root(), root().dir());
	
	public AbstractBundlableNode(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	protected abstract List<LinkedAsset> modelSeedAssets();
	
	@Override
	public List<AssetLocation> seedAssetLocations() {
		List<AssetLocation> seedAssetLocations = new ArrayList<>();
		
		for(AssetLocationPlugin assetLocationPlugin : root().plugins().assetLocationProducers()) {
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
	public SourceModule getSourceModule(String requirePath) throws RequirePathException {
		SourceModule sourceModule = null;
		
		for(AssetContainer assetContainer : assetContainers()) {//getPotentialAssetContainers(requirePath)) {
			SourceModule locationSourceModule = assetContainer.sourceModule(requirePath);
			
			if(locationSourceModule != null) {
				if(sourceModule == null) {
					sourceModule = locationSourceModule;
				}
				else {
					throw new AmbiguousRequirePathException("'" + sourceModule.getAssetPath() + "' and '" +
						locationSourceModule.getAssetPath() + "' source files both available via require path '" +
						sourceModule.getRequirePath() + "'.");
				}
			}
		}
		
		if(sourceModule == null) {
			throw new UnresolvableRequirePathException(requirePath);
		}
		
		return sourceModule;
	}
	
	@Override
	public AliasDefinition getAlias(String aliasName) throws UnresolvableAliasException, AmbiguousAliasException, IncompleteAliasException, ContentFileProcessingException {
		
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
			
			for(AssetContainer assetContainer : assetContainers()) {
				for(AssetLocation assetLocation : assetContainer.assetLocations()) {
					AliasDefinitionsFile aliasDefinitionsFile = assetLocation.aliasDefinitionsFile();
					
					if(aliasDefinitionsFile.getUnderlyingFile().exists()) {
						aliasDefinitionFiles.add(aliasDefinitionsFile);
					}
				}
			}
			
			return aliasDefinitionFiles;
		});
	}
	
	@Override
	public void handleLogicalRequest(String logicalRequestPath, OutputStream os) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		if (requestHandler == null)
		{
			requestHandler = new LogicalRequestHandler(this);
		}
		requestHandler.handle( logicalRequestPath, os);
	}
	
	@Override
	public List<AssetContainer> scopeAssetContainers() {
		return assetContainers();
	}
	
}
