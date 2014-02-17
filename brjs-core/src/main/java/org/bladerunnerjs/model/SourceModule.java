package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.model.exception.ModelOperationException;

/**
 * A source file, typically JavaScript (.js) files that live in a 'src' directory.
 *
 */
public interface SourceModule extends LinkedAsset {
	String getRequirePath();
	String getClassname();
	
	boolean isEncapsulatedModule();
	
	void addPatch(SourceModulePatch patch);
	
	/**
	 * Returns a list of source files that *must* precede this source file in the output 
	 * @param bundlableNode TODO
	 */
	List<SourceModule> getOrderDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException;
}
