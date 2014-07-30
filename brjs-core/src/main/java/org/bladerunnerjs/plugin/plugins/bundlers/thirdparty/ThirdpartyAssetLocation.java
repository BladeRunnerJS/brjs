package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.ResourcesAssetLocation;
import org.bladerunnerjs.model.ThirdpartyLibManifest;
import org.bladerunnerjs.model.exception.ConfigException;

public final class ThirdpartyAssetLocation extends ResourcesAssetLocation {
	private final ThirdpartyLibManifest manifest;
	
	public ThirdpartyAssetLocation(BRJS root, AssetContainer assetContainer, File dir, AssetLocation parentAssetLocation) {
		super(root, assetContainer, dir, parentAssetLocation);
		
		try {
			manifest = new ThirdpartyLibManifest(this);
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	public ThirdpartyLibManifest getManifest(){
		return manifest;
	}
	
	protected List<File> getCandidateFiles() {
		try {
			List<File> assetFiles = new ArrayList<>(manifest.getCssFiles());
			assetFiles.add(file("thirdparty-lib.manifest"));
			
			return assetFiles;
		}
		catch(ConfigException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String requirePrefix() {
		return ((JsLib) assetContainer()).getName();
	}
	
	@Override
	public String jsStyle() {
		return ThirdpartyAssetLocation.class.getSimpleName();
	}
}
