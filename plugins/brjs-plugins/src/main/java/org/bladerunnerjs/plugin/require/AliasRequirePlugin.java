package org.bladerunnerjs.plugin.require;

import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.UnresolvableRequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.base.AbstractRequirePlugin;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasDefinition;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasException;
import org.bladerunnerjs.plugin.plugins.require.AliasDataSourceModule;

public class AliasRequirePlugin extends AbstractRequirePlugin implements RequirePlugin {
	private final Map<BundlableNode, Map<String, SourceModule>> bundlableNodeSourceModules = new HashMap<>();
	
	@Override
	public void setBRJS(BRJS brjs) {
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
	
	private SourceModule getSourceModule(BundlableNode bundlableNode, String requirePathSuffix) throws ContentFileProcessingException, AliasException {
		if(!bundlableNodeSourceModules.containsKey(bundlableNode)) {
			bundlableNodeSourceModules.put(bundlableNode, new HashMap<>());
			bundlableNodeSourceModules.get(bundlableNode).put("$data", new AliasDataSourceModule(bundlableNode));
		}
		
		Map<String, SourceModule> sourceModules = bundlableNodeSourceModules.get(bundlableNode);
		
		if(requirePathSuffix.equals("$data")) {
			return sourceModules.get(requirePathSuffix);
		}
		else {
			
			AliasDefinition aliasDefinition = getAlias(bundlableNode, requirePathSuffix);
			
			if(!sourceModules.containsKey(requirePathSuffix)) {
				sourceModules.put(requirePathSuffix, new AliasCommonJsSourceModule(bundlableNode, aliasDefinition));
			}
			else {
				((AliasCommonJsSourceModule) sourceModules.get(requirePathSuffix)).setAlias(aliasDefinition);
			}
			
			return sourceModules.get(requirePathSuffix);
		}
	}
}
