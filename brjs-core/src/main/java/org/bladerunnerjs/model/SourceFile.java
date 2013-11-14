package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.model.exception.ModelOperationException;


public interface SourceFile extends LinkedAssetFile {
	String getRequirePath();
	List<AssetLocation> getAssetLocations();  //TODO: this method should be deleted -- users can do sourceFile.getAssetLocation().getDependentAssetLocations() instead
	List<SourceFile> getOrderDependentSourceFiles() throws ModelOperationException;
}
