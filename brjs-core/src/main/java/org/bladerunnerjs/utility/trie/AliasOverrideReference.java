package org.bladerunnerjs.utility.trie;

import org.bladerunnerjs.aliasing.AliasOverride;

public class AliasOverrideReference implements AliasReference {
	
	private final AliasOverride alias;

	public AliasOverrideReference(AliasOverride alias) {
		this.alias = alias;
	}
	
	public String getName() {
		return alias.getName();
	}
}