package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;

public class AppJsLib extends AbstractJsLib
{
	private final NodeList<TypedTestPack> testTypes;
	private final MemoizedValue<List<TypedTestPack>> testTypesList = new MemoizedValue<>("AppJsLib.testTypes", root(), file("tests"));
	
	public AppJsLib(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir, name);
		testTypes = TypedTestPack.createNodeSet(this);
	}
	
	public AppJsLib(RootNode rootNode, Node parent, File dir)
	{
		this(rootNode, parent, dir, null);
	}
	
	public static NodeList<AppJsLib> createAppNonBladeRunnerLibNodeSet(Node node)
	{
		return new NodeList<>(node, AppJsLib.class, "thirdparty-libraries", null);
	}
	
	public static NodeList<AppJsLib> createAppNodeSet(Node node)
	{
		return new NodeList<>(node, AppJsLib.class, "libs", null);
	}
	
	@Override
	public List<TypedTestPack> testTypes()
	{
		return testTypesList.value(() -> {
			return testTypes.list();
		});
	}
	
	@Override
	public TypedTestPack testType(String testTypeName)
	{
		return testTypes.item(testTypeName);
	}
}
