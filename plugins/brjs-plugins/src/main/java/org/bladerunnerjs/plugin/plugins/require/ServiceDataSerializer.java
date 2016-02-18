package org.bladerunnerjs.plugin.plugins.require;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasDefinition;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasException;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility;
import org.bladerunnerjs.plugin.require.AliasCommonJsSourceModule;


public class ServiceDataSerializer
{

	private static final String sourceModuleJsonSeparator = ",\n";

	public static String createJson(BundleSet bundleSet) throws ModelOperationException
	{
		StringBuilder output = new StringBuilder();

		List<AliasDefinition> aliasDefinitions = getAliasDefinitions(bundleSet);
		List<Asset> serviceAssets = bundleSet.assets("service!");
		for (Asset asset : serviceAssets) {
			if (!(asset instanceof SourceModule)) {
				continue;
			}
			if (asset.getPrimaryRequirePath().equals(ServiceDataSourceModule.PRIMARY_REQUIRE_PATH)) {
				continue;
			}

			final SourceModule serviceSourceModule = (SourceModule) asset;
			final SourceModule resolvedServiceSourceModule = resolveService(aliasDefinitions, serviceSourceModule, bundleSet);

			if (resolvedServiceSourceModule == null) {
				continue;
			}

			final ArrayList<Asset> dependencies = new ArrayList<>();
			addDependentAssets(bundleSet, resolvedServiceSourceModule, dependencies);
			appendServiceToJSON(output, serviceSourceModule, resolvedServiceSourceModule, dependencies);
		}
		if (output.length() > 0) {
			output.setLength( output.length() - sourceModuleJsonSeparator.length() ); // remove the final separator that was added above
			return "{\n"+output.toString()+"\n}";
		}

		return "{ }";
	}

	private static void addDependentAssets(BundleSet bundleSet, Asset asset, List<Asset> acc) throws ModelOperationException {
		List<Asset> dependentAssets = new ArrayList<>();

		if (asset instanceof LinkedAsset) {
			dependentAssets = ((LinkedAsset) asset).getDependentAssets(bundleSet.bundlableNode());
		} else{
			if (!dependentAssets.contains(asset)) {
				dependentAssets.add(asset);
			}
		}

		for (Asset dependentAsset : dependentAssets) {
			if (acc.contains(dependentAsset)) {
				continue;
			}
			acc.add(dependentAsset);
			addDependentAssets(bundleSet, dependentAsset, acc);
		}
	}

	private static void appendServiceToJSON(StringBuilder output, SourceModule serviceSourceModule, SourceModule resolvedServiceSourceModule, List<Asset> dependentAssets) throws ModelOperationException {
		output.append(String.format(
			"	\"%s\": {\n" +
			"		\"requirePath\": \"%s\",\n" +
			"		\"dependencies\": [%s]\n" +
			"	}", serviceSourceModule.getPrimaryRequirePath(), resolvedServiceSourceModule.getPrimaryRequirePath(), stringifyDependentAssets(dependentAssets)
		));
		output.append(sourceModuleJsonSeparator);
	}

	private static Object stringifyDependentAssets(List<Asset> dependentAssets)
	{
		List<String> assetStrings = new LinkedList<>();

		for (Asset asset : dependentAssets) {
			String primaryRequirePath = asset.getPrimaryRequirePath();
			if (!primaryRequirePath.startsWith("service!") || primaryRequirePath.endsWith("$data")) {
				continue;
			}

			String serviceAlias = StringUtils.substringAfter(primaryRequirePath, "service!");

			assetStrings.add("			\""+serviceAlias+"\"");
		}

		if (assetStrings.isEmpty()) {
			return "";
		}
		return "\n"+StringUtils.join(assetStrings, ",\n")+"\n		"; // whitespace is intentional so we can output well formatted JSON
	}

	private static SourceModule resolveService(List<AliasDefinition> aliasDefinitions, SourceModule serviceSourceModule, BundleSet bundleSet)
	{
		for (AliasDefinition aliasDefinition : aliasDefinitions) {
			String serviceRequireSuffix = serviceSourceModule.getPrimaryRequirePath().replaceFirst("service!", "");
			String aliasDefinitionRequirePath = aliasDefinition.getRequirePath();

			if (aliasDefinitionRequirePath == null || !aliasDefinition.getName().equals(serviceRequireSuffix)) {
				continue;
			}

			List<SourceModule> sourceModules = bundleSet.sourceModules(SourceModule.class);

			SourceModule module = null;
			for(SourceModule _module: sourceModules) {
				if (!_module.getPrimaryRequirePath().equals(aliasDefinitionRequirePath)) {
					continue;
				}
				module = _module;
			}

			if (!module.getPrimaryRequirePath().equals(AliasDefinition.UNKNOWN_CLASS_REQUIRE_PATH)) {
				return module;
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
