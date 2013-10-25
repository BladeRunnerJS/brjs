package com.caplin.cutlass.bundler.xml.merge;

import org.junit.Test;

public class MergeTestWorkbenchInclusionExclusion extends MergeTestRunnerTest {

	public MergeTestWorkbenchInclusionExclusion()
	{
		super("folders-with-workbench");
	}
	
	@Test
	public void excludingWorkbench() throws Exception {
		runBundlerTest("excludingWorkbench", new String[]{"workbench"});
	}
	
	@Test
	public void includingWorkbench() throws Exception {
		runBundlerTest("includingWorkbench");
	}
	
}
