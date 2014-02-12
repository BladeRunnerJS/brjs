package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;

public class BRSdkLib extends BRLib
{
	private final NodeMap<BRSdkTypedTestPack> testTypes;
	
	public BRSdkLib(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir, name);
		testTypes = BRSdkTypedTestPack.createSdkTestPackNodeSet(rootNode);
	}
	
	public static NodeMap<BRSdkLib> createSdkLibNodeSet(RootNode rootNode)
	{
		return new NodeMap<>(rootNode, BRSdkLib.class, "sdk/libs/javascript/br-libs", null);
	}

	@Override
	public App getApp()
	{
		return root().systemApp("dummy-sdk-app");
	}
	
	@Override
	public List<TypedTestPack> testTypes()
	{
		return new ArrayList<TypedTestPack>( children(testTypes) );
	}
	
	@Override
	public TypedTestPack testType(String testTypeName)
	{
		return child(testTypes, testTypeName);
	}
	
}
