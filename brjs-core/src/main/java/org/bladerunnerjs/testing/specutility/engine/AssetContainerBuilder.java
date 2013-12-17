package org.bladerunnerjs.testing.specutility.engine;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsBundlerContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.nodejs.NodeJsBundlerContentPlugin;
import org.bladerunnerjs.utility.JsStyleUtility;


public abstract class AssetContainerBuilder<N extends AssetContainer> extends NodeBuilder<N>
{
	
	private AssetContainer node;
	
	public AssetContainerBuilder(SpecTest specTest, N node)
	{
		super(specTest, node);
		this.node = node;
	}
	
	public BuilderChainer hasPackageStyle(String jsStyle) {
		return hasPackageStyle("", jsStyle);
	}
	
	public BuilderChainer hasPackageStyle(String packagePath, String jsStyle) {
		// TODO: delete this replaceAll() line, and make tests just use normal paths
		String path = packagePath.replaceAll("\\.", "/");
		JsStyleUtility.setJsStyle(node.file(path), jsStyle);
		
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
		
		if(!jsStyle.equals(NamespacedJsBundlerContentPlugin.JS_STYLE)) {
			throw new RuntimeException("classRefersTo() can only be used if packageOfStyle() has been set to '" + NamespacedJsBundlerContentPlugin.JS_STYLE + "'");
		}
		
		FileUtils.write(sourceFile, getClassBody(sourceClass) + "var obj = new " + referencedClass + "();\n");
		
		return builderChainer;
	}
	
	public BuilderChainer classDependsOn(String dependentClass, String referencedClass) throws Exception {
		File dependentSourceFile = getSourceFile(dependentClass);
		String jsStyle = JsStyleUtility.getJsStyle(dependentSourceFile.getParentFile());
		
		if(!jsStyle.equals(NamespacedJsBundlerContentPlugin.JS_STYLE)) {
			throw new RuntimeException("classDependsOn() can only be used if packageOfStyle() has been set to '" + NamespacedJsBundlerContentPlugin.JS_STYLE + "'");
		}
		
		FileUtils.write(dependentSourceFile, getCaplinJsClassBody(dependentClass, referencedClass));
		
		return builderChainer;
	}
	
	public BuilderChainer classRequires(String sourceClass, String dependencyClass) throws Exception {
		File sourceFile = getSourceFile(sourceClass);
		String jsStyle = JsStyleUtility.getJsStyle(sourceFile.getParentFile());
		
		if(!jsStyle.equals(NodeJsBundlerContentPlugin.JS_STYLE)) {
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
	
	public BuilderChainer classRefersToThirdpartyLib(String sourceClass, JsLib thirdpartyLib) throws Exception
	{
		File sourceFile = getSourceFile(sourceClass);
		String jsStyle = JsStyleUtility.getJsStyle(sourceFile.getParentFile());
		
		if(!jsStyle.equals(NamespacedJsBundlerContentPlugin.JS_STYLE)) {
			throw new RuntimeException("classRefersToThirdpartyLib() can only be used if packageOfStyle() has been set to '" + NamespacedJsBundlerContentPlugin.JS_STYLE + "'");
		}
		
		FileUtils.write(sourceFile, "br.Core.thirdparty('"+thirdpartyLib.getName()+"');", true);
		
		return builderChainer;
	}
	
	public BuilderChainer classRequiresThirdpartyLib(String sourceClass, JsLib thirdpartyLib) throws Exception
	{
		File sourceFile = getSourceFile(sourceClass);
		String jsStyle = JsStyleUtility.getJsStyle(sourceFile.getParentFile());
		
		if(!jsStyle.equals(NodeJsBundlerContentPlugin.JS_STYLE)) {
			throw new RuntimeException("classRequiresThirdpartyLib() can only be used if packageOfStyle() has not been used, or has been set to 'node.js' for dir '"+sourceFile.getParentFile().getPath()+"'");
		}
		
		FileUtils.write(sourceFile, "require('"+thirdpartyLib.getName()+"');", true);
		
		return builderChainer;
	}

	private File getSourceFile(String sourceClass) {
		return node.assetLocation("src").file(sourceClass.replaceAll("\\.", "/") + ".js");
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
		
		if(jsStyle.equals(NodeJsBundlerContentPlugin.JS_STYLE)) {
			classBody = className + " = function() {\n};\nmodule.exports = " + className + ";\n";
		}
		else if(jsStyle.equals(NamespacedJsBundlerContentPlugin.JS_STYLE)) {
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
		if (destClass.startsWith("./"))
		{
			destClassRequirePath = destClassRequirePath.replaceFirst("^//", "./");
		}
		
		return "var " + classRef + " = require('" + destClassRequirePath + "');\n" + getClassBody(sourceClass);
	}
	
	private String getCaplinJsClassBody(String sourceClass, String destClass) {
		return getClassBody(sourceClass) + "br.Core.extend(" + sourceClass + ", " + destClass + ");\n";
	}
	
}
