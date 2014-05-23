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

public class SourceModuleResolver {
	private final BundlableNode bundlableNode;
	private final AssetLocation assetLocation;
	private final MemoizedValue<List<SourceModule>> sourceModules;
	
	public SourceModuleResolver(BundlableNode bundlableNode, AssetLocation assetLocation, File... watchItems) {
		this.bundlableNode = bundlableNode;
		this.assetLocation = assetLocation;
		
		sourceModules = new MemoizedValue<>("SourceModuleResolver.sourceModules", bundlableNode.root(), watchItems);
	}
	
	public List<SourceModule> getSourceModules(Collection<String> requirePaths) throws RequirePathException {
		return sourceModules.value(() -> {
			Set<SourceModule> sourceModules = new LinkedHashSet<>();
			
			for(String requirePath : requirePaths) {				
				String canonicalRequirePath = assetLocation.canonicaliseRequirePath(requirePath);
				SourceModule sourceModule = bundlableNode.getSourceModule(canonicalRequirePath);
				sourceModules.add(sourceModule);
			}
			
			return new ArrayList<SourceModule>( sourceModules );
		});
	}
}
