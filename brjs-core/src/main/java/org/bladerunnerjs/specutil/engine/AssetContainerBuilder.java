package org.bladerunnerjs.specutil.engine;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.NonBladerunnerJsLib;
import org.bladerunnerjs.model.utility.JsStyleUtility;


public abstract class AssetContainerBuilder<N extends AssetContainer> extends NodeBuilder<N>
{
	
	private AssetContainer node;
	
	public AssetContainerBuilder(SpecTest specTest, N node)
	{
		super(specTest, node);
		this.node = node;
	}
	
	public BuilderChainer hasPackageStyle(String packageDir, String jsStyle) {
		String path = packageDir.replaceAll("\\.", "/");
		JsStyleUtility.setJsStyle(node.src().file(path), jsStyle);
		
		return builderChainer;
	}
	
	public BuilderChainer hasClass(String className) throws Exception
	{
		FileUtils.write(getSourceFile(className), getClassBody(className));
		
		return builderChainer;
	}

	public BuilderChainer hasClasses(String... classNames) throws Exception
	{
		for(String className : classNames) {
			hasClass(className);
		}
		
		return builderChainer;
	}

	public BuilderChainer classRefersTo(String sourceClass, String referencedClass) throws Exception
	{
		File sourceFile = getSourceFile(sourceClass);
		String jsStyle = JsStyleUtility.getJsStyle(sourceFile.getParentFile());
		
		if(!jsStyle.equals("caplin-js")) {
			throw new RuntimeException("classRefersTo() can only be used if packageOfStyle() has been set to 'caplin-js'");
		}
		
		FileUtils.write(sourceFile, getCaplinJsClassBody(sourceClass, referencedClass));
		
		return builderChainer;
	}
	
	public BuilderChainer classRequires(String sourceClass, String dependencyClass) throws Exception {
		File sourceFile = getSourceFile(sourceClass);
		String jsStyle = JsStyleUtility.getJsStyle(sourceFile.getParentFile());
		
		if(!jsStyle.equals("node.js")) {
			throw new RuntimeException("classRequires() can only be used if packageOfStyle() has not been used, or has been set to 'node.js' for dir '"+sourceFile.getParentFile().getPath()+"'");
		}
		
		FileUtils.write(sourceFile, getNodeJsClassBody(sourceClass, dependencyClass));
		
		return builderChainer;
	}
	
	public BuilderChainer classFileHasContent(String sourceClass, String content) throws Exception
	{
		File sourceFile = getSourceFile(sourceClass);
		FileUtils.write(sourceFile, content);
		
		return builderChainer;
	}
	
	public BuilderChainer classRequiresThirdpartyLib(String sourceClass, NonBladerunnerJsLib thirdpartyLib) throws Exception
	{
		File sourceFile = getSourceFile(sourceClass);
		
		String jsStyle = JsStyleUtility.getJsStyle(sourceFile.getParentFile());
		
		if (jsStyle.equals("caplin-js")) {
			FileUtils.write(sourceFile, "br.require('"+thirdpartyLib.getName()+"');", true);
		}
		else {
			FileUtils.write(sourceFile, "require('"+thirdpartyLib.getName()+"');", true);
		}
		
		return builderChainer;
	}

	private File getSourceFile(String sourceClass) {
		return node.src().file(sourceClass.replaceAll("\\.", "/") + ".js");
	}
	
	public BuilderChainer hasBeenPopulated() throws Exception
	{
		node.populate();
		return builderChainer;
	}
	
	private String getClassBody(String className) {
		File sourceFile = getSourceFile(className);
		String jsStyle = JsStyleUtility.getJsStyle(sourceFile.getParentFile());
		String classBody;
		
		if(jsStyle.equals("node.js")) {
			classBody = className + " = function() {\n};\n";
		}
		else if(jsStyle.equals("caplin-js")) {
			classBody = className + " = function() {\n};\n";
		}
		else {
			throw new RuntimeException("'" + jsStyle + "' is an unsupported js style");
		}
		
		return classBody;
	}
	
	private String getNodeJsClassBody(String sourceClass, String destClass) {
		String classRef = sourceClass.substring(sourceClass.lastIndexOf('.') + 1);
		String destClassRequirePath = destClass.replaceAll("\\.", "/").replaceAll("\\.js$", "");
		
		return "var " + classRef + " = require('" + destClassRequirePath + "');\n" + getClassBody(sourceClass);
	}
	
	private String getCaplinJsClassBody(String sourceClass, String destClass) {
		return getClassBody(sourceClass) + "br.extend(" + sourceClass + ", " + destClass + ");\n";
	}
	
}
