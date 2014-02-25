package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.model.exception.ModelOperationException;


public interface SourceAsset extends LinkedAsset
{
	String getRequirePath();
	String getClassname();
	
	boolean isEncapsulatedModule();
	
	/**
	 * Returns a list of source files that *must* precede this source file in the output 
	 * @param bundlableNode TODO
	 */
	List<SourceModule> getOrderDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException;
}
