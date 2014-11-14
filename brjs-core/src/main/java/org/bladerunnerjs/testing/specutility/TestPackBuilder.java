package org.bladerunnerjs.testing.specutility;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.plugin.plugins.bundlers.commonjs.CommonJsSourceModule;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsSourceModule;
import org.bladerunnerjs.testing.specutility.engine.AssetContainerBuilder;
import org.bladerunnerjs.testing.specutility.engine.BuilderChainer;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.utility.JsStyleUtility;


public class TestPackBuilder extends AssetContainerBuilder<TestPack>
{
	TestPack testPack;

	public TestPackBuilder(SpecTest modelTest, TestPack testPack)
	{
		super(modelTest, testPack);
		this.testPack = testPack;
	}

	public BuilderChainer testRefersTo(String testFilePath, String... classNames) throws IOException
	{
		File testFile = testPack.tests().file(testFilePath);
		String jsStyle = JsStyleUtility.getJsStyle(specTest.brjs, testFile.getParentFile());
		
		if(!jsStyle.equals(NamespacedJsSourceModule.JS_STYLE)) {
			throw new RuntimeException("testRefersTo() can only be used if packageOfStyle() has been set to '" + NamespacedJsSourceModule.JS_STYLE + "'");
		}
		
		String content = "";
		for(String className : classNames)
		{
			content += className + "\n";
		}
		
		writeToFile(testFile, content);
		
		return builderChainer;
	}
	
	public BuilderChainer testFileHasContent(String testFilePath, String content) throws IOException
	{
		File testFile = testPack.tests().file(testFilePath);
		writeToFile(testFile, content);
		return builderChainer;
	}
	
	public BuilderChainer testRequires(String testFilePath, String className) throws IOException
	{
		File testFile = testPack.tests().file(testFilePath);
		String jsStyle = JsStyleUtility.getJsStyle(specTest.brjs, testFile.getParentFile());
		
		if(!jsStyle.equals(CommonJsSourceModule.JS_STYLE)) {
			throw new RuntimeException("testRequires() can only be used if packageOfStyle() has been set to '" + CommonJsSourceModule.JS_STYLE + "'");
		}
		
		writeToFile(testFile, "require('"+className+"');");
		
		return builderChainer;
	}
	
	@Override
	public MemoizedFile getSourceFile(String sourceClass) {
		return testPack.testSource().file(sourceClass.replaceAll("\\.", "/") + ".js");
	}

}
