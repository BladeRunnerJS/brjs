package org.bladerunnerjs.api.utility;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.model.exception.NamespaceException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.DirectoryLinkedAsset;

import com.google.common.base.Joiner;


public class RequirePathUtility
{

	private static final Pattern matcherPattern = Pattern.compile("(require|br\\.Core\\.alias|caplin\\.alias|getAlias|getService)\\([ ]*[\"']([^)]+)[\"'][ ]*\\)");
	
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
	
	
	public static void addRequirePathsFromReader(Reader reader, Set<String> dependencies, List<String> aliases) throws IOException {
		StringWriter stringWriter = new StringWriter();
		IOUtils.copy(reader, stringWriter);
		
		Matcher m = matcherPattern.matcher(stringWriter.toString());
		while (m.find()) {
			String methodArgument = m.group(2);
			
			if (m.group(1).startsWith("require")) {
				String requirePath = methodArgument;
				dependencies.add(requirePath);
			}
			else if (m.group(1).startsWith("getService")){
				String serviceAliasName = methodArgument;
				dependencies.add("service!" + serviceAliasName);
			}
			else {
				String aliasName = methodArgument;
				aliases.add(aliasName);
			}
		}
	}

	public static String requirePathAssetList(List<Asset> assets) {
		return requirePathList(getRequirePaths(assets));
	}
	
	public static String requirePathList(List<String> requirePaths) {
		List<String> quotedRequirePaths = new ArrayList<>();
		
		for(String requirePath : requirePaths) {
			quotedRequirePaths.add("'" + requirePath + "'");
		}
		
		return "[" + Joiner.on(", ").join(quotedRequirePaths) + "]";
	}

	private static List<String> getRequirePaths(List<Asset> assets) {
		List<String> requirePaths = new ArrayList<>();
		
		for(Asset asset : assets) {
			requirePaths.add(asset.getPrimaryRequirePath());
		}
		
		return requirePaths;
	}
}
