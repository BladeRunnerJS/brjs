package org.bladerunnerjs.testing.specutility;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.testing.specutility.engine.AssetContainerBuilder;
import org.bladerunnerjs.testing.specutility.engine.BuilderChainer;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;

public class WorkbenchBuilder extends AssetContainerBuilder<Workbench> 
{
	private final Workbench workbench;
	
	public WorkbenchBuilder(SpecTest modelTest, Workbench workbench)
	{
		super(modelTest, workbench);
		this.workbench = workbench;
	}
	
	public BuilderChainer indexPageRefersTo(String className) throws Exception 
	{
		FileUtils.write(workbench.file("index.html"), className, "UTF-8");
		
		return builderChainer;
	}
	
	public BuilderChainer resourceFileRefersTo(String resourceFileName, String className) throws Exception 
	{
		FileUtils.write(workbench.assetLocation("resources").file(resourceFileName), "<root refs='" + className + "'/>", "UTF-8");
		
		return builderChainer;
	}
	
	public BuilderChainer hasAlias(String aliasName, String classRef) throws Exception 
	{
		FileUtils.write(workbench.aliasesFile().getUnderlyingFile(),
			"<aliases xmlns='http://schema.caplin.com/CaplinTrader/aliases'>" +
			"	<alias name='" + aliasName + "' class='" + classRef + "'/>" +
			"</aliases>", "UTF-8");
		
		return builderChainer;
	}
	
	public BuilderChainer indexPageHasContent(String content) throws Exception 
	{
		return indexPageRefersTo(content);
	}

	public BuilderChainer indexPageRequires(String requirePath) throws Exception
	{
		FileUtils.write(workbench.file("index.html"), "require('"+requirePath+"');", "UTF-8");
		
		return builderChainer;
	}
}