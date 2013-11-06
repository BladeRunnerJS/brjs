package org.bladerunnerjs.model;

import java.io.File;

public class FullyQualifiedLinkedAssetFileSetFactory implements AssetFileFactory<LinkedAssetFile> {
	@Override
	public LinkedAssetFile createFile(SourceLocation sourceLocation, File file) {
		return new FullyQualifiedLinkedAssetFile(sourceLocation, file);
	}
}
