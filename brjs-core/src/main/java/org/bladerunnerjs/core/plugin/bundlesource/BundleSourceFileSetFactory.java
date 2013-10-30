package org.bladerunnerjs.core.plugin.bundlesource;

import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.FileSet;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.SourceLocation;

public interface BundleSourceFileSetFactory {
	FileSet<LinkedAssetFile> getSeedFileSet(BundlableNode bundlableNode);
	FileSet<SourceFile> getSourceFileSet(SourceLocation sourceLocation);
	FileSet<AssetFile> getResourceFileSet(SourceLocation sourceLocation);
}
