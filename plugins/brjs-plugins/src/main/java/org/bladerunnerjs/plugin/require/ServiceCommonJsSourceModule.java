package org.bladerunnerjs.plugin.require;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.plugin.bundlers.commonjs.CommonJsSourceModule;


public class ServiceCommonJsSourceModule implements CommonJsSourceModule {
	
	private final String requirePath;
	private AssetContainer assetContainer;
	private MemoizedFile dir;
	
	public ServiceCommonJsSourceModule(AssetContainer assetContainer, String requirePath) {
		this.assetContainer = assetContainer;
		this.requirePath = requirePath;
		this.dir = assetContainer.dir();;
	}

	@Override
	public void addImplicitDependencies(List<Asset> implicitDependencies) {
	}
	
	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return getPreExportDefineTimeDependentAssets(bundlableNode);
	}

	@Override
	public Reader getReader() throws IOException {
		return new StringReader("define('service!" + requirePath + "', function(require, exports, module) {\n"+
			"	module.preventCaching = true;\n" +
			"	module.exports = require('br/ServiceRegistry').getService('" + requirePath + "');\n" +
			"});\n"
		);
	}

	@Override
	public MemoizedFile file() {
		return dir;
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
		
		try
		{
			dependencies.add(bundlableNode.getLinkedAsset("br/ServiceRegistry"));
		}
		catch (RequirePathException ex)
		{
			throw new ModelOperationException(ex);
		}
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
	public AssetContainer assetContainer()
	{
		return assetContainer;
	}
	
	@Override
	public boolean isScopeEnforced() {
		return false;
	}
	
	@Override
	public boolean isRequirable()
	{
		return true;
	}

}
