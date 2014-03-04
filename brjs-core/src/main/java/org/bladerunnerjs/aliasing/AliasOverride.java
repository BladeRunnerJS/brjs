package org.bladerunnerjs.aliasing;

public class AliasOverride {
	private String className;
	private String name;
	
	public AliasOverride(String name, String className) {
		this.name = name;
		this.className = className;
	}
	
	public String getName() {
		return name;
	}
	
	public String getClassName() {
		return className;
	}
}
