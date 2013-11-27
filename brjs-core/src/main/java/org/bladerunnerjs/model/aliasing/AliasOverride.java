package org.bladerunnerjs.model.aliasing;

public class AliasOverride {
	private String className;
	private String name;
	
	public AliasOverride(String className, String name) {
		this.className = className;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public String getClassName() {
		return className;
	}
}
