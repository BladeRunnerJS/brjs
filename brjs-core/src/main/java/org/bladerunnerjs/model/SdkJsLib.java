package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;

public class SdkJsLib extends AbstractJsLib
{
	private final NodeList<BRSdkTypedTestPack> testTypes = new NodeList<>(this, BRSdkTypedTestPack.class, "tests", "^test-");
	
	public SdkJsLib(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
	}
	
	public SdkJsLib(RootNode rootNode, Node parent, File dir)
	{
		this(rootNode, parent, dir, null);
	}
	
	@Override
	public App app()
	{
		return root().systemApp("SDK");			
	}
	
	@Override
	public List<TypedTestPack> testTypes()
	{
		return new ArrayList<TypedTestPack>( testTypes.list() );
	}
	
	@Override
	public TypedTestPack testType(String testTypeName)
	{
		return testTypes.item(testTypeName);
	}
}
