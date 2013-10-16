package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.model.exception.ModelOperationException;


public interface LinkedAssetFile extends AssetFile {
	void onSourceLocationsUpdated(List<SourceLocation> sourceLocations);
	List<SourceFile> getDependentSourceFiles() throws ModelOperationException;
	List<AliasDefinition> getAliases() throws ModelOperationException;
	boolean containsClassReferences() throws ModelOperationException;
}
