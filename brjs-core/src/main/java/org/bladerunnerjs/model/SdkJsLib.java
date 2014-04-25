package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;

public class SdkJsLib extends AbstractJsLib
{
	private final NodeMap<BRSdkTypedTestPack> testTypes;
	private final MemoizedValue<List<TypedTestPack>> testTypesList = new MemoizedValue<>("SdkJsLib.testTypes", root(), file("tests"));
	
	public SdkJsLib(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		testTypes = BRSdkTypedTestPack.createSdkTestPackNodeSet(rootNode);
	}
	
	public SdkJsLib(RootNode rootNode, Node parent, File dir)
	{
		this(rootNode, parent, dir, null);
	}
	
	public static NodeMap<SdkJsLib> createSdkNonBladeRunnerLibNodeSet(RootNode rootNode)
	{
		return new NodeMap<>(rootNode, SdkJsLib.class, "sdk/libs/javascript/thirdparty", null);
	}
	
	public static NodeMap<SdkJsLib> createSdkLibNodeSet(RootNode rootNode)
	{
		return new NodeMap<>(rootNode, SdkJsLib.class, "sdk/libs/javascript/br-libs", null);
	}
	
	@Override
	public App app()
	{
		return root().systemApp("SDK");			
	}
	
	@Override
	public List<TypedTestPack> testTypes()
	{
		return testTypesList.value(() -> {
			return new ArrayList<TypedTestPack>( children(testTypes) );
		});
	}
	
	@Override
	public TypedTestPack testType(String testTypeName)
	{
		return child(testTypes, testTypeName);
	}
}
