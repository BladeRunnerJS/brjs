package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetFilter;
import org.bladerunnerjs.model.DeepAssetLocation;
import org.bladerunnerjs.model.NonBladerunnerJsLibManifest;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;

public class ThirdpartyAssetLocation extends DeepAssetLocation {
	private final NonBladerunnerJsLibManifest manifest;
	
	public ThirdpartyAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
		
		try {
			manifest = new NonBladerunnerJsLibManifest(this);
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String jsStyle() {
		return ThirdpartyAssetLocation.class.getSimpleName();
	}
	
	@Override
	public <A extends Asset> A obtainAsset(Class<? extends A> assetClass, File dir, String assetName) throws AssetFileInstantationException {
		A asset;
		
		if(!dir.equals(this.dir())) {
			throw new AssetFileInstantationException("directory '" + dir.getPath() + "' was not the asset location directory '" + dir().getPath() + "'.");
		}
		else if(!assetName.equals("")) {
			throw new AssetFileInstantationException("asset name '" + assetName + "' was not empty.");
		}
		else {
			asset = assetLocator.obtainAsset(assetClass, dir, assetName);
		}
		
		return asset;
	}
	
	@Override
	public <A extends Asset> List<A> obtainMatchingAssets(AssetFilter assetFilter, Class<A> assetListClass, Class<? extends A> assetClass) throws AssetFileInstantationException {
		List<A> assets = new ArrayList<>();
		
		try {
			for(File cssAssetFile : manifest.getCssFiles()) {
				if(assetFilter.accept(cssAssetFile.getName())) {
					assets.add(assetLocator.obtainAsset(assetClass, cssAssetFile.getParentFile(), cssAssetFile.getName()));
				}
			}
		}
		catch (ConfigException e) {
			throw new RuntimeException(e);
		}
		
		return assets;
	}
}
