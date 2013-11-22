package org.bladerunnerjs.model.aliasing;

// TODO: we will probably need to add scenarioName in our new bundling code
public class AliasDefinition
{
	private String className;
	private String interfaceName;
	private String name;
	private String groupName;
	
	public AliasDefinition(String name, String className, String interfaceName)
	{
		this.name = name;
		this.className = className;
		this.interfaceName = interfaceName;
	}
	
	public AliasDefinition(AliasDefinition alias)
	{
		name = alias.name;
		className = alias.className;
		interfaceName = alias.interfaceName;
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

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getGroup() {
		return groupName;
	}

	public void setGroup(String groupName) {
		this.groupName = groupName;
		
	}
	
	public String getScenario() {
		// TODO Auto-generated method stub
		return null;
	}
}
