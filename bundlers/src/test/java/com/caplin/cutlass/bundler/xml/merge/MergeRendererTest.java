package com.caplin.cutlass.bundler.xml.merge;

import org.junit.Test;

public class MergeRendererTest extends MergeTestRunnerTest
{
	public MergeRendererTest()
	{
		super("renderer");
	}
	
	@Test
	public void fxSpread() throws Exception
	{
		runBundlerTest("fx-spread");
	}
}
