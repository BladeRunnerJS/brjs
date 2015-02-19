package org.bladerunnerjs.api.spec.utility;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.spec.engine.AssetContainerBuilder;
import org.bladerunnerjs.api.spec.engine.BuilderChainer;
import org.bladerunnerjs.api.spec.engine.SpecTest;


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
		String jsStyle = specTest.brjs.jsStyleAccessor().getJsStyle(testFile.getParentFile());
		
		if(!jsStyle.equals(SpecTest.NAMESPACED_JS_STYLE)) {
			throw new RuntimeException("testRefersTo() can only be used if packageOfStyle() has been set to '" + SpecTest.NAMESPACED_JS_STYLE + "'");
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
		String jsStyle = specTest.brjs.jsStyleAccessor().getJsStyle(testFile.getParentFile());
		
		if(!jsStyle.equals(SpecTest.COMMON_JS_STYLE)) {
			throw new RuntimeException("testRequires() can only be used if packageOfStyle() has been set to '" + SpecTest.COMMON_JS_STYLE + "'");
		}
		
		writeToFile(testFile, "require('"+className+"');");
		
		return builderChainer;
	}
	
	@Override
	public MemoizedFile getSourceFile(String sourceClass) {
		return testPack.testSource().file(sourceClass.replaceAll("\\.", "/") + ".js");
	}

}
