package org.bladerunnerjs.utility.deps;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.api.LinkedAsset;

public class DependencyInfo {
	public Map<LinkedAsset, Set<LinkedAsset>> map = new LinkedHashMap<>();
	public Map<LinkedAsset, Set<LinkedAsset>> staticDeps = new LinkedHashMap<>();
	public Set<LinkedAsset> seedAssets = new LinkedHashSet<>();
}
