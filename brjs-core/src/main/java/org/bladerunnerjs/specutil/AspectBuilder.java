package org.bladerunnerjs.specutil;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.JsLib;
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
	
	public BuilderChainer indexPageHasContent(String content) throws Exception {
		return indexPageRefersTo(content);
	}

	public BuilderChainer indexPageRefersTo(JsLib thirdpartyLib) throws Exception
	{
		FileUtils.write(aspect.file("index.html"), "br.thirdparty('"+thirdpartyLib.getName()+"');");
		
		return builderChainer;
	}
}
