package org.bladerunnerjs.utility.deps;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.model.LinkedAsset;

public class DependencyInfo {
	public Map<LinkedAsset, Set<LinkedAsset>> map = new LinkedHashMap<>();
	public Set<LinkedAsset> seedAssets = new HashSet<>();
	public Set<LinkedAsset> resourceAssets = new HashSet<>();
	public Map<String, AliasAsset> aliasAssets = new HashMap<>();
}
