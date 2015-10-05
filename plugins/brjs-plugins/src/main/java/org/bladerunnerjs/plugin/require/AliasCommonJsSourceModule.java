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
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasesFile;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility;
import org.bladerunnerjs.plugin.bundlers.commonjs.CommonJsSourceModule;
import org.bladerunnerjs.plugin.plugins.require.AliasDataSourceModule;

import com.google.common.base.Joiner;

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
		List<String> dependencies = new ArrayList<>();
		if(aliasDefinition.getRequirePath() != null) {
			dependencies.add("'" + aliasDefinition.getRequirePath() + "'");
		}
		if(aliasDefinition.getInterfaceRequirePath() != null) {
			dependencies.add("'" + aliasDefinition.getInterfaceRequirePath() + "'");
			dependencies.add("'topiarist'");
		}
		
		String interfaceCheck =
			"	var InterfaceClass = require('" + aliasDefinition.getInterfaceRequirePath() + "');\n" +
			"	var topiarist = require('topiarist');\n" +
			"	if(!topiarist.classIsA(AliasClass, InterfaceClass)) throw new TypeError(\"'" + aliasDefinition.getRequirePath() + "' was not an instance of '" + aliasDefinition.getInterfaceRequirePath() + "'.\");\n";
		
		return new StringReader(
			"System.registerDynamic('alias!" + aliasDefinition.getName() + "', [" + Joiner.on(", ").join(dependencies) + "], true, function(require, exports, module) {\n" +
			"	var AliasClass = require('" + aliasDefinition.getRequirePath() + "');\n" +
			((aliasDefinition.getInterfaceRequirePath() == null) ? "" : interfaceCheck) +
			"	module.exports = AliasClass;\n" +
			"	return module.exports;\n" +
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
			AliasesFile aliasesFile = AliasingUtility.aliasesFile(bundlableNode);
			AliasDefinition resolvedAliasDefinition = aliasesFile.getAlias(aliasDefinition.getName());
			
			dependencies.add(bundlableNode.getLinkedAsset("br/AliasRegistry"));
			dependencies.add(bundlableNode.getLinkedAsset(resolvedAliasDefinition.getRequirePath()));
			if (aliasDefinition.getInterfaceRequirePath() != null) {
				dependencies.add(bundlableNode.getLinkedAsset(resolvedAliasDefinition.getInterfaceRequirePath()));
			}
			
			Asset aliasData = bundlableNode.getLinkedAsset(AliasDataSourceModule.PRIMARY_REQUIRE_PATH);
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
