package org.bladerunnerjs.api.spec.utility;

import java.io.File;

import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.spec.engine.BuilderChainer;
import org.bladerunnerjs.api.spec.engine.BundlableNodeBuilder;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.junit.Assert;

import com.google.common.base.Joiner;

public class AspectBuilder extends BundlableNodeBuilder<Aspect> {
	private final Aspect aspect;
	
	public AspectBuilder(SpecTest modelTest, Aspect aspect)
	{
		super(modelTest, aspect);
		this.aspect = aspect;
	}
	
	public BuilderChainer resourceFileRefersTo(String resourceFileName, String... classNames) throws Exception 
	{
		writeToFile(getResourceFile(resourceFileName), generateRootRefContentForClasses(classNames));
		
		return builderChainer;
	}
	
	public BuilderChainer indexPageHasAliasReferences(String... aliasReferences) throws Exception 
	{
		writeToFile(getIndexFile(), generateStringAliasReferencesContent(aliasReferences));	
		
		return builderChainer;
	}
	
	public BuilderChainer sourceResourceFileRefersTo(String resourceFileName, String... classNames) throws Exception 
	{
		writeToFile(getSrcResourceFile(resourceFileName), generateRootRefContentForClasses(classNames));
		
		return builderChainer;
	}
	
	public BuilderChainer indexPageHasContent(String content) throws Exception 
	{
		writeToFile(getIndexFile(), content);	
		
		return builderChainer;
	}

	public BuilderChainer indexPageRequires(JsLib thirdpartyLib) throws Exception
	{
		return indexPageRequires(thirdpartyLib.getName());
	}
	
	public BuilderChainer indexPageRequires(String... requirePaths) throws Exception
	{
		for(String requirePath : requirePaths) {
			if(requirePath.contains(".")) {
				throw new RuntimeException("The '" + requirePath + "' require path contains a dot. Did you mean to use indexPageRefersTo() instead?");
			}
		}
		
		writeToFile(getIndexFile(), "require('" + Joiner.on("');\nrequire('").join(requirePaths) + "');");
		
		return builderChainer;
	}
	
	public BuilderChainer doesNotContainFile(String filePath)
	{
		File file = aspect.file(filePath);
		
		Assert.assertFalse( "The file at "+file.getAbsolutePath()+" was not meant to exist", file.exists() );
		
		return builderChainer;
	}
	
	
	public MemoizedFile getResourceFile(String resourceFileName) {
		return aspect.assetLocation("resources").file(resourceFileName);
	}
	
	public MemoizedFile getSrcResourceFile(String resourceFileName) {
		return aspect.assetLocation("src").file(resourceFileName);
	}
	
	public MemoizedFile getIndexFile() {
		return aspect.file("index.html");
	}
	
	
	// Private
	
	
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
