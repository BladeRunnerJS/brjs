package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.Workbench;
import org.bladerunnerjs.api.spec.engine.BuilderChainer;
import org.bladerunnerjs.api.spec.engine.BundlableNodeBuilder;
import org.bladerunnerjs.api.spec.engine.SpecTest;

public class WorkbenchBuilder extends BundlableNodeBuilder<Workbench<?>>
{
	private final Workbench<?> workbench;
	
	public WorkbenchBuilder(SpecTest modelTest, Workbench<?> workbench)
	{
		super(modelTest, workbench);
		this.workbench = workbench;
	}
	
	public BuilderChainer resourceFileRefersTo(String resourceFileName, String className) throws Exception 
	{
		writeToFile(workbench.assetLocation("resources").file(resourceFileName), "<root refs='" + className + "'/>");
		
		return builderChainer;
	}
	
	public BuilderChainer hasAlias(String aliasName, String classRef) throws Exception 
	{
		writeToFile(workbench.aliasesFile().getUnderlyingFile(),
			"<aliases xmlns='http://schema.caplin.com/CaplinTrader/aliases'>" +
			"	<alias name='" + aliasName + "' class='" + classRef + "'/>" +
			"</aliases>");
		
		return builderChainer;
	}
}