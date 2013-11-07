package org.bladerunnerjs.specutil;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.specutil.engine.BuilderChainer;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class TestPackNodeBuilder extends NamedNodeBuilder
{
	TestPack testPack;

	public TestPackNodeBuilder(SpecTest modelTest, TestPack testPack)
	{
		super(modelTest, testPack);
		this.testPack = testPack;
	}

	public BuilderChainer testRefersTo(String className) throws IOException
	{
		FileUtils.write(testPack.testSource().file(className.replaceAll("\\.", "/") + ".js"), "");
		
		return builderChainer;
	}

}
