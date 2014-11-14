package org.bladerunnerjs.aliasing;

public class AliasDefinition {
	private final String name;
	private final String className;
	private final String interfaceName;
	
	public AliasDefinition(String name, String className, String interfaceName) {
		this.name = name;
		this.className = className;
		this.interfaceName = interfaceName;
	}
	
	public boolean equals(AliasDefinition aliasDefinition) {
		return name.equals(aliasDefinition.getName()) && className.equals(aliasDefinition.getClassName()) && interfaceName.equals(aliasDefinition.getInterfaceName());
	}
	
	public String getInterfaceName() {
		return interfaceName;
	}
	
	public String getInterfaceRequirePath() {
		// TODO: we need to make require paths a first class concept in aliasing
		return (interfaceName == null) ? null : interfaceName.replaceAll("\\.", "/");
	}
	
	public String getClassName() {
		return className;
	}
	
	public String getRequirePath() {
		// TODO: we need to make require paths a first class concept in aliasing
		return className.replaceAll("\\.", "/");
	}
	
	public String getName() {
		return name;
	}
	
}
