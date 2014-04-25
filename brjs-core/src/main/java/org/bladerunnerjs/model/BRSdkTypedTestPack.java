package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;

public class BRSdkTypedTestPack extends TypedTestPack
{
	private final NodeMap<BRSdkTestPack> technologyTestPacks;
	
	public BRSdkTypedTestPack(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir, name);
		technologyTestPacks = BRSdkTestPack.createBrSdkTestPackNodeSet(this);
	}
	
	public static NodeMap<BRSdkTypedTestPack> createSdkTestPackNodeSet(Node node)
	{
		return new NodeMap<>(node, BRSdkTypedTestPack.class, "tests", "^test-");
	}
	
	public List<TestPack> testTechs()
	{
		return new ArrayList<TestPack>( children(technologyTestPacks) );
	}

	public TestPack testTech(String technologyName)
	{
		return child(technologyTestPacks, technologyName);
	}
}
