package com.caplin.cutlass.structure.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.caplin.cutlass.CutlassConfig;


public class SdkModelTest
{

	private static final String testBase = "src/test/resources/ExampleAppStructure";
	
	@Test
	public void testRootNodeIsCreatedWhenRequested() throws IOException
	{
		File nodeDir = new File(testBase);
		assertEquals( NodeType.ROOT, SdkModel.getNode(nodeDir).getNodeType() );
		assertEquals( nodeDir, SdkModel.getNode(nodeDir).getDir() );
	}

	@Test
	public void testSdkNodeIsCreatedWhenRequested() throws IOException
	{
		File nodeDir = new File(testBase, CutlassConfig.SDK_DIR);
		assertEquals( NodeType.SDK, SdkModel.getNode(nodeDir).getNodeType() );
		assertEquals( nodeDir, SdkModel.getNode(nodeDir).getDir() );
	}
	
	@Test
	public void testNearestNodeIsReturnedForNonNodePath() throws IOException
	{
		File nodeDir = new File(testBase, "/apps/app1/a-bladeset/blades/blade1/workbench/workbench.css");
		assertEquals( NodeType.WORKBENCH, SdkModel.getNode(nodeDir).getNodeType() );
		assertEquals( nodeDir.getParentFile(), SdkModel.getNode(nodeDir).getDir() );
	}
	
	
	

}
