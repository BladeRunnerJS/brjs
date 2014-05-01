package org.bladerunnerjs.plugin.plugins.bundlers.thirdparty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.DeepAssetLocation;
import org.bladerunnerjs.model.JsLib;
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
	
	protected List<File> getCandidateFiles() {
		try {
			List<File> assetFiles = new ArrayList<>(manifest.getCssFiles());
			assetFiles.add(file("library.manifest"));
			
			return assetFiles;
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
	public String requirePrefix() {
		return ((JsLib) assetContainer()).getName();
	}
}
