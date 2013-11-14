package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.model.exception.ModelOperationException;


public interface SourceFile extends LinkedAssetFile {
	String getRequirePath();
	List<AssetLocation> getAssetLocations();  //TODO: remove this, move the functionality into resources
	List<SourceFile> getOrderDependentSourceFiles() throws ModelOperationException;
}
