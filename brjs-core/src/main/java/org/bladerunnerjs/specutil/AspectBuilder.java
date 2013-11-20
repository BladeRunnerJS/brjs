package org.bladerunnerjs.specutil;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.specutil.engine.BuilderChainer;
import org.bladerunnerjs.specutil.engine.AssetContainerBuilder;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class AspectBuilder extends AssetContainerBuilder<Aspect> {
	private final Aspect aspect;
	
	public AspectBuilder(SpecTest modelTest, Aspect aspect)
	{
		super(modelTest, aspect);
		this.aspect = aspect;
	}
	
	public BuilderChainer indexPageRefersTo(String className) throws Exception {
		FileUtils.write(aspect.file("index.html"), className);
		
		return builderChainer;
	}
	
	public BuilderChainer resourceFileRefersTo(String resourceFileName, String className) throws Exception {
		FileUtils.write(aspect.resources().file(resourceFileName), "<root refs='" + className + "'/>");
		
		return builderChainer;
	}
	
	public BuilderChainer hasAlias(String aliasName, String classRef) throws Exception {
		FileUtils.write(aspect.aliasesFile(),
			"<aliasDefinitions xmlns='http://schema.caplin.com/CaplinTrader/aliasDefinitions'>" +
			"	<alias name='" + aliasName + "' defaultClass='" + classRef + "'/>" +
			"</aliasDefinitions>");
		
		return builderChainer;
	}
	
	public void indexPageHasContent(String string) {
		// TODO Auto-generated method stub
		
	}
}
