package com.caplin.cutlass.bundler.xml.merge;

import org.junit.Test;

public class MergeSingleStreamTest extends MergeTestRunnerTest
{
	public MergeSingleStreamTest()
	{
		super("single-stream");
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
	public void multipleTemplateElems() throws Exception
	{
		runBundlerTest("multiple-template-elems");
	}
	
	@Test
	public void multipleMergeElems() throws Exception
	{
		runBundlerTest("multiple-merge-elems");
	}
	
	@Test
	public void alternateTemplateElems() throws Exception
	{
		runBundlerTest("alternate-template-elems");
	}
	
	@Test
	public void nestedTemplateElems() throws Exception
	{
		runBundlerTest("nested-template-elems");
	}
	
	@Test
	public void templateElemsContainingMergeElems() throws Exception
	{
		runBundlerTest("template-elems-containing-merge-elems");
	}
	
	@Test
	public void templateElemsContainingMergeAndTemplateElems() throws Exception
	{
		runBundlerTest("template-elems-containing-merge-and-template-elems");
	}
	
	@Test
	public void templateElemsContainingMergeElemsFollowedByTemplateElem() throws Exception
	{
		runBundlerTest("template-elems-containing-merge-elems-followed-by-template-elem");
	}
	
	@Test
	public void mergeElemWithNoId() throws Exception
	{
		runBundlerTest("merge-elem-with-no-id");
	}
	
	@Test
	public void mergeElemWithAlternateId() throws Exception
	{
		runBundlerTest("merge-elem-with-alternate-id");
	}
	
	@Test
	public void multipleMergeElemsWithNoId() throws Exception
	{
		runBundlerTest("multiple-merge-elems-with-no-id");
	}
	
	@Test
	public void mergeElemsWithDuplicateIds() throws Exception
	{
		runBundlerTest("merge-elems-with-duplicate-ids");
	}
	
	@Test
	public void mergeElemsWithAlternateDuplicateIds() throws Exception
	{
		runBundlerTest("merge-elems-with-alternate-duplicate-ids");
	}
	
	@Test
	public void textContentWithinMergeElem() throws Exception
	{
		runBundlerTest("text-content-within-merge-elem");
	}
	
	@Test
	public void textContentDeeplyNestedWithinMergeElem() throws Exception
	{
		runBundlerTest("text-content-deeply-nested-within-merge-elem");
	}
	
	@Test
	public void textContentStraddlingAnElem() throws Exception
	{
		runBundlerTest("text-content-straddling-an-elem");
	}
	
	@Test
	public void textContentDeeplyNestedStraddlingAnElem() throws Exception
	{
		runBundlerTest("text-content-deeply-nested-straddling-an-elem");
	}
	
	@Test
	public void elementContainingCdataTag() throws Exception
	{
		runBundlerTest("text-content-inside-cdata-tag");
	}
}
