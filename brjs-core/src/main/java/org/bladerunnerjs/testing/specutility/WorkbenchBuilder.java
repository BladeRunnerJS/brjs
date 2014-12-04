package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.BuilderChainer;
import org.bladerunnerjs.testing.specutility.engine.BundlableNodeBuilder;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;

public class WorkbenchBuilder extends BundlableNodeBuilder<Workbench>
{
	private final Workbench workbench;
	
	public WorkbenchBuilder(SpecTest modelTest, Workbench workbench)
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
	
	public BuilderChainer indexPageHasContent(String content) throws Exception 
	{
		return indexPageRefersTo(content);
	}

	public BuilderChainer indexPageRequires(String requirePath) throws Exception
	{
		writeToFile(workbench.file("index.html"), "require('"+requirePath+"');");
		
		return builderChainer;
	}
}