package org.bladerunnerjs.model;

public class SourceModuleReference {
	private String requirePath;
	
	public SourceModuleReference(String requirePath) {
		this.requirePath = requirePath;
	}
	
	public String getRequirePath() {
		return requirePath;
	}
}
