package org.bladerunnerjs.plugin.plugins.require;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasDefinition;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasException;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility;
import org.bladerunnerjs.plugin.require.AliasCommonJsSourceModule;


public class ServiceDataSerializer
{

	public static String createJson(BundleSet bundleSet) throws ModelOperationException
	{
		final String sourceModuleJsonSeparator = ",\n";
		
		StringBuffer output = new StringBuffer();
		
		List<AliasDefinition> aliasDefinitions = getAliasDefinitions(bundleSet);
		BundlableNode bundlableNode = bundleSet.bundlableNode();
		List<Asset> serviceAssets = bundleSet.assets("service!");
		for (Asset asset : serviceAssets) {
			
			if (!(asset instanceof SourceModule)) {
				continue;
			}
			if (asset.getPrimaryRequirePath().equals(ServiceDataSourceModule.PRIMARY_REQUIRE_PATH)) {
				continue;
			}
			
			SourceModule serviceSourceModule = (SourceModule) asset;
			addSourceModuleData(output, bundlableNode, aliasDefinitions, serviceSourceModule);
			output.append(sourceModuleJsonSeparator);
		}
		if (output.length() > 0) {
			output.setLength( output.length() - sourceModuleJsonSeparator.length() ); // remove the final separator that was added above
			return "{\n"+output.toString()+"\n}";
		}
		return "{ }";	
	}

	private static void addSourceModuleData(StringBuffer output, BundlableNode bundlableNode, List<AliasDefinition> aliasDefinitions, SourceModule serviceSourceModule) throws ModelOperationException
	{
		SourceModule resolvedServiceSourceModule = resolveService(aliasDefinitions, serviceSourceModule, bundlableNode);
		if (resolvedServiceSourceModule != null) {
			List<Asset> dependantAssets = resolvedServiceSourceModule.getDependentAssets(bundlableNode);
    		output.append( String.format(
			"	\"%s\": {\n"+
			"		\"requirePath\": \"%s\",\n"+
			"		\"dependencies\": [%s]\n"+
			"	}", serviceSourceModule.getPrimaryRequirePath(), resolvedServiceSourceModule.getPrimaryRequirePath(), stringifyDependantAssets(dependantAssets) 
			) );
		} else {
			System.err.println(serviceSourceModule.getAssetPath());
		}
	}
	
	private static Object stringifyDependantAssets(List<Asset> dependantAssets)
	{
		List<String> assetStrings = new LinkedList<>();
		
		for (Asset asset : dependantAssets) {
			String assetPrimaryRequirePath = asset.getPrimaryRequirePath();
			if (!assetPrimaryRequirePath.startsWith("service!")) {
				continue;
			}
			String assetRequireSuffix = StringUtils.substringAfter(assetPrimaryRequirePath, "service!");
			
			assetStrings.add("			\""+assetRequireSuffix+"\"");
		}
		
		if (assetStrings.isEmpty()) {
			return "";
		}
		return "\n"+StringUtils.join(assetStrings, ",\n")+"\n		"; // whitespace is intentional so we can output well formatted JSON
	}

	private static SourceModule resolveService(List<AliasDefinition> aliasDefinitions, SourceModule serviceSourceModule, BundlableNode bundlableNode)
	{
		for (AliasDefinition aliasDefinition : aliasDefinitions) {
			String serviceRequireSuffix = serviceSourceModule.getPrimaryRequirePath().replaceFirst("service!", "");
			
			if (aliasDefinition.getName().equals(serviceRequireSuffix)) {
				Asset asset =  bundlableNode.asset(aliasDefinition.getRequirePath());
				if (asset instanceof SourceModule) {
					return (SourceModule) asset;
				}
			}
		}
		return null;
	}

	private static List<AliasDefinition> getAliasDefinitions(BundleSet bundleSet) {
		List<AliasDefinition> aliasDefinitions = new ArrayList<>();
		
		List<AliasCommonJsSourceModule> aliasModules = bundleSet.sourceModules(AliasCommonJsSourceModule.class);
		
		for (AliasCommonJsSourceModule aliasSourceModule : aliasModules) {
			AliasDefinition aliasDefinition = aliasSourceModule.getAliasDefinition();
			try
			{
				aliasDefinition = AliasingUtility.resolveAlias(aliasDefinition.getName(), bundleSet.bundlableNode());
			}
			catch (AliasException e)
			{
				// use the alias definition we had already
			}
			catch (ContentFileProcessingException ex)
			{
				throw new RuntimeException(ex);
			}
			aliasDefinitions.add(aliasDefinition);
		}
		
		return aliasDefinitions;
	}

}
