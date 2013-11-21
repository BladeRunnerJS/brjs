package org.bladerunnerjs.specutil;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.specutil.engine.AssetContainerBuilder;
import org.bladerunnerjs.specutil.engine.BuilderChainer;
import org.bladerunnerjs.specutil.engine.SpecTest;

public class WorkbenchBuilder extends AssetContainerBuilder<Workbench> {
	private final Workbench workbench;
	
	public WorkbenchBuilder(SpecTest modelTest, Workbench workbench)
	{
		super(modelTest, workbench);
		this.workbench = workbench;
	}
	
	public BuilderChainer indexPageRefersTo(String className) throws Exception {
		FileUtils.write(workbench.file("index.html"), className);
		
		return builderChainer;
	}
	
	public BuilderChainer resourceFileRefersTo(String resourceFileName, String className) throws Exception {
		FileUtils.write(workbench.resources().file(resourceFileName), "<root refs='" + className + "'/>");
		
		return builderChainer;
	}
	
	public BuilderChainer hasAlias(String aliasName, String classRef) throws Exception {
		FileUtils.write(workbench.aliasesFile(),
			"<aliases xmlns='http://schema.caplin.com/CaplinTrader/aliases'>" +
			"	<alias name='" + aliasName + "' class='" + classRef + "'/>" +
			"</aliases>");
		
		return builderChainer;
	}
	
	public BuilderChainer indexPageHasContent(String content) throws Exception {
		return indexPageRefersTo(content);
	}
}