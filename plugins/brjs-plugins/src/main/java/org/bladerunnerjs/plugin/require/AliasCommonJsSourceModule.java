package org.bladerunnerjs.plugin.require;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasDefinition;
import org.bladerunnerjs.plugin.bundlers.commonjs.CommonJsSourceModule;

public class AliasCommonJsSourceModule implements CommonJsSourceModule {
	
	private final BundlableNode bundlableNode;
	private AliasDefinition aliasDefinition;
	private String requirePath;
	
	public AliasCommonJsSourceModule(BundlableNode bundlableNode, AliasDefinition aliasDefinition) {
		this.bundlableNode = bundlableNode;
		this.aliasDefinition = aliasDefinition;
		this.requirePath = calculateRequirePath(aliasDefinition);
	}
	
	public AliasDefinition getAliasDefinition() {
		return aliasDefinition;
	}
	
	public void setAlias(AliasDefinition aliasDefinition) {
		this.aliasDefinition = aliasDefinition;
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
	public MemoizedFile file() {
		return bundlableNode.dir();
	}

	@Override
	public String getAssetName() {
		return aliasDefinition.getName();
	}

	@Override
	public String getAssetPath() {
		return "alias!"+getAssetName();
	}

	@Override
	public List<String> getRequirePaths() {
		return Arrays.asList(requirePath);
	}

	@Override
	public String getPrimaryRequirePath() {
		return requirePath;
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
				if (aliasDefinition.getInterfaceRequirePath() != null) {
					dependencies.add(bundlableNode.getLinkedAsset(aliasDefinition.getInterfaceRequirePath()));
				}
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

	@Override
	public AssetContainer assetContainer()
	{
		return bundlableNode;
	}
	
	public static String calculateRequirePath(AliasDefinition alias) {
		return "alias!"+alias.getName();
	}
}
