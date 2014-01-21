package org.bladerunnerjs.testing.specutility;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsContentPlugin;
import org.bladerunnerjs.plugin.plugins.bundlers.nodejs.NodeJsContentPlugin;
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

	public BuilderChainer testRefersTo(String testFilePath, String className) throws IOException
	{
		File testFile = testPack.tests().file(testFilePath);
		String jsStyle = JsStyleUtility.getJsStyle(testFile.getParentFile());
		
		if(!jsStyle.equals(NamespacedJsContentPlugin.JS_STYLE)) {
			throw new RuntimeException("testRefersTo() can only be used if packageOfStyle() has been set to '" + NamespacedJsContentPlugin.JS_STYLE + "'");
		}
		
		FileUtils.write(testFile, className);
		
		return builderChainer;
	}
	
	public BuilderChainer testRequires(String testFilePath, String className) throws IOException
	{
		File testFile = testPack.tests().file(testFilePath);
		String jsStyle = JsStyleUtility.getJsStyle(testFile.getParentFile());
		
		if(!jsStyle.equals(NodeJsContentPlugin.JS_STYLE)) {
			throw new RuntimeException("testRequires() can only be used if packageOfStyle() has been set to '" + NodeJsContentPlugin.JS_STYLE + "'");
		}
		
		FileUtils.write(testFile, "require('"+className+"');");
		
		return builderChainer;
	}
	
	@Override
	protected File getSourceFile(String sourceClass) {
		return testPack.testSource().file(sourceClass.replaceAll("\\.", "/") + ".js");
	}

}
