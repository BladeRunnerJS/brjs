package org.bladerunnerjs.core.plugin.bundlesource;

import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.FileSet;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.NullFileSet;
import org.bladerunnerjs.model.Resources;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.SourceLocation;

public class NullFileSetFactory implements FileSetFactory {
	@Override
	public FileSet<SourceFile> getSourceFileSet(SourceLocation sourceLocation) {
		return new NullFileSet<>();
	}
	
	@Override
	public FileSet<LinkedAssetFile> getLinkedResourceFileSet(Resources resources) {
		return new NullFileSet<>();
	}
	
	@Override
	public FileSet<AssetFile> getResourceFileSet(Resources resources) {
		return new NullFileSet<>();
	}
}
