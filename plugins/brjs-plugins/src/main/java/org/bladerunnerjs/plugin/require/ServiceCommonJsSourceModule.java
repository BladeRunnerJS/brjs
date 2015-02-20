package org.bladerunnerjs.plugin.require;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.plugin.bundlers.commonjs.CommonJsSourceModule;

public class ServiceCommonJsSourceModule implements CommonJsSourceModule {
	private final AssetLocation assetLocation;
	private final String requirePath;
	
	public ServiceCommonJsSourceModule(AssetLocation assetLocation, String requirePath) {
		this.assetLocation = assetLocation;
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
			dependencies.add(bundlableNode.getLinkedAsset("br/ServiceRegistry"));
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
		return "	module.exports = require('br/ServiceRegistry').getService('" + requirePath + "');\n";
	}
}
