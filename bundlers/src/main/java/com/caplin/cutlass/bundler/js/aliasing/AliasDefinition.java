package com.caplin.cutlass.bundler.js.aliasing;

public class AliasDefinition extends org.bladerunnerjs.aliasing.AliasDefinition
{
	private String groupName;
	
	public AliasDefinition(String name, String className, String interfaceName) {
		super(name, className, interfaceName);
	}
	
	public AliasDefinition(org.bladerunnerjs.aliasing.AliasDefinition alias)
	{
		super(alias.getName(), alias.getClassName(), alias.getInterfaceName());
	}
	
	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getGroup() {
		return groupName;
	}

	public void setGroup(String groupName) {
		this.groupName = groupName;
		
	}
}
