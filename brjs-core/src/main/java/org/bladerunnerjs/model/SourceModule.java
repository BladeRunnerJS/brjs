package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.model.exception.ModelOperationException;

/**
 * A source file, typically JavaScript (.js) files that live in a 'src' directory.
 *
 */
public interface SourceModule extends LinkedAsset {	
	
	boolean isEncapsulatedModule();
	boolean isGlobalisedModule();
	
	/**
	 * Returns a list of source files that are 'define time' dependencies of this source module.
	 * These dependencies *must* precede this source file in the output so they are available when the module is defined.
	 *  
	 * @param bundlableNode TODO
	 */
	List<Asset> getDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException;
	
	/**
	 * Returns a list of source files that are 'use time' dependencies of this source module.
	 * These dependencies can appear at any point in the output.
	 * 
	 * @param bundlableNode TODO
	 */
	List<Asset> getUseTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException;
	
	List<AssetLocation> assetLocations();
}
