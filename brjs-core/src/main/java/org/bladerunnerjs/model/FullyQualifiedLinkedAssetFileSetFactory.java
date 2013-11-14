package org.bladerunnerjs.model;

import java.io.File;

public class FullyQualifiedLinkedAssetFileSetFactory extends AbstractAssetFileFactory<LinkedAssetFile> {
	@Override
	public LinkedAssetFile createFile(AssetContainer assetContainer, File file) {
		return new FullyQualifiedLinkedAssetFile(assetContainer, file);
	}
}
