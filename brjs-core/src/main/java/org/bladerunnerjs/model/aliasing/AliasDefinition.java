package org.bladerunnerjs.model.aliasing;

public class AliasDefinition {
	private final String name;
	private final String className;
	protected String interfaceName; // TODO: change back to 'final' and 'private' once we delete the old bundling code
	
	public AliasDefinition(String name, String className, String interfaceName) {
		this.name = name;
		this.className = className;
		this.interfaceName = interfaceName;
	}
	
	public String getInterfaceName() {
		return interfaceName;
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getName() {
		return name;
	}
}
