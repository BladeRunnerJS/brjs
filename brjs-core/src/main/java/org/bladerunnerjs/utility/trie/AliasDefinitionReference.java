package org.bladerunnerjs.utility.trie;

import org.bladerunnerjs.aliasing.AliasDefinition;

public class AliasDefinitionReference implements AliasReference {
	
	private final AliasDefinition alias;

	public AliasDefinitionReference(AliasDefinition alias) {
		this.alias = alias;
	}
	
	public String getName() {
		return alias.getName();
	}
}