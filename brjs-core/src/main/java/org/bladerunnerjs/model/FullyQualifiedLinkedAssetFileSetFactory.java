package org.bladerunnerjs.model;

import java.io.File;

public class FullyQualifiedLinkedAssetFileSetFactory implements FileSetFactory<LinkedAssetFile> {
	@Override
	public LinkedAssetFile createFile(SourceLocation sourceLocation, File file) {
		return new FullyQualifiedLinkedAssetFile(sourceLocation, file);
	}
}
