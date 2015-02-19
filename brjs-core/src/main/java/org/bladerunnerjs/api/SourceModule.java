package org.bladerunnerjs.api;

import java.util.List;

import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.BundlableNode;

/**
 * A source file, typically JavaScript (.js) files that live in a 'src' directory.
 *
 */
public interface SourceModule extends LinkedAsset {	
	
	boolean isEncapsulatedModule();
	boolean isGlobalisedModule();
	
	/**
	 * Returns the list of all dependencies required by the source-module before it's able to define itself.
	 * 
	 * @param bundlableNode The bundlable node for which a bundle-set is being generated.
	 */
	List<Asset> getPreExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException;
	
	/**
	 * Returns the list of dependencies that happen to be required at define-time, but which are not needed for the
	 * source-module to actually define itself.
	 * 
	 * @param bundlableNode The bundlable node for which a bundle-set is being generated.
	 */
	List<Asset> getPostExportDefineTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException;
	
	/**
	 * Returns the list of dependencies that are known not to be needed by the source-module until use-time.
	 * 
	 * @param bundlableNode The bundlable node for which a bundle-set is being generated.
	 */
	List<Asset> getUseTimeDependentAssets(BundlableNode bundlableNode) throws ModelOperationException;
	
	List<AssetLocation> assetLocations();
}
