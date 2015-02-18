package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.Workbench;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSetCreator;
import org.bladerunnerjs.model.BundleSetCreator.Messages;
import org.bladerunnerjs.model.StandardBundleSet;

import com.google.common.base.Joiner;

public class BundleSetBuilder {
	
	public static final String BOOTSTRAP_LIB_NAME = "br-bootstrap";
	
	private final Set<Asset> assets = new LinkedHashSet<>();
	private final Set<SourceModule> sourceModules = new LinkedHashSet<>();
	private final Set<LinkedAsset> linkedAssets = new HashSet<LinkedAsset>();
	private final BundlableNode bundlableNode;
	private final Logger logger;
	
	public BundleSetBuilder(BundlableNode bundlableNode) {
		this.bundlableNode = bundlableNode;
		logger = bundlableNode.root().logger(BundleSetCreator.class);
	}
	
	public BundleSet createBundleSet() throws ModelOperationException {
		
		if (bundlableNode instanceof Workbench) {
			Asset rootAsset = bundlableNode.app().defaultAspect().asset(bundlableNode.app().getRequirePrefix());
			if (rootAsset != null && rootAsset instanceof LinkedAsset) {
				addUnscopedAsset( (LinkedAsset)rootAsset );
			}
		}
		
		List<SourceModule> bootstrappingSourceModules = new ArrayList<SourceModule>();
		if (!sourceModules.isEmpty())
		{
			addBootstrapAndDependencies(bootstrappingSourceModules);
		}
		
		List<SourceModule> orderedSourceModules = SourceModuleDependencyOrderCalculator.getOrderedSourceModules(bundlableNode, bootstrappingSourceModules, sourceModules);
		List<Asset> assetList = orderAssets(assets);
		
		return new StandardBundleSet(bundlableNode, assetList, orderedSourceModules);
	}

	public void addSeedFiles(List<LinkedAsset> seedFiles) throws ModelOperationException {
		for(LinkedAsset seedFile : seedFiles) {
			addLinkedAsset(seedFile);
		}
	}
	
	
	private void addSourceModule(SourceModule sourceModule) throws ModelOperationException {
		if (sourceModules.add(sourceModule)) {
			addLinkedAsset(sourceModule);
		}
	}

	private void addLinkedAsset(LinkedAsset linkedAsset) throws ModelOperationException {
		if(linkedAssets.add(linkedAsset)) {
			assets.add(linkedAsset);
			List<Asset> moduleDependencies = getModuleDependencies(linkedAsset);
			
			if (linkedAsset instanceof SourceModule) {
				addSourceModule((SourceModule) linkedAsset);
			}
			
			for(Asset asset : moduleDependencies) {
				if(asset instanceof SourceModule){
					addSourceModule((SourceModule)asset);
				} else if (asset instanceof LinkedAsset) {
					addLinkedAsset((LinkedAsset) asset);						
				}
				assets.add(asset);
			}
			
		}
	}

	private List<Asset> getModuleDependencies(LinkedAsset linkedAsset) throws ModelOperationException
	{
		List<Asset> moduleDependencies = new ArrayList<>(linkedAsset.getDependentAssets(bundlableNode));
		
		if(moduleDependencies.isEmpty()) {
			logger.debug(Messages.FILE_HAS_NO_DEPENDENCIES_MSG, linkedAsset.getAssetPath());
		}
		else {
			
			logger.debug(Messages.FILE_DEPENDENCIES_MSG, linkedAsset.getAssetPath(), assetFilePaths(moduleDependencies));
		}
		return moduleDependencies;
	}
	
	
	private void addUnscopedAsset(LinkedAsset asset) throws ModelOperationException {
		if (assets.add(asset)) {
			for (Asset dependentAsset : getModuleDependencies(asset)) {
				assets.add(dependentAsset);
			}
		}
	}
	
	private String assetFilePaths(List<Asset> assets) {
		List<String> sourceFilePaths = new ArrayList<>();
		
		for(Asset asset : assets) {
			sourceFilePaths.add(asset.getAssetPath());
		}
		
		return "'" + Joiner.on("', '").join(sourceFilePaths) + "'";
	}
	
	
	private void addAllSourceModuleDependencies(SourceModule sourceModule, List<SourceModule> sourceModules) throws ModelOperationException
	{
		addAllSourceModuleDependencies(sourceModule, sourceModules, new ArrayList<SourceModule>());
	}
	
	private void addAllSourceModuleDependencies(SourceModule sourceModule, List<SourceModule> sourceModules, List<SourceModule> processedModules) throws ModelOperationException
	{
		if (processedModules.contains(sourceModule))
		{
			return;
		}
		processedModules.add(sourceModule);
		
		for (Asset asset : sourceModule.getDependentAssets(bundlableNode))
		{
			if (!sourceModules.contains(asset)) {
				if(asset instanceof SourceModule){
					addAllSourceModuleDependencies((SourceModule)asset, sourceModules, processedModules);
				}
			}
		}
		sourceModules.add(sourceModule);
	}
	
	private void addBootstrapAndDependencies(List<SourceModule> bootstrappingSourceModules) throws ModelOperationException
	{
		JsLib boostrapLib = bundlableNode.app().jsLib(BOOTSTRAP_LIB_NAME);
		for (Asset asset : boostrapLib.assets()) {
			if (asset instanceof SourceModule) {
				addSourceModule( (SourceModule) asset );
				addAllSourceModuleDependencies( (SourceModule) asset, bootstrappingSourceModules );						
			}
		}
	}
	
	private List<Asset> orderAssets(Set<Asset> assets) {
		List<Asset> orderedAssets = new ArrayList<>();
		List<Asset> unorderedAssets = new ArrayList<>(assets);
		for (AssetContainer assetContainer : bundlableNode.scopeAssetContainers()) {
			for (Asset asset : assets) {
				if (asset.assetContainer() == assetContainer) {
					orderedAssets.add(asset);
					unorderedAssets.remove(asset);
				}
			}
		}
		orderedAssets.addAll(0, unorderedAssets);
		return orderedAssets;
	}
	
}
