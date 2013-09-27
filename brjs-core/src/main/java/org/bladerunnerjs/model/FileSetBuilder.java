package org.bladerunnerjs.model;

public class FileSetBuilder<AF extends AssetFile> {
	private final SourceLocation sourceLocation;
	private String[] includePaths;
	private String[] excludePaths;
	private final Class<AF> assetFileClass;
	
	public FileSetBuilder(Class<AF> assetFileClass, SourceLocation sourceLocation) {
		this.assetFileClass = assetFileClass;
		this.sourceLocation = sourceLocation;
	}
	
	public static FileSetBuilder<AssetFile> createAssetFileSetForDir(SourceLocation sourceLocation) {
		return new FileSetBuilder<AssetFile>(AssetFile.class, sourceLocation);
	}
	
	public static FileSetBuilder<LinkedAssetFile> createLinkedAssetFileSetForDir(SourceLocation sourceLocation) {
		return new FileSetBuilder<LinkedAssetFile>(LinkedAssetFile.class, sourceLocation);
	}
	
	public static FileSetBuilder<SourceFile> createSourceFileSetForDir(SourceLocation sourceLocation) {
		return new FileSetBuilder<SourceFile>(SourceFile.class, sourceLocation);
	}
	
	public FileSetBuilder<AF> includingPaths(String... includePaths) {
		this.includePaths = includePaths;
		return this;
	}
	
	public FileSetBuilder<AF> excludingPaths(String... excludePaths) {
		this.excludePaths = excludePaths;
		return this;
	}
	
	public FileSet<AF> build() {
		return new FileSet<AF>(assetFileClass, sourceLocation, includePaths, excludePaths);
	}
}
