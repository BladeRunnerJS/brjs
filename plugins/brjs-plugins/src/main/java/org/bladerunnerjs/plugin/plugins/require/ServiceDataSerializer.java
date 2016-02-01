package org.bladerunnerjs.plugin.plugins.require;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
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
			try {
				addDependentAssets(bundleSet, resolvedServiceSourceModule, dependencies);
			} catch (RequirePathException e) {
				e.printStackTrace();
			}
			appendServiceToJSON(output, serviceSourceModule, resolvedServiceSourceModule, dependencies);
		}
		if (output.length() > 0) {
			output.setLength( output.length() - sourceModuleJsonSeparator.length() ); // remove the final separator that was added above
			return "{\n"+output.toString()+"\n}";
		}
		return "{ }";
	}

	private static void addDependentAssets(BundleSet bundleSet, Asset asset, List<Asset> acc) throws ModelOperationException, RequirePathException {
		List<Asset> dependentAssets = bundleSet.bundlableNode().getLinkedAsset(asset.getPrimaryRequirePath()).getDependentAssets(bundleSet.bundlableNode());
		for (Asset dependentAsset : dependentAssets) {
			if (acc.contains(dependentAsset)) {
				continue;
			}
			acc.add(dependentAsset);
			addDependentAssets(bundleSet, dependentAsset, acc);
		}
	}

	private static void appendServiceToJSON(StringBuilder output, SourceModule serviceSourceModule, SourceModule resolvedServiceSourceModule, List<Asset> dependantAssets) throws ModelOperationException {
		output.append(String.format(
			"	\"%s\": {\n" +
			"		\"requirePath\": \"%s\",\n" +
			"		\"dependencies\": [%s]\n" +
			"	}", serviceSourceModule.getPrimaryRequirePath(), resolvedServiceSourceModule.getPrimaryRequirePath(), stringifyDependantAssets(dependantAssets)
		));
		output.append(sourceModuleJsonSeparator);
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

	private static SourceModule resolveService(List<AliasDefinition> aliasDefinitions, SourceModule serviceSourceModule, BundleSet bundleSet)
	{
		for (AliasDefinition aliasDefinition : aliasDefinitions) {
			String serviceRequireSuffix = serviceSourceModule.getPrimaryRequirePath().replaceFirst("service!", "");

			if (aliasDefinition.getRequirePath() != null && aliasDefinition.getName().equals(serviceRequireSuffix)) {
				// TODO: there's no way to get a single Asset matching a given require path, only those that match a 'prefix'
				List<Asset> matchingAssets = bundleSet.assets(Arrays.asList(SourceModule.class), aliasDefinition.getRequirePath());
				if (matchingAssets.size() > 0) {
					Asset asset = matchingAssets.get(0);
					if (asset instanceof SourceModule && !asset.getPrimaryRequirePath().equals(AliasDefinition.UNKNOWN_CLASS_REQUIRE_PATH)) {
						return (SourceModule) asset;
					}
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
