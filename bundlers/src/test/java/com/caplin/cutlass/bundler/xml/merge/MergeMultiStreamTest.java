package com.caplin.cutlass.bundler.xml.merge;

import org.junit.Test;

public class MergeMultiStreamTest extends MergeTestRunnerTest
{
	public MergeMultiStreamTest()
	{
		super("multi-stream");
	}
	
	@Test
	public void singleTemplateElem() throws Exception
	{
		runBundlerTest("single-template-elem");
	}
	
	@Test
	public void singleMergeElem() throws Exception
	{
		runBundlerTest("single-merge-elem");
	}
	
	@Test
	public void multipleMergeElems() throws Exception
	{
		runBundlerTest("multiple-merge-elems");
	}
	
	@Test
	public void multipleTemplateElems() throws Exception
	{
		runBundlerTest("multiple-template-elems");
	}
	
	@Test
	public void nestedTemplateElems() throws Exception
	{
		runBundlerTest("nested-template-elems");
	}
	
	// TODO: implement the remainder of the multi-stream tests...
}
