package org.bladerunnerjs.plugin.plugins.require;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.plugin.plugins.bundlers.commonjs.CommonJsSourceModule;

public class ServiceCommonJsSourceModule implements CommonJsSourceModule {
	private final static Map<String, ServiceCommonJsSourceModule> sourceModules = new HashMap<>();
	private static AssetLocation assetLocation;
	private String requirePath;

	public static ServiceCommonJsSourceModule getSourceModule(BRJS brjs, String requirePath) {
		if(assetLocation == null) {
			assetLocation = new NullAssetLocation(brjs);
		}
		
		if(!sourceModules.containsKey(requirePath)) {
			sourceModules.put(requirePath, new ServiceCommonJsSourceModule(requirePath));
		}
		
		return sourceModules.get(requirePath);
	}
	
	public ServiceCommonJsSourceModule(String requirePath) {
		this.requirePath = requirePath;
	}

	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return getPreExportDefineTimeDependentAssets(bundlableNode);
	}

	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		return Collections.emptyList();
	}

	@Override
	public Reader getReader() throws IOException {
		return new StringReader(
			"define('service!" + requirePath + "', function(require, exports, module) {\n" +
			getModuleContent() +
			"});\n");
	}

	@Override
	public Reader getUnalteredContentReader() throws IOException {
		return new StringReader(getModuleContent());
	}

	@Override
	public AssetLocation assetLocation() {
		return assetLocation;
	}

	@Override
	public MemoizedFile dir() {
		return assetLocation.dir();
	}

	@Override
	public String getAssetName() {
		return getPrimaryRequirePath();
	}

	@Override
	public String getAssetPath() {
		return getPrimaryRequirePath();
	}

	@Override
	public List<String> getRequirePaths() {
		List<String> requirePaths = new ArrayList<>();
		requirePaths.add(getPrimaryRequirePath());
		
		return requirePaths;
	}

	@Override
	public String getPrimaryRequirePath() {
		return "service!" + requirePath;
	}

	@Override
	public boolean isEncapsulatedModule() {
		return true;
	}

	@Override
	public boolean isGlobalisedModule() {
		return false;
	}

	@Override
	public List<Asset> getPreExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		List<Asset> dependencies = new ArrayList<>();
		
		try {
			dependencies.add(bundlableNode.getLinkedAsset("alias!" + requirePath));
		}
		catch(RequirePathException e) {
			// ignore exception here since services are allowed to have their aliases defined on the client
		}
		
		return dependencies;
	}

	@Override
	public List<Asset> getPostExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return Collections.emptyList();
	}

	@Override
	public List<Asset> getUseTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return Collections.emptyList();
	}

	@Override
	public List<AssetLocation> assetLocations() {
		return Collections.emptyList();
	}
	
	private String getModuleContent() {
		return "	module.exports = new (require('alias!" + requirePath + "'))();\n";
	}
}
