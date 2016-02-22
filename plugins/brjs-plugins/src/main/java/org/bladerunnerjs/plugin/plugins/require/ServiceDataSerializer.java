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

	private static final String servicePrefix = "service!";
	private static final String sourceModuleJsonSeparator = ",\n";
	private static final String sourceModuleJsonTemplate = "	\"%s\": {\n" +
			"		\"requirePath\": \"%s\",\n" +
			"		\"dependencies\": [%s]\n" +
			"	}";

	public static String createJson(BundleSet bundleSet) throws ModelOperationException
	{
		StringBuilder output = new StringBuilder();

		List<AliasDefinition> aliasDefinitions = getAliasDefinitions(bundleSet);
		List<Asset> serviceAssets = bundleSet.assets(servicePrefix);

		for (Asset asset : serviceAssets) {
			if (!(asset instanceof SourceModule)) {
				continue;
			}

			if (asset.getPrimaryRequirePath().equals(ServiceDataSourceModule.PRIMARY_REQUIRE_PATH)) {
				continue;
			}

			final SourceModule serviceDefinition = (SourceModule) asset;
			final SourceModule serviceImplementation = resolveService(aliasDefinitions, serviceDefinition, bundleSet);

			if (serviceImplementation == null) {
				continue;
			}

			final ArrayList<Asset> dependencies = new ArrayList<>();
			addDependentAssets(bundleSet, serviceDefinition.getPrimaryRequirePath(), serviceImplementation, dependencies);
			appendServiceToJSON(output, serviceDefinition, serviceImplementation, dependencies);
		}

		if (output.length() > 0) {
			return "{\n" + output.toString() + "\n}";
		}

		return "{ }";
	}

	private static void addDependentAssets(BundleSet bundleSet, String serviceAssetPrimaryRequirePath, Asset currentAsset, List<Asset> acc) throws ModelOperationException {
		if (!(currentAsset instanceof LinkedAsset)) {
			return;
		}

		List<Asset> dependentAssets = ((LinkedAsset) currentAsset).getDependentAssets(bundleSet.bundlableNode());

		for (Asset dependentAsset : dependentAssets) {
			if (acc.contains(dependentAsset) || dependentAsset.getPrimaryRequirePath().equals(serviceAssetPrimaryRequirePath)) {
				continue;
			}
			acc.add(dependentAsset);
			addDependentAssets(bundleSet, serviceAssetPrimaryRequirePath, dependentAsset, acc);
		}
	}

	private static void appendServiceToJSON(StringBuilder output, SourceModule serviceSourceModule, SourceModule resolvedServiceSourceModule, List<Asset> dependentAssets) throws ModelOperationException {
		if (output.length() > 0) {
			output.append(sourceModuleJsonSeparator);
		}

		output.append(String.format(
			sourceModuleJsonTemplate,
			serviceSourceModule.getPrimaryRequirePath(),
			resolvedServiceSourceModule.getPrimaryRequirePath(),
			stringifyDependentServices(dependentAssets)
		));
	}

	private static Object stringifyDependentServices(List<Asset> dependentAssets)
	{
		List<String> assetStrings = new LinkedList<>();

		for (Asset asset : dependentAssets) {
			String primaryRequirePath = asset.getPrimaryRequirePath();
			if (!primaryRequirePath.startsWith(servicePrefix) || primaryRequirePath.endsWith("$data")) {
				continue;
			}

			String serviceAlias = primaryRequirePath.replaceFirst(servicePrefix, "");

			assetStrings.add("			\""+serviceAlias+"\"");
		}

		if (assetStrings.isEmpty()) {
			return "";
		}
		// whitespace is intentional so we can output well formatted JSON
		return "\n"+StringUtils.join(assetStrings, ",\n")+"\n		";
	}

	private static SourceModule resolveService(List<AliasDefinition> aliasDefinitions, SourceModule serviceSourceModule, BundleSet bundleSet)
	{
		for (AliasDefinition aliasDefinition : aliasDefinitions) {
			String serviceRequireSuffix = serviceSourceModule.getPrimaryRequirePath().replaceFirst(servicePrefix, "");
			String aliasDefinitionRequirePath = aliasDefinition.getRequirePath();

			if (aliasDefinitionRequirePath == null || !aliasDefinition.getName().equals(serviceRequireSuffix)) {
				continue;
			}

			List<SourceModule> sourceModules = bundleSet.sourceModules(SourceModule.class);

			SourceModule module = null;
			for (SourceModule _module: sourceModules) {
				if (!_module.getPrimaryRequirePath().equals(aliasDefinitionRequirePath)) {
					continue;
				}
				module = _module;
				break;
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
