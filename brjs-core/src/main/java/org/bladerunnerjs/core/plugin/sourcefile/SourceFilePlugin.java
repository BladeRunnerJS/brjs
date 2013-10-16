package org.bladerunnerjs.core.plugin.sourcefile;

import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.model.FileSet;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.SourceLocation;

public interface SourceFilePlugin extends Plugin {
	<SF extends SourceFile> FileSet<SF> getSourceFileSet(SourceLocation sourceLocation);
}
