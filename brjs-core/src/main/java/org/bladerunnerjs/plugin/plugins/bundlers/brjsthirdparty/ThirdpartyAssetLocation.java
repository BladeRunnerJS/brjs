package org.bladerunnerjs.plugin.plugins.bundlers.brjsthirdparty;

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
	public String getJsStyle() {
		return ThirdpartyAssetLocation.class.getSimpleName();
	}
	
	@Override
	public <A extends Asset> List<A> obtainMatchingAssets(AssetFilter assetFilter, Class<A> assetListClass, Class<? extends A> assetClass) throws AssetFileInstantationException {
		List<A> assets = new ArrayList<>();
		
		try {
			// TODO: this makes no sense as assets are still dependent on an associated file, whereas they shouldn't be
			if(assetFilter.accept("lib.js") && !manifest.getJs().isEmpty()) {
				assets.add(obtainAsset(dir(), assetClass));
			}
			
			for(String cssAssetName : manifest.getCss()) {
				if(assetFilter.accept(cssAssetName)) {
					assets.add(obtainAsset(file(cssAssetName), assetClass));
				}
			}
		}
		catch (ConfigException e) {
			throw new RuntimeException(e);
		}
		
		return assets;
	}
}
