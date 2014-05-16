package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;

public class BRSdkTypedTestPack extends TypedTestPack
{
	private final NodeList<BRSdkTestPack> technologyTestPacks = new NodeList<>(this, BRSdkTestPack.class, "", null);
	
	public BRSdkTypedTestPack(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir, name);
	}
	
	public List<TestPack> testTechs()
	{
		return new ArrayList<TestPack>( technologyTestPacks.list() );
	}

	public TestPack testTech(String technologyName)
	{
		return technologyTestPacks.item(technologyName);
	}
}
