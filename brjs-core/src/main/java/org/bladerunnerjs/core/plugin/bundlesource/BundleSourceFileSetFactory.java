package org.bladerunnerjs.core.plugin.bundlesource;

import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.FileSet;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.SourceLocation;
import org.bladerunnerjs.model.Resources;

public interface BundleSourceFileSetFactory {
	FileSet<SourceFile> getSourceFileSet(SourceLocation sourceLocation);
	FileSet<LinkedAssetFile> getLinkedResourceFileSet(Resources resources);
	FileSet<AssetFile> getResourceFileSet(Resources resources);
}
