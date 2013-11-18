package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.model.exception.ModelOperationException;

/**
 * An extension of AssetFile, represents any AssetFile that could depend on other AssetFiles. 
 * For example an XML file that might reference a source file class.
 * 
 */
public interface LinkedAssetFile extends AssetFile {
	List<SourceFile> getDependentSourceFiles() throws ModelOperationException;
	List<AliasDefinition> getAliases() throws ModelOperationException;
}
