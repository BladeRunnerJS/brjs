package org.bladerunnerjs.plugin.plugins.require;

import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.aliasing.AliasDefinition;
import org.bladerunnerjs.api.aliasing.AliasException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.base.AbstractRequirePlugin;
import org.bladerunnerjs.model.BundlableNode;

public class AliasRequirePlugin extends AbstractRequirePlugin implements RequirePlugin {
	private final Map<BundlableNode, Map<String, AliasCommonJsSourceModule>> bundlableNodeSourceModules = new HashMap<>();
	private AssetLocation assetLocation;
	
	@Override
	public void setBRJS(BRJS brjs) {
		assetLocation = new NullAssetLocation(brjs);
	}

	@Override
	public String getPluginName() {
		return "alias";
	}

	@Override
	public Asset getAsset(BundlableNode bundlableNode, String requirePathSuffix) throws RequirePathException {
		try {
			return getSourceModule(bundlableNode, requirePathSuffix);
		}
		catch (ContentFileProcessingException | AliasException e) {
			throw new UnresolvableRequirePathException("alias!" + requirePathSuffix, e);
		}
	}
	
	private AliasCommonJsSourceModule getSourceModule(BundlableNode bundlableNode, String requirePathSuffix) throws ContentFileProcessingException, AliasException {
		if(!bundlableNodeSourceModules.containsKey(bundlableNode)) {
			bundlableNodeSourceModules.put(bundlableNode, new HashMap<>());
		}
		
		Map<String, AliasCommonJsSourceModule> sourceModules = bundlableNodeSourceModules.get(bundlableNode);
		AliasDefinition aliasDefinition = bundlableNode.getAlias(requirePathSuffix);
		
		if(!sourceModules.containsKey(requirePathSuffix)) {
			sourceModules.put(requirePathSuffix, new AliasCommonJsSourceModule(assetLocation, aliasDefinition));
		}
		else {
			sourceModules.get(requirePathSuffix).setAlias(aliasDefinition);
		}
		
		return sourceModules.get(requirePathSuffix);
	}
}
