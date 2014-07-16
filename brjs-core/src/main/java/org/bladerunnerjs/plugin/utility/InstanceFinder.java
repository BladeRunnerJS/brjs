package org.bladerunnerjs.plugin.utility;

import java.util.List;

import org.bladerunnerjs.model.SourceModule;

public class InstanceFinder {
	public static boolean containsInstance(List<SourceModule> sourceModules, Class<? extends SourceModule> sourceModuleClass) {
		boolean containsInstance = false;
		
		for (SourceModule sourceModule : sourceModules) {
			if (sourceModuleClass.isInstance(sourceModule)) {
				containsInstance = true;
				break;
			}
		}
		
		return containsInstance;
	}
}
