package com.caplin.cutlass.bundler.xml.merge;

import org.junit.Test;

public class MergeGridTest extends MergeTestRunnerTest
{
	public MergeGridTest()
	{
		super("grid");
	}
	
	@Test
	public void dataProvidersOnly() throws Exception
	{
		runBundlerTest("data-providers-only");
	}
	
	@Test
	public void mappingsOnly() throws Exception
	{
		runBundlerTest("mappings-only");
	}
	
	@Test
	public void simpleGridCombining() throws Exception
	{
		runBundlerTest("simple-grid-combining");
	}
	
	@Test
	public void gridCombiningWithNonSharedFolders() throws Exception
	{
		runBundlerTest("grid-combining-with-non-shared-folders");
	}
	
	/* This functionality doesn't work yet
	@Test
	public void gridCombiningWithSharedFolders() throws Exception
	{
		runBundlerTest("grid-combining-with-shared-folders");
	}
	*/
}
