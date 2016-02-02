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
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasDefinition;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasException;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility;
import org.bladerunnerjs.plugin.bundlers.commonjs.CommonJsSourceModule;
import org.bladerunnerjs.plugin.plugins.require.AliasDataSourceModule;

public class AliasCommonJsSourceModule implements CommonJsSourceModule {
	
	private final AssetContainer assetContainer;
	private AliasDefinition aliasDefinition;
	private String requirePath;
	private List<Asset> implicitDependencies;
	
	public AliasCommonJsSourceModule(AssetContainer assetContainer, AliasDefinition aliasDefinition, List<Asset> implicitDependencies) {
		this.assetContainer = assetContainer;
		this.aliasDefinition = aliasDefinition;
		this.implicitDependencies = implicitDependencies;
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
			"	module.exports = require('br/AliasRegistry').getClass('"+aliasDefinition.getName()+"');\n" +
			"});\n"
		);
	}

	@Override
	public MemoizedFile file() {
		return assetContainer.dir();
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
		List<Asset> dependencies = new ArrayList<>();
		
		try {
			AliasDefinition resolvedAliasDefinition = AliasingUtility.resolveAlias(aliasDefinition.getName(), bundlableNode);
			
			dependencies.add(bundlableNode.getAsset("br/AliasRegistry"));
			dependencies.add(bundlableNode.getAsset(resolvedAliasDefinition.getRequirePath()));
			if (aliasDefinition.getInterfaceRequirePath() != null) {
				dependencies.add(bundlableNode.getAsset(resolvedAliasDefinition.getInterfaceRequirePath()));
			}
			
			Asset aliasData = bundlableNode.getAsset(AliasDataSourceModule.PRIMARY_REQUIRE_PATH);
			if (aliasData != null) {
				dependencies.add(aliasData);
			}
			dependencies.addAll(implicitDependencies);
			
			return dependencies;
		}
		catch(ContentFileProcessingException | RequirePathException | AliasException e) {
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
	
	public static String calculateRequirePath(AliasDefinition alias) {
		return "alias!"+alias.getName();
	}
}
