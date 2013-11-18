package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.model.exception.ModelOperationException;

/**
 * A source file, typically JavaScript (.js) files that live in a 'src' directory.
 *
 */
public interface SourceFile extends LinkedAssetFile {
	String getRequirePath();
	List<SourceFile> getOrderDependentSourceFiles() throws ModelOperationException;
}
