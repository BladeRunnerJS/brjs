package org.bladerunnerjs.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.UnresolvableRequirePathException;

public class SourceModuleResolver {
	private final BundlableNode bundlableNode;
	private final AssetLocation assetLocation;
	private final String sourceRequirePath;
	private final MemoizedValue<List<SourceModule>> sourceModules;
	
	public SourceModuleResolver(BundlableNode bundlableNode, AssetLocation assetLocation, String sourceRequirePath, File... watchItems) {
		this.bundlableNode = bundlableNode;
		this.assetLocation = assetLocation;
		this.sourceRequirePath = sourceRequirePath;
		
		sourceModules = new MemoizedValue<>("SourceModuleResolver.sourceModules", bundlableNode.root(), watchItems);
	}
	
	public List<SourceModule> getSourceModules(Collection<String> requirePaths) throws RequirePathException {
		return sourceModules.value(() -> {
			Set<SourceModule> dependentSourceModules = new LinkedHashSet<>();
			
			for(String requirePath : requirePaths) {
				SourceModule sourceModule = assetLocation.sourceModule(requirePath);
				
				if(sourceModule == null) {
					throw new UnresolvableRequirePathException(requirePath, sourceRequirePath);
				}
				
				SourceModule bundlableSourceModule = bundlableNode.getSourceModule(sourceModule.getRequirePath());
				dependentSourceModules.add(bundlableSourceModule);
			}
			
			return new ArrayList<SourceModule>( dependentSourceModules );
		});
	}
}
