package com.caplin.cutlass.bundler.xml.merge;

import org.junit.Test;

public class MergeDeckTest extends MergeTestRunnerTest
{
	public MergeDeckTest()
	{
		super("deck");
	}
	
	@Test
	public void deckDefinitionsOnly() throws Exception
	{
		runBundlerTest("deck-definitions-only");
	}
	

}
