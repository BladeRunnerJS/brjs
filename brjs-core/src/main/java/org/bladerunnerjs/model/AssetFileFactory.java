package org.bladerunnerjs.model;

public interface AssetFileFactory<AF extends AssetFile> {
	AF createFile(SourceLocation sourceLocation, String filePath);
}
