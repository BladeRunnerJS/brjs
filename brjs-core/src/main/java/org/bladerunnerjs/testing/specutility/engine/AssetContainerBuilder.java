package org.bladerunnerjs.testing.specutility.engine;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.plugin.plugins.bundlers.commonjs.CommonJsContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsContentPlugin;
import org.bladerunnerjs.utility.EncodedFileUtil;
import org.bladerunnerjs.utility.JsStyleUtility;


public abstract class AssetContainerBuilder<N extends AssetContainer> extends NodeBuilder<N>
{
	private AssetContainer node;
	protected EncodedFileUtil fileUtil;
	
	public AssetContainerBuilder(SpecTest specTest, N node)
	{
		super(specTest, node);
		
		this.node = node;
		fileUtil = new EncodedFileUtil(specTest.getActiveCharacterEncoding());
	}
	
	public BuilderChainer containsResourceFile(String resourceFilePath) throws Exception {
		fileUtil.write(node.assetLocation("resources").file(resourceFilePath), resourceFilePath + "\n");
		
		return builderChainer;
	}
	
	public BuilderChainer containsResourceFiles(String... resourceFilePaths) throws Exception {
		for(String resourceFilePath : resourceFilePaths) {
			containsResourceFile(resourceFilePath);
		}
		
		return builderChainer;
	}
	
	public BuilderChainer containsResourceFileWithContents(String resourceFileName, String contents) throws Exception 
	{
		fileUtil.write(node.assetLocation("resources").file(resourceFileName), contents);
		
		return builderChainer;
	}
	
	public BuilderChainer containsFileCopiedFrom(String resourceFileName, String srcFile) throws Exception 
	{
		FileUtils.copyFile( new File(srcFile), node.file(resourceFileName) );
		
		return builderChainer;
	}
	
	public BuilderChainer hasClass(String className) throws Exception
	{
		fileUtil.write(getSourceFile(className), getClassBody(className));
		return builderChainer;
	}
	
	public BuilderChainer hasClasses(String... classNames) throws Exception
	{
		for(String className : classNames) {
			hasClass(className);
		}
		return builderChainer;
	}
	
	public BuilderChainer hasTestClass(String className) throws Exception
	{
		fileUtil.write(getTestSourceFile(className), getClassBody(className));
		return builderChainer;
	}
	
	public BuilderChainer hasTestClasses(String... classNames) throws Exception
	{
		for(String className : classNames) {
			hasTestClass(className);
		}
		return builderChainer;
	}

	
	
	public BuilderChainer classDependsOn(String sourceClass, String... referencedClasses) throws Exception
	{
		File sourceFile = getSourceFile(sourceClass);
		return classDependsOn(sourceClass, sourceFile, referencedClasses);
	}
	
	public BuilderChainer testClassDependsOn(String sourceClass, String... referencedClasses) throws Exception
	{
		File sourceFile = getTestSourceFile(sourceClass);
		return classDependsOn(sourceClass, sourceFile, referencedClasses);
	}
	
	public BuilderChainer classStaticallyDependsOn(String dependentClass, String referencedClass) throws Exception {
		return classExtends(dependentClass, referencedClass);
	}
	
	public BuilderChainer classExtends(String dependentClass, String referencedClass) throws Exception {
		File dependentSourceFile = getSourceFile(dependentClass);
		
		String classBody = getClassBody(dependentClass);
		String extendString = "br.Core.extend(" + dependentClass + ", " + referencedClass + ");\n";
		
		fileUtil.write(dependentSourceFile, classBody + extendString);
		
		return builderChainer;
	}
	
	public BuilderChainer classRequires(String sourceClass, String dependencyClass) throws Exception {
		File sourceFile = getSourceFile(sourceClass);
		return classRequires(sourceClass, dependencyClass, sourceFile);
	}
	
	public BuilderChainer testClassRequires(String sourceClass, String dependencyClass) throws Exception {
		File sourceFile = getTestSourceFile(sourceClass);
		return classRequires(sourceClass, dependencyClass, sourceFile);
	}
	
	public BuilderChainer classDependsOnAlias(String sourceClass, String alias) throws Exception
	{
		File sourceFile = getSourceFile(sourceClass);
		return classDependsOn(sourceClass, sourceFile, "'" + alias + "'");
	}
	
	public BuilderChainer classFileHasContent(String sourceClass, String content) throws Exception
	{
		File sourceFile = getSourceFile(sourceClass);
		fileUtil.write(sourceFile, content);
		
		return builderChainer;
	}
	
	public BuilderChainer classDependsOnThirdpartyLib(String sourceClass, JsLib thirdpartyLib) throws Exception
	{
		File sourceFile = getSourceFile(sourceClass);
		String jsStyle = JsStyleUtility.getJsStyle(sourceFile.getParentFile());
		
		if(!jsStyle.equals(NamespacedJsContentPlugin.JS_STYLE)) {
			throw new RuntimeException("classDependsOnThirdpartyLib() can only be used if packageOfStyle() has been set to '" + NamespacedJsContentPlugin.JS_STYLE + "'");
		}
		
		fileUtil.write( sourceFile, "br.Core.thirdparty('"+thirdpartyLib.getName()+"');" + getClassBody(sourceClass) );
		
		return builderChainer;
	}
	
	public BuilderChainer classRequiresThirdpartyLib(String sourceClass, JsLib thirdpartyLib) throws Exception
	{
		File sourceFile = getSourceFile(sourceClass);
		String jsStyle = JsStyleUtility.getJsStyle(sourceFile.getParentFile());
		
		if(!jsStyle.equals(CommonJsContentPlugin.JS_STYLE)) {
			throw new RuntimeException("classRequiresThirdpartyLib() can only be used if packageOfStyle() has not been used, or has been set to 'node.js' for dir '"+sourceFile.getParentFile().getPath()+"'");
		}
		
		fileUtil.write(sourceFile, "require('"+thirdpartyLib.getName()+"');", true);
		
		return builderChainer;
	}
	
	public BuilderChainer hasBeenPopulated() throws Exception
	{
		node.populate();
		return builderChainer;
	}
	
	
	
	
	
	
	protected File getSourceFile(String sourceClass) {
		return node.assetLocation("src").file(sourceClass.replaceAll("\\.", "/") + ".js");
	}
	
	protected File getTestSourceFile(String sourceClass)
	{
		return node.assetLocation("src-test").file(sourceClass.replaceAll("\\.", "/") + ".js");		
	}
	
	
	private BuilderChainer classDependsOn(String sourceClass, File sourceFile, String... referencedClasses) throws Exception
	{
		String jsStyle = JsStyleUtility.getJsStyle(sourceFile.getParentFile());
		
		if(!jsStyle.equals(NamespacedJsContentPlugin.JS_STYLE)) {
			throw new RuntimeException("classRefersTo() can only be used if packageOfStyle() has been set to '" + NamespacedJsContentPlugin.JS_STYLE + "' for dir '"+sourceFile.getParentFile().getPath()+"'.");
		}
		
		String classReferencesContent = "var someFunction = function() {\n";
		for(String referencedClass : referencedClasses)
		{
			classReferencesContent += "\tnew " + referencedClass + "();\n";
		}
		classReferencesContent += "};\n";
		
		if (referencedClasses.length > 0)
		{
			fileUtil.write(sourceFile, getClassBody(sourceClass) + classReferencesContent);
		}
		
		return builderChainer;
	}
	
	private BuilderChainer classRequires(String sourceClass, String dependencyClass, File sourceFile) throws Exception
	{
		String jsStyle = JsStyleUtility.getJsStyle(sourceFile.getParentFile());
		
		if(!jsStyle.equals(CommonJsContentPlugin.JS_STYLE)) {
			throw new RuntimeException("classRequires() can only be used if packageOfStyle() has not been used, or has been set to '"+CommonJsContentPlugin.JS_STYLE+"' for dir '"+sourceFile.getParentFile().getPath()+"'");
		}
		
		dependencyClass = dependencyClass.replaceAll("\\.(\\w)", "/$1");
		String classRef = StringUtils.substringAfterLast(dependencyClass, "/");
		String requireString = "var " + classRef + " = require('" + dependencyClass + "');\n";
		fileUtil.write(sourceFile, requireString + getClassBody(sourceClass));
		
		return builderChainer;
	}
	
	private String getClassBody(String className) {
		File sourceFile = getSourceFile(className);
		String jsStyle = JsStyleUtility.getJsStyle(sourceFile.getParentFile());
		String classBody;
		
		if(jsStyle.equals(CommonJsContentPlugin.JS_STYLE)) {
			if (className.contains("."))
			{
				throw new RuntimeException("Require paths must not contain the '.' character");
			}
			className = className.replaceAll("\\.", "/");
			String commonJsClassName = StringUtils.substringAfterLast(className, "/");
			classBody = commonJsClassName + " = function() {\n"+
				"};\n" +
				"\n" +
				"module.exports = " + commonJsClassName + ";\n";
		}
		else if(jsStyle.equals(NamespacedJsContentPlugin.JS_STYLE)) {
			if (className.contains("/"))
			{
				throw new RuntimeException("Class names must not contain the '/' character");
			}
			classBody = className + " = function() {\n};\n";
		}
		else {
			throw new RuntimeException("'" + jsStyle + "' is an unsupported js style");
		}
		
		return classBody;
	}
	
}
