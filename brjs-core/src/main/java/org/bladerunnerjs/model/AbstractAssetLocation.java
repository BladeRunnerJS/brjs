package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.InvalidRequirePathException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.UnresolvableRelativeRequirePathException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.utility.AssetLocator;
import org.bladerunnerjs.plugin.utility.Assets;
import org.bladerunnerjs.utility.JsStyleUtility;
import org.bladerunnerjs.utility.NamespaceUtility;
import org.bladerunnerjs.utility.RelativePathUtility;

public abstract class AbstractAssetLocation extends InstantiatedBRJSNode implements AssetLocation {
	protected final AssetContainer assetContainer;
	protected final FileInfo dirInfo;
	
	private final MemoizedValue<String> requirePrefix;
	private final AssetLocator assetLocator;
	private List<AssetLocation> dependentAssetLocations = new ArrayList<>();
	private final Map<String, SourceModule> sourceModules = new HashMap<>();
	private AliasDefinitionsFile aliasDefinitionsFile;
	private final Assets emptyAssets;
	private final MemoizedValue<String> jsStyle = new MemoizedValue<>("AssetLocation.jsStyle", root(), dir());
	
	public AbstractAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation... dependentAssetLocations) {
		super(rootNode, parent, dir);
		
		dirInfo = root().getFileInfo(dir);
		assetLocator = new AssetLocator(this);
		emptyAssets = new Assets(root());
		this.assetContainer = (AssetContainer) parent;
		requirePrefix = new MemoizedValue<>("AssetLocation.requirePrefix", root(), dir(), assetContainer.app().file("app.conf"), root().conf().file("bladerunner.conf"));
		this.dependentAssetLocations.addAll( Arrays.asList(dependentAssetLocations) );
	}
	
	protected abstract List<File> getCandidateFiles();
	
	@Override
	public String requirePrefix() {
		return requirePrefix.value(() -> {
			String relativeRequirePath = RelativePathUtility.get(assetContainer.dir(), dir());
			
			return assetContainer.requirePrefix() + "/" + relativeRequirePath;
		});
	}
	
	@Override
	public AssetContainer assetContainer() {
		return assetContainer;
	}
	
	@Override
	public List<AssetLocation> dependentAssetLocations() {
		return dependentAssetLocations;
	}
	
	@Override
	public AliasDefinitionsFile aliasDefinitionsFile() {		
		if(aliasDefinitionsFile == null) {
			aliasDefinitionsFile = new AliasDefinitionsFile(this, dir(), "aliasDefinitions.xml");
		}
		
		return aliasDefinitionsFile;
	}
	
	@Override
	public List<LinkedAsset> linkedAssets() {
		return assets().linkedAssets;
	}
	
	@Override
	public List<Asset> bundlableAssets(AssetPlugin assetPlugin) {
		return assets().pluginAssets.get(assetPlugin);
	}
	
	@Override
	public List<SourceModule> sourceModules() {
		return assets().sourceModules;
	}
	
	@Override
	public SourceModule sourceModule(String requirePath) throws RequirePathException {
		String canonicalRequirePath = canonicaliseRequirePath(requirePrefix(), requirePath);
		
		if(sourceModules.containsKey(requirePath)) {
			return sourceModules.get(requirePath);
		}
		
		SourceModule sourceModule = findSourceModuleWithRequirePath(assetContainer().app().getAllAssetContainers(), canonicalRequirePath);
		
		if(sourceModule != null) {
			sourceModules.put(requirePath, sourceModule);
			return sourceModule;
		}
		
		throw new InvalidRequirePathException("Unable to find SourceModule for require path '" + requirePath
			+ "'. It either does not exist or it is outside of the scope for this request.");
	}
	
	@Override
	public String jsStyle() {
		return jsStyle.value(() -> {
			return JsStyleUtility.getJsStyle(dir());
		});
	}
	
	@Override
	public void assertIdentifierCorrectlyNamespaced(String identifier) throws NamespaceException, RequirePathException {
		String namespace = NamespaceUtility.convertToNamespace(requirePrefix());
		
		if(assetContainer.isNamespaceEnforced() && !identifier.startsWith(namespace)) {
			throw new NamespaceException( "The identifier '" + identifier + "' is not correctly namespaced.\nNamespace '" + namespace + ".*' was expected.");
		}
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException {
		// do nothing
	}
	
	private SourceModule findSourceModuleWithRequirePath(List<AssetContainer> assetContainers, String requirePath)
	{
		for (AssetContainer assetContainer : assetContainers)
		{
			for (SourceModule sourceModule : assetContainer.sourceModules())
			{
				if (sourceModule.getRequirePath().equals(requirePath))
				{
					sourceModules.put(requirePath, sourceModule);
					return sourceModule;
				}
			}
		}
		return null;
	}
	
	private Assets assets() {
		return (!dirInfo.exists()) ? emptyAssets : assetLocator.assets(getCandidateFiles());
	}
	
	private String canonicaliseRequirePath(String requirePrefix, String requirePath) throws RequirePathException
	{
		List<String> requirePrefixParts = new LinkedList<String>( Arrays.asList(requirePrefix.split("/")) );
		List<String> requirePathParts = new LinkedList<String>( Arrays.asList(requirePath.split("/")) );
		
		if(!requirePath.contains("../") && !requirePath.contains("./")) {
			return requirePath;
		}
		
		Iterator<String> requirePathPartsIterator = requirePathParts.iterator();
		while(requirePathPartsIterator.hasNext()) {
			String pathPart = requirePathPartsIterator.next();
			
			switch (pathPart) {
				case ".":
					requirePathPartsIterator.remove();
					break;
				
				case "..":
					requirePathPartsIterator.remove();
					if (requirePrefixParts.size() > 0)
					{
						requirePrefixParts.remove( requirePrefixParts.size()-1 );						
					}
					else
					{
						String msg = String.format("Unable to continue up to parent require path, no more parents remaining. Require path of container was '%s', relative require path was '%s'", requirePrefix, requirePath);
						throw new UnresolvableRelativeRequirePathException(msg);
					}
					break;
				
				default:
					break;
			}
		}
		
		return StringUtils.join(requirePrefixParts, "/") + "/" + StringUtils.join(requirePathParts, "/");
	}
}
