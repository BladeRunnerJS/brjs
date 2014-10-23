package org.bladerunnerjs.model.exception;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.model.SourceModule;

import com.google.common.base.Joiner;

public class CircularDependencyException extends ModelOperationException {
	private static final long serialVersionUID = 1L;
	
	public CircularDependencyException(Set<SourceModule> sourceModules) {
		super("Circular dependency involving the classes '" + Joiner.on("', '").join(getRequirePaths(sourceModules)) + "' prevented the bundle from being written.");
	}
	
	private static List<String> getRequirePaths(Set<SourceModule> sourceModules) {
		List<String> requirePaths = new ArrayList<>();
		
		for (SourceModule sourceModule : sourceModules) {
			requirePaths.add(sourceModule.getPrimaryRequirePath());
		}
		
		return requirePaths;
	}
}
