package org.bladerunnerjs.api.utility;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.model.exception.NamespaceException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.DirectoryLinkedAsset;


public class RequirePathUtility
{

	public static void assertIdentifierCorrectlyNamespaced(Asset asset, String identifier) throws NamespaceException 
	{	
		AssetContainer assetContainer = asset.assetContainer();
		if (asset.file().isChildOf(assetContainer.file("resources"))) {
			assertIdentifierCorrectlyNamespaced(assetContainer, identifier);
			return;
		}
		
		String assetParentRequirePath = StringUtils.substringBeforeLast(calculateRequireSuffix(asset), "/");
		Asset parentAsset;
		while (assetParentRequirePath.length() > 0) {
			parentAsset = assetContainer.asset(assetParentRequirePath);
			if (parentAsset instanceof DirectoryLinkedAsset) {
				String namespace = calculateNamespace(assetParentRequirePath)+".";
				assertIdentifierCorrectlyNamespaced(assetContainer, namespace, identifier);
				return;
			}
			assetParentRequirePath = StringUtils.substringBeforeLast(assetParentRequirePath, "/");
		}	
	}
	
	public static void assertIdentifierCorrectlyNamespaced(AssetContainer assetContainer, String identifier) throws NamespaceException 
	{	
		String namespace = calculateNamespace(assetContainer.requirePrefix())+".";
		assertIdentifierCorrectlyNamespaced(assetContainer, namespace, identifier);
	}
	
	private static void assertIdentifierCorrectlyNamespaced(AssetContainer assetContainer, String namespace, String identifier) throws NamespaceException {
		if (assetContainer.isNamespaceEnforced() && !identifier.startsWith(namespace)) {
			throw new NamespaceException( "The identifier '" + identifier + "' is not correctly namespaced.\nNamespace '" + namespace + "*' was expected.");
		}
	}
	
	public static String calculateRequireSuffix(Asset asset) {
		return calculateRequireSuffix(asset.getPrimaryRequirePath());
	}
	
	public static String calculateRequireSuffix(String requirePath)
	{
		String requirePathSuffix = requirePath;
		for (String requirePrefixSeperator : Arrays.asList("!", ":")) {
			if (requirePathSuffix.contains(requirePrefixSeperator)) {
				requirePathSuffix = StringUtils.substringAfter(requirePathSuffix, requirePrefixSeperator);
			}
		}
		return requirePathSuffix;
	}
	
	public static String calculateNamespace(AssetContainer assetContainer) {
		return calculateNamespace(assetContainer.requirePrefix());
	}
	
	public static String calculateNamespace(Asset asset) {
		return calculateNamespace(asset.getPrimaryRequirePath());
	}
	
	public static String calculateNamespace(String requirePath) 
	{
		return calculateRequireSuffix(requirePath).replace("/", ".");
	}
	
}
