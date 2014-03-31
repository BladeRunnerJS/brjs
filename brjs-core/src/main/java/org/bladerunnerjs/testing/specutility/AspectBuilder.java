package org.bladerunnerjs.testing.specutility;

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
	
	public BuilderChainer indexPageRefersTo(String... classNames) throws Exception 
	{
		fileUtil.write(aspect.file("index.html"), generateStringClassReferencesContent(classNames));	
		
		return builderChainer;
	}

	public BuilderChainer resourceFileRefersTo(String resourceFileName, String... classNames) throws Exception 
	{
		fileUtil.write(aspect.assetLocation("resources").file(resourceFileName), generateRootRefContentForClasses(classNames));
		
		return builderChainer;
	}
	
	public BuilderChainer indexPageHasAliasReferences(String... aliasReferences) throws Exception 
	{
		fileUtil.write(aspect.file("index.html"), generateStringAliasReferencesContent(aliasReferences));	
		
		return builderChainer;
	}
	
	public BuilderChainer sourceResourceFileRefersTo(String resourceFileName, String... classNames) throws Exception 
	{
		fileUtil.write(aspect.assetLocation("src").file(resourceFileName), generateRootRefContentForClasses(classNames));
		
		return builderChainer;
	}
	
	public BuilderChainer indexPageHasContent(String content) throws Exception 
	{
		fileUtil.write(aspect.file("index.html"), content);	
		
		return builderChainer;
	}

	public BuilderChainer indexPageRequires(JsLib thirdpartyLib) throws Exception
	{
		return indexPageRequires(thirdpartyLib.getName());
	}
	
	public BuilderChainer indexPageRequires(String requirePath) throws Exception
	{
		if(requirePath.contains(".")) {
			throw new RuntimeException("The '" + requirePath + "' require path contains a dot. Did you mean to use indexPageRefersTo() instead?");
		}
		
		fileUtil.write(aspect.file("index.html"), "require('"+requirePath+"');");
		
		return builderChainer;
	}
	
	
	// Private
	private String generateStringClassReferencesContent(String... classNames) 
	{
		String content = "";
		
		for(String className : classNames)
		{
			if(className.contains("/")) {
				throw new RuntimeException("The '" + className + "' class name contains a slash. Did you mean to use indexPageRequires() instead?");
			}
			
			content += className + "\n";
		}
		return content;
	}
	
	private String generateStringAliasReferencesContent(String... aliasReferences) 
	{
		String content = "";
		
		for(String alias : aliasReferences)
		{
			content += "'" + alias + "'\n";
		}
		return content;
	}
	
	private String generateRootRefContentForClasses(String... classNames) 
	{
		String content = "";
		
		for(String className : classNames)
		{
			content += "<root refs='" + className + "'/>" + "\n";
		}
		return content;
	}
}
