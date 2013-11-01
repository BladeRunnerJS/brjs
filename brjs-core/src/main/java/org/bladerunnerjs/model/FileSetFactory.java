package org.bladerunnerjs.model;

public interface FileSetFactory<AF extends AssetFile> {
	AF createFile(SourceLocation sourceLocation, String filePath);
}
