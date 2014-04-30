package org.bladerunnerjs.plugin.plugins.brjsconformant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetFilter;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.InstantiatedBRJSNode;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.RootAssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.utility.JsStyleUtility;

public class BRJSConformantRootAssetLocation extends InstantiatedBRJSNode implements RootAssetLocation {
	private final List<LinkedAsset> emptyLinkedAssetList = new ArrayList<>();
	private final List<Asset> emptyAssetList = new ArrayList<>();
	private final List<SourceModule> emptySourceModulesList = new ArrayList<>();
	private final List<AssetLocation> emptyAssetLocationList = new ArrayList<>();
	private AliasDefinitionsFile aliasDefinitionsFile;
	private BRLibManifest libManifest;
	
	private final MemoizedValue<String> jsStyle = new MemoizedValue<>("AssetLocation.jsStyle", root(), dir());
	
	public BRJSConformantRootAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
		
		if(assetContainer() instanceof JsLib) {
			try {
				libManifest = new BRLibManifest((JsLib) assetContainer());
			}
			catch (ConfigException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException {
		// do nothing
	}
	
	@Override
	public String jsStyle() {
		return jsStyle.value(() -> {
			return JsStyleUtility.getJsStyle(dir());
		});
	}
	
	@Override
	public String requirePrefix() {
		if (!libManifest.manifestExists()) {
			return ((JsLib) assetContainer()).getName();
		}
		
		try {
			return libManifest.getRequirePrefix();
		}
		catch (ConfigException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	@Override
	public void assertIdentifierCorrectlyNamespaced(String identifier) throws NamespaceException, RequirePathException {
		throw new RuntimeException("BRJSConformantRootAssetLocation.assertIdentifierCorrectlyNamespaced() should never be invoked");
	}
	
	@Override
	public SourceModule sourceModule(String requirePath) throws RequirePathException {
		throw new RuntimeException("BRJSConformantRootAssetLocation.sourceModule() should never be invoked");
	}
	
	@Override
	public AliasDefinitionsFile aliasDefinitionsFile() {
		if(aliasDefinitionsFile == null) {
			aliasDefinitionsFile = new AliasDefinitionsFile(assetContainer(), dir(), "aliasDefinitions.xml");
		}
		
		return aliasDefinitionsFile;
	}
	
	@Override
	public List<LinkedAsset> linkedAssets() {
		return emptyLinkedAssetList;
	}
	
	@Override
	public List<Asset> bundlableAssets(AssetPlugin assetProducer) {
		return emptyAssetList;
	}
	
	@Override
	public List<SourceModule> sourceModules() {
		return emptySourceModulesList;
	}
	
	@Override
	public AssetContainer assetContainer() {
		return (AssetContainer) parentNode();
	}
	
	@Override
	public List<AssetLocation> dependentAssetLocations() {
		return emptyAssetLocationList ;
	}
	
	@Override
	public <A extends Asset> A obtainAsset(Class<? extends A> assetClass, File dir, String assetName) throws AssetFileInstantationException {
		throw new RuntimeException("BRJSConformantRootAssetLocation.obtainAsset() should never be invoked");
	}
	
	@Override
	public <A extends Asset> List<A> obtainMatchingAssets(AssetFilter assetFilter, Class<A> assetClass, Class<? extends A> instantiateAssetClass) throws AssetFileInstantationException {
		return new ArrayList<A>();
	}

	@Override
	public void setNamespace(String namespace) throws ConfigException {
		libManifest.setRequirePrefix(namespace.replace('.', '/'));
	}
}
