package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;

public class SourceModuleResolver {
	public static List<SourceModule> getSourceModules(AssetLocation assetLocation, Collection<String> requirePaths, String sourceRequirePath, BundlableNode bundlableNode, boolean ignoreUnavailableSourceModules) throws RequirePathException {
		Set<SourceModule> dependentSourceModules = new LinkedHashSet<>();
		
		for(String requirePath : requirePaths) {
			SourceModule sourceModule = assetLocation.sourceModule(requirePath);
			
			if(sourceModule == null) {
				throw new UnresolvableRequirePathException(requirePath, sourceRequirePath);
			}
			
			try {
				SourceModule bundlableSourceModule = bundlableNode.getSourceModule(sourceModule.getRequirePath());
				
				dependentSourceModules.add(bundlableSourceModule);
			}
			catch(RequirePathException e) {
				if(!ignoreUnavailableSourceModules) {
					throw e;
				}
			}
		}
		
		return new ArrayList<SourceModule>( dependentSourceModules );
	}
}
