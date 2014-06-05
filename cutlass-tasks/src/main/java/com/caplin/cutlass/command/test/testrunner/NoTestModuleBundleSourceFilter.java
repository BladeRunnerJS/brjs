package com.caplin.cutlass.command.test.testrunner;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BundleSetFilter;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.TestAssetLocation;

public class NoTestModuleBundleSourceFilter extends BundleSetFilter {
	public List<SourceModule> filterSourceModules(List<SourceModule> sourceModules) {
		List<SourceModule> filteredSourceModules = new ArrayList<>();
		
		for(SourceModule sourceModule : sourceModules) {
			if(!(sourceModule.assetLocation() instanceof TestAssetLocation)) {
				filteredSourceModules.add(sourceModule);
			}
		}
		
		return filteredSourceModules;
	}
}
