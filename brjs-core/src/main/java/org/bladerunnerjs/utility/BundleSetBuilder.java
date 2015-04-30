package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.Workbench;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.OutOfBundleScopeRequirePathException;
import org.bladerunnerjs.api.model.exception.OutOfScopeRequirePathException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BundleSetCreator;
import org.bladerunnerjs.model.BundleSetCreator.Messages;
import org.bladerunnerjs.model.StandardBundleSet;

import com.google.common.base.Joiner;

public class BundleSetBuilder {
	
	public static final String BOOTSTRAP_LIB_NAME = "br-bootstrap";
	public static final String STRICT_CHECKING_DISABLED_MSG = "Strict checking has been disabled for the directory '%s' and for the Asset '%s'."+
			" This allows the Blade class to directly depend on another Blade class when the App loaded. This dependency should be broken using Services and the file '%s' should be removed to re-enable the scope enforcement.";
	public static final String INVALID_REQUIRE_MSG = "The class '%s' depends on the class '%s' which is outside of it's scope - this dependency should be broken using Services.";
	
	private final List<LinkedAsset> seedAssets = new ArrayList<>();
	private final Set<Asset> assets = new LinkedHashSet<>();
	private final Set<SourceModule> sourceModules = new LinkedHashSet<>();
	private final Set<Asset> processedAssets = new LinkedHashSet<>();
	private final BundlableNode bundlableNode;
	private final Logger logger;
	private Set<Asset> strictCheckingAssetsLogged = new HashSet<>();
	
	public BundleSetBuilder(BundlableNode bundlableNode) {
		this.bundlableNode = bundlableNode;
		logger = bundlableNode.root().logger(BundleSetCreator.class);
	}
	
	public BundleSet createBundleSet() throws ModelOperationException {
		
		if (bundlableNode instanceof Workbench) {
			Asset rootAsset = bundlableNode.app().defaultAspect().asset(bundlableNode.app().getRequirePrefix());
			if (rootAsset instanceof LinkedAsset) {
				addUnscopedAsset( (LinkedAsset)rootAsset );
			}
		}
		
		List<SourceModule> bootstrappingSourceModules = new ArrayList<SourceModule>();
		if (!sourceModules.isEmpty())
		{
			addBootstrapAndDependencies(bootstrappingSourceModules);
		}
		
		List<SourceModule> orderedSourceModules = SourceModuleDependencyOrderCalculator.getOrderedSourceModules(bundlableNode, bootstrappingSourceModules, sourceModules);
		List<Asset> assetList = orderAssetsByAssetContainer(assets);
		
		return new StandardBundleSet(bundlableNode, seedAssets, assetList, orderedSourceModules);
	}

	public void addSeedFiles(List<LinkedAsset> seedFiles) throws ModelOperationException {
		seedAssets.addAll(seedFiles);
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
		if(processedAssets.add(linkedAsset)) {
			if (linkedAsset.isRequirable()) {
				assets.add(linkedAsset);
			}
			
			List<Asset> moduleDependencies = getModuleDependencies(linkedAsset);
			
			if (linkedAsset instanceof SourceModule) {
				addSourceModule((SourceModule) linkedAsset);
			}
			
			BRJS brjs = linkedAsset.assetContainer().root();
			for(Asset dependantAsset : moduleDependencies) {
				boolean assetIsInScope = ensureDependentAssetIsInScope(linkedAsset, dependantAsset);
				if (!assetIsInScope && linkedAsset.isScopeEnforced() && dependantAsset.isScopeEnforced()) {
					brjs.logger(this.getClass()).warn(INVALID_REQUIRE_MSG, linkedAsset.getPrimaryRequirePath(), dependantAsset.getPrimaryRequirePath());
				}
				if(dependantAsset instanceof SourceModule){
					addSourceModule((SourceModule)dependantAsset);
				} else if (dependantAsset instanceof LinkedAsset) {
					addLinkedAsset((LinkedAsset) dependantAsset);
				}
				
				if (dependantAsset.isRequirable()) {
					assets.add(dependantAsset);
				}
			}
			
		}
	}

	private boolean ensureDependentAssetIsInScope(LinkedAsset asset, Asset dependantAsset) throws ModelOperationException
	{
		boolean throwExceptionOnFailure = true;
		if (!asset.isScopeEnforced() || !dependantAsset.isScopeEnforced() ||
				strictCheckingDisabled(asset) || strictCheckingDisabled(dependantAsset)) {
			throwExceptionOnFailure = false;
		}
		
		StringBuilder scopedLocations = new StringBuilder();
		AssetContainer sourceAssetContainer = asset.assetContainer();
		AssetContainer dependantAssetContainer = dependantAsset.assetContainer();
		BRJS brjs = sourceAssetContainer.root();
		
		for (AssetContainer sourceAssetContainerScope : sourceAssetContainer.scopeAssetContainers()) {
			if (assetContainerMatchesScope(sourceAssetContainerScope, dependantAssetContainer)) {
				return true;
			}
			scopedLocations.append( brjs.dir().getRelativePath(sourceAssetContainerScope.dir()) );
		}
		if (!throwExceptionOnFailure) {
			return false;
		}
		RequirePathException scopeException = new OutOfScopeRequirePathException(asset, dependantAsset);
		throw new ModelOperationException(scopeException);
	}
	
	private boolean assetContainerMatchesScope(AssetContainer sourceAssetContainer, AssetContainer dependantAssetContainer) {
		// check the dir is equals as well incase the asset container is wrapped
		if (sourceAssetContainer == dependantAssetContainer) {
			return true;
		}
		if (sourceAssetContainer.dir() == dependantAssetContainer.dir() && sourceAssetContainer.getClass() == dependantAssetContainer.getClass()) {
			return true;
		}
		if (sourceAssetContainer instanceof JsLib && dependantAssetContainer instanceof JsLib && sourceAssetContainer.dir().getName().equals(dependantAssetContainer.dir().getName())) {
			return true;
		}
		return false;
	}

	private List<Asset> getModuleDependencies(LinkedAsset linkedAsset) throws ModelOperationException
	{
		List<Asset> moduleDependencies;
		try {
			moduleDependencies = new ArrayList<>(linkedAsset.getDependentAssets(bundlableNode));
		} catch (ModelOperationException ex) {
			if (ex.getCause() instanceof OutOfBundleScopeRequirePathException) {
				((OutOfBundleScopeRequirePathException) ex.getCause()).setAssetWithException(linkedAsset);
			}
			throw ex;
		}
		
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
	
	private <AT extends Asset> List<AT> orderAssetsByAssetContainer(Set<? extends AT> assets) {
		List<AT> orderedAssets = new ArrayList<>();
		List<AT> unorderedAssets = new ArrayList<>(assets);
		for (AssetContainer assetContainer : bundlableNode.scopeAssetContainers()) {
			for (AT asset : assets) {
				if (asset.assetContainer() == assetContainer) {
					orderedAssets.add(asset);
					unorderedAssets.remove(asset);
				}
			}
		}
		orderedAssets.addAll(0, unorderedAssets);
		return orderedAssets;
	}	
	
	private boolean strictCheckingDisabled(Asset asset) {
		MemoizedFile currentDir = asset.file().isDirectory() ? asset.file() : asset.file().getParentFile();
		while (currentDir != null && currentDir != asset.assetContainer().dir().getParentFile()) {
			MemoizedFile strictCheckingFile = currentDir.file("no-strict-checking");
			if (strictCheckingFile.isFile()) {
				if (!strictCheckingAssetsLogged.add(asset)) {
					BRJS brjs = asset.assetContainer().root();
					brjs.logger(this.getClass()).warn(STRICT_CHECKING_DISABLED_MSG, brjs.dir().getRelativePath(currentDir), asset.getAssetPath(), brjs.dir().getRelativePath(strictCheckingFile));
				}
				return true;
			}
			currentDir = currentDir.getParentFile();
		}
		return false;
	}
	
}
