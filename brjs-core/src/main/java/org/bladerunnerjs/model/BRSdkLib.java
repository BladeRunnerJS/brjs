package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;

public class BRSdkLib extends StandardJsLib
{
	private static final String DUMMY_SDK_APP_NAME = "dummy-sdk-app"; // we need a dummy SDK app since SDK libs dont live inside an app
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
	public App app()
	{
		return root().systemApp(DUMMY_SDK_APP_NAME);
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
