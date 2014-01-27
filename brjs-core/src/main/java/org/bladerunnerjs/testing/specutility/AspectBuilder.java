package org.bladerunnerjs.testing.specutility;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.AssetContainerBuilder;
import org.bladerunnerjs.testing.specutility.engine.BuilderChainer;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


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
		FileUtils.write(aspect.assetLocation("resources").file(resourceFileName), "<root refs='" + className + "'/>");
		
		return builderChainer;
	}
	
	public BuilderChainer sourceResourceFileRefersTo(String resourceFileName, String className) throws Exception {
		FileUtils.write(aspect.assetLocation("src").file(resourceFileName), "<root refs='" + className + "'/>");
		
		return builderChainer;
	}
	
	public BuilderChainer indexPageHasContent(String content) throws Exception {
		return indexPageRefersTo(content);
	}

	public BuilderChainer indexPageRequires(JsLib thirdpartyLib) throws Exception
	{
		return indexPageRequires(thirdpartyLib.getName());
	}
	
	public BuilderChainer indexPageRequires(String requirePath) throws Exception
	{
		FileUtils.write(aspect.file("index.html"), "require('"+requirePath+"');");
		
		return builderChainer;
	}
}
