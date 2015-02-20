package org.bladerunnerjs.api.spec.engine;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.utility.FileUtils;


public abstract class AssetContainerBuilder<N extends AssetContainer> extends NodeBuilder<N>
{
	private AssetContainer node;
	
	public AssetContainerBuilder(SpecTest specTest, N node)
	{
		super(specTest, node);
		
		this.node = node;
	}
	
	public BuilderChainer containsResourceFile(String resourceFilePath) throws Exception {
		writeToFile(node.assetLocation("resources").file(resourceFilePath), resourceFilePath + "\n");
		
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
		writeToFile(node.assetLocation("resources").file(resourceFileName), contents);
		
		return builderChainer;
	}
	
	public BuilderChainer containsFileCopiedFrom(String resourceFileName, String srcFile) throws Exception 
	{
		FileUtils.copyFile( specTest.brjs, new File(srcFile), node.file(resourceFileName) );
		
		return builderChainer;
	}
	
	public BuilderChainer hasClass(String className) throws Exception
	{
		writeToFile(getSourceFile(className), getClassBody(className));
		
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
		writeToFile(getTestSourceFile(className), getClassBody(className));
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
		MemoizedFile sourceFile = getSourceFile(sourceClass);
		return classDependsOn(sourceClass, sourceFile, referencedClasses);
	}
	
	public BuilderChainer testClassDependsOn(String sourceClass, String... referencedClasses) throws Exception
	{
		MemoizedFile sourceFile = getTestSourceFile(sourceClass);
		return classDependsOn(sourceClass, sourceFile, referencedClasses);
	}
	
	public BuilderChainer classStaticallyDependsOn(String dependentClass, String referencedClass) throws Exception {
		return classExtends(dependentClass, referencedClass);
	}
	
	public BuilderChainer classExtends(String dependentClass, String referencedClass) throws Exception {
		File dependentSourceFile = getSourceFile(dependentClass);
		
		String classBody = getClassBody(dependentClass);
		String extendString = "br.Core.extend(" + dependentClass + ", " + referencedClass + ");\n";
		
		writeToFile(dependentSourceFile, classBody + extendString);
		
		return builderChainer;
	}
	
	public BuilderChainer classRequires(String sourceClass, String dependencyClass) throws Exception {
		MemoizedFile sourceFile = getSourceFile(sourceClass);
		return classRequires(sourceClass, dependencyClass, sourceFile, false);
	}
	
	public BuilderChainer classRequiresAtUseTime(String sourceClass, String dependencyClass) throws Exception {
		MemoizedFile sourceFile = getSourceFile(sourceClass);
		return classRequires(sourceClass, dependencyClass, sourceFile, true);
	}
	
	public BuilderChainer testClassRequires(String sourceClass, String dependencyClass) throws Exception {
		MemoizedFile sourceFile = getTestSourceFile(sourceClass);
		return classRequires(sourceClass, dependencyClass, sourceFile, false);
	}
	
	public BuilderChainer classDependsOnAlias(String sourceClass, String alias) throws Exception
	{
		MemoizedFile sourceFile = getSourceFile(sourceClass);
		return classDependsOn(sourceClass, sourceFile, "'" + alias + "'");
	}
	
	public BuilderChainer classFileHasContent(String sourceClass, String content) throws Exception
	{
		File sourceFile = getSourceFile(sourceClass);
		writeToFile(sourceFile, content);
		
		return builderChainer;
	}
	
	public BuilderChainer classDependsOnThirdpartyLib(String sourceClass, JsLib thirdpartyLib) throws Exception
	{
		File sourceFile = getSourceFile(sourceClass);
		String jsStyle = specTest.brjs.jsStyleAccessor().getJsStyle(sourceFile.getParentFile());
		
		if(!jsStyle.equals(SpecTest.NAMESPACED_JS_STYLE)) {
			throw new RuntimeException("classDependsOnThirdpartyLib() can only be used if packageOfStyle() has been set to '" + SpecTest.NAMESPACED_JS_STYLE + "'");
		}
		
		writeToFile( sourceFile, "br.Core.thirdparty('"+thirdpartyLib.getName()+"');" + getClassBody(sourceClass) );
		
		return builderChainer;
	}
	
	public BuilderChainer classRequiresThirdpartyLib(String sourceClass, JsLib thirdpartyLib) throws Exception
	{
		File sourceFile = getSourceFile(sourceClass);
		String jsStyle = specTest.brjs.jsStyleAccessor().getJsStyle(sourceFile.getParentFile());
		
		if(!jsStyle.equals(SpecTest.COMMON_JS_STYLE)) {
			throw new RuntimeException("classRequiresThirdpartyLib() can only be used if packageOfStyle() has not been used, or has been set to 'node.js' for dir '"+sourceFile.getParentFile().getPath()+"'");
		}
		
		writeToFile(sourceFile, "require('"+thirdpartyLib.getName()+"');", true);
		
		return builderChainer;
	}
	
	public BuilderChainer hasBeenPopulated() throws Exception
	{
		node.populate("default");
		return builderChainer;
	}
	
	public MemoizedFile getSourceFile(String sourceClass) {
		AssetLocation assetLocation = node.assetLocation("src");
		if (assetLocation == null) {
			throw new RuntimeException("Cannot find asset location for the 'src' dir. Either it doesn't exist or there are no asset plugins to discover it.");
		}
		return assetLocation.file(sourceClass.replaceAll("\\.", "/") + ".js");
	}
	
	
	protected MemoizedFile getTestSourceFile(String sourceClass)
	{
		return node.assetLocation("src-test").file(sourceClass.replaceAll("\\.", "/") + ".js");		
	}
	
	
	private BuilderChainer classDependsOn(String sourceClass, MemoizedFile sourceFile, String... referencedClasses) throws Exception
	{
		String jsStyle = specTest.brjs.jsStyleAccessor().getJsStyle(sourceFile.getParentFile());
		
		if(!jsStyle.equals(SpecTest.NAMESPACED_JS_STYLE)) {
			throw new RuntimeException("classDependsOn() can only be used if packageOfStyle() has been set to '" + SpecTest.NAMESPACED_JS_STYLE + "' for dir '"+sourceFile.getParentFile().getPath()+"'.");
		}
		
		String classReferencesContent = "var someFunction = function() {\n";
		for(String referencedClass : referencedClasses)
		{
			if (referencedClass.contains("/")) {
				throw new RuntimeException("Class names should not contain '/'s.");
			}
			classReferencesContent += "\tnew " + referencedClass + "();\n";
		}
		classReferencesContent += "};\n";
		
		if (referencedClasses.length > 0)
		{
			writeToFile(sourceFile, getClassBody(sourceClass) + classReferencesContent);
		}
		
		return builderChainer;
	}
	
	private BuilderChainer classRequires(String sourceClass, String dependencyClass, MemoizedFile sourceFile, boolean atUseTime) throws Exception
	{
		String jsStyle = specTest.brjs.jsStyleAccessor().getJsStyle(sourceFile.getParentFile());
		
		if(!jsStyle.equals(SpecTest.COMMON_JS_STYLE)) {
			throw new RuntimeException("classRequires() can only be used if packageOfStyle() has not been used, or has been set to '"+SpecTest.COMMON_JS_STYLE+"' for dir '"+sourceFile.getParentFile().getPath()+"'");
		}
		
		if (dependencyClass.matches(".*?\\.(?![/\\.]).*")) { // matches '.' unless it is immediately followed by another . or a /
			if(!dependencyClass.contains("!")) {
				throw new RuntimeException("Requre paths should not contain '.'s.");
			}
		}
		
		String classRef = (dependencyClass.contains("/")) ? StringUtils.substringAfterLast(dependencyClass, "/") : dependencyClass;
		String requireString = "var " + classRef + " = require('" + dependencyClass + "');\n";
		
		if(atUseTime) {
			writeToFile(sourceFile, "\nfunction f() {\n" + requireString + getClassBody(sourceClass) + "\n};");
		}
		else {
			writeToFile(sourceFile, requireString + getClassBody(sourceClass));
		}
		
		
		return builderChainer;
	}
	
	private String getClassBody(String className) {
		File sourceFile = getSourceFile(className);
		String jsStyle = specTest.brjs.jsStyleAccessor().getJsStyle(sourceFile.getParentFile());
		String classBody;
		
		if(jsStyle.equals(SpecTest.COMMON_JS_STYLE)) {
			if (className.contains("."))
			{
				throw new RuntimeException("Require paths must not contain the '.' character");
			}
			className = className.replaceAll("\\.", "/");
			String commonJsClassName = (className.contains("/")) ? StringUtils.substringAfterLast(className, "/") : className;
			classBody = commonJsClassName + " = function() {\n"+
				"};\n" +
				"\n" +
				"module.exports = " + commonJsClassName + ";\n";
		}
		else if(jsStyle.equals(SpecTest.NAMESPACED_JS_STYLE)) {
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
