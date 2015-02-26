package org.bladerunnerjs.plugin.bundlers.thirdparty;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.ThirdpartyLibManifest;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.ResourcesAssetLocation;

public final class ThirdpartyAssetLocation extends ResourcesAssetLocation {
	private final ThirdpartyLibManifest manifest;
	
	public ThirdpartyAssetLocation(BRJS root, AssetContainer assetContainer, MemoizedFile dir, AssetLocation parentAssetLocation) {
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
	
	public List<MemoizedFile> getCandidateFiles() {
		try {
			List<MemoizedFile> assetFiles = new ArrayList<>(manifest.getCssFiles());
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
