package org.bladerunnerjs.plugin.plugins.require;

import java.util.ArrayList;
import java.util.List;

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
		StringBuffer output = new StringBuffer();
		
		List<AliasDefinition> aliasDefinitions = getAliasDefinitions(bundleSet);
		BundlableNode bundlableNode = bundleSet.bundlableNode();
		List<Asset> serviceAssets = bundleSet.assets("service!");
		for (Asset asset : serviceAssets) {
			
			if (!(asset instanceof SourceModule)) {
				continue;
			}
			
			if (output.length() != 0) {
				output.append(",\n");
			}
			
			SourceModule serviceSourceModule = (SourceModule) asset;
			addSourceModuleData(output, bundlableNode, aliasDefinitions, serviceSourceModule);
		}
		return "{\n"+output.toString()+"}";
	}

	private static void addSourceModuleData(StringBuffer output, BundlableNode bundlableNode, List<AliasDefinition> aliasDefinitions, SourceModule serviceSourceModule) throws ModelOperationException
	{
		List<Asset> dependantAssets = serviceSourceModule.getDependentAssets(bundlableNode);
		Asset resolvedServiceSourceModule = resolveService(aliasDefinitions, serviceSourceModule, bundlableNode);
		if (resolvedServiceSourceModule != null) {
    		output.append( String.format(
			"	\"%s\": {\n"+
			"		\"requirePath\": \"%s\",\n"+
			"		\"dependencies\": [%s]\n"+
			"	}", serviceSourceModule.getPrimaryRequirePath(), resolvedServiceSourceModule.getPrimaryRequirePath(), stringifyDependantAssets(dependantAssets) 
			) );
		}
	}
	
	private static Object stringifyDependantAssets(List<Asset> dependantAssets)
	{
		StringBuffer output = new StringBuffer();
		for (Asset asset : dependantAssets) {
			String assetPrimaryRequirePath = asset.getPrimaryRequirePath();
			if (!assetPrimaryRequirePath.startsWith("service!")) {
				continue;
			}
			
			if (output.length() != 0) {
				output.append(", ");
			}
			output.append(assetPrimaryRequirePath);
		}
		return output.toString();
	}

	private static Asset resolveService(List<AliasDefinition> aliasDefinitions, SourceModule serviceSourceModule, BundlableNode bundlableNode)
	{
		for (AliasDefinition aliasDefinition : aliasDefinitions) {
			String serviceRequireSuffix = serviceSourceModule.getPrimaryRequirePath().replaceFirst("service!", "");
			
			if (aliasDefinition.getName().equals(serviceRequireSuffix)) {
				return bundlableNode.asset(aliasDefinition.getRequirePath());
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
