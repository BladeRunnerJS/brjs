package org.bladerunnerjs.model;

import java.io.File;
import java.util.*;

import org.apache.commons.lang3.*;
import org.bladerunnerjs.aliasing.*;
import org.bladerunnerjs.aliasing.aliasdefinitions.*;
import org.bladerunnerjs.memoization.*;
import org.bladerunnerjs.model.engine.*;
import org.bladerunnerjs.model.exception.*;
import org.bladerunnerjs.model.exception.modelupdate.*;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.utility.*;
import org.bladerunnerjs.utility.*;

// TODO Java 8 (1.8.0-b123) compiler throws errors when this class is named 'AbstractAssetLocation'
public abstract class TheAbstractAssetLocation extends InstantiatedBRJSNode implements AssetLocation {
	private final AssetLocation parentAssetLocation;
	private final AssetContainer assetContainer;
	private final FileInfo dirInfo;
	
	private final AssetLocator assetLocator;
	private List<AssetLocation> dependentAssetLocations = new ArrayList<>();
	private AliasDefinitionsFile aliasDefinitionsFile;
	private Map<String, AliasDefinitionsFile> aliasDefinitionsFilesMap = new HashMap<>();
	private final Assets emptyAssets;
	private final MemoizedValue<String> jsStyle = new MemoizedValue<>(dir()+" jsStyle", root(), dir());
	
	public TheAbstractAssetLocation(RootNode rootNode, AssetContainer assetContainer, File dir, AssetLocation parentAssetLocation, AssetLocation... dependentAssetLocations) {
		super(rootNode, assetContainer, dir);
		
		dirInfo = root().getFileInfo(dir);
		assetLocator = new AssetLocator(this);
		emptyAssets = new Assets(root());
		this.parentAssetLocation = parentAssetLocation;
		this.assetContainer = assetContainer;
		this.dependentAssetLocations.addAll( Arrays.asList(dependentAssetLocations) );
	}
	
	protected abstract List<File> getCandidateFiles();
	
	@Override
	public String requirePrefix() {
		String requirePrefix = (parentAssetLocation == null) ? assetContainer.requirePrefix() : parentAssetLocation.requirePrefix();
		return requirePrefix + "/" + dir().getName();
	}
	
	@Override
	public AssetLocation parentAssetLocation() {
		return parentAssetLocation;
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
	public List<AliasDefinitionsFile> aliasDefinitionsFiles() {
		List<AliasDefinitionsFile> aliasDefinitionsFiles = new ArrayList<>();
		
		if(aliasDefinitionsFile().getUnderlyingFile().exists()) {
			aliasDefinitionsFiles.add(aliasDefinitionsFile());
		}
		
		// TODO: fix this dependency from the model to plug-in code (ResourcesAssetLocation)
		//       we instead need a way to either know this asset-location has a deep directory structure, or have way of getting it to list it's nested directories
		if(dir().exists() && (this instanceof ResourcesAssetLocation)) {
			for(File dir : root().getFileInfo(dir()).nestedDirs()) {
				if(new File(dir, "aliasDefinitions.xml").exists()) {
					String dirPath = dir.getAbsolutePath();
					
					if(!aliasDefinitionsFilesMap.containsKey(dirPath)) {
						aliasDefinitionsFilesMap.put(dirPath, new AliasDefinitionsFile(this, dir, "aliasDefinitions.xml"));
					}
					
					aliasDefinitionsFiles.add(aliasDefinitionsFilesMap.get(dirPath));
				}
			}
		}
		
		return aliasDefinitionsFiles;
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
	
	private Assets assets() {
		return (!dirInfo.exists()) ? emptyAssets : assetLocator.assets(getCandidateFiles());
	}
	
	@Override
	public String canonicaliseRequirePath(String requirePath) throws RequirePathException
	{
		String requirePrefix = requirePrefix();
		
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
	
	protected FileInfo getDirInfo() {
		return dirInfo;
	}
	
}
