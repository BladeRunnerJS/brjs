package org.bladerunnerjs.model;

public class FullyQualifiedLinkedAssetFileSetFactory implements AssetFileFactory<LinkedAssetFile> {
	@Override
	public LinkedAssetFile createFile(SourceLocation sourceLocation, String filePath) {
		return new FullyQualifiedLinkedAssetFile(sourceLocation, filePath);
	}
}
