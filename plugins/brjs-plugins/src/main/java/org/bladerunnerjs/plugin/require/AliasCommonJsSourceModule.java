package org.bladerunnerjs.plugin.require;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.aliasing.AliasDefinition;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.plugin.bundlers.commonjs.CommonJsSourceModule;

public class AliasCommonJsSourceModule implements CommonJsSourceModule {
	private final AssetLocation assetLocation;
	private AliasDefinition aliasDefinition;
	
	public AliasCommonJsSourceModule(AssetLocation assetLocation, AliasDefinition aliasDefinition) {
		this.assetLocation = assetLocation;
		this.aliasDefinition = aliasDefinition;
	}
	
	public AliasDefinition getAliasDefinition() {
		return aliasDefinition;
	}
	
	public void setAlias(AliasDefinition aliasDefinition) {
		this.aliasDefinition = aliasDefinition;
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
			"define('alias!" + aliasDefinition.getName() + "', function(require, exports, module) {\n" +
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
		return "alias!" + aliasDefinition.getName();
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
		try {
			List<Asset> dependencies = new ArrayList<>();
			
			if(aliasDefinition.getInterfaceRequirePath() == null) {
				dependencies.add(bundlableNode.getLinkedAsset(aliasDefinition.getRequirePath()));
			}
			else {
				dependencies.add(bundlableNode.getLinkedAsset("br/Core"));
				dependencies.add(bundlableNode.getLinkedAsset("br/AliasInterfaceError"));
				dependencies.add(bundlableNode.getLinkedAsset(aliasDefinition.getRequirePath()));
				dependencies.add(bundlableNode.getLinkedAsset(aliasDefinition.getInterfaceRequirePath()));
			}
			
			return dependencies;
		}
		catch(RequirePathException e) {
			throw new ModelOperationException(e);
		}
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
		return ((aliasDefinition.getInterfaceRequirePath() == null) ? nonInterfaceModule() : interfaceModule());
	}
	
	private String nonInterfaceModule() {
		return
			"	module.exports = require('" + aliasDefinition.getRequirePath() + "');\n";
	}
	
	private String interfaceModule() {
		return
			"	var br = require('br/Core');\n" +
			"	var AliasInterfaceError = require('br/AliasInterfaceError');\n" +
			"	var classRef = require('" + aliasDefinition.getRequirePath() + "');\n" +
			"	var interfaceRef = require('" + aliasDefinition.getInterfaceRequirePath() + "');\n" +
			"	if(!br.classIsA(classRef, interfaceRef)) throw new AliasInterfaceError('" + aliasDefinition.getName() + "', '" + aliasDefinition.getRequirePath() + "', '" + aliasDefinition.getInterfaceRequirePath() + "');\n" +
			"\n" +
			"	module.exports = classRef;\n";
	}
}
