package org.bladerunnerjs.model;

import java.io.File;
import java.util.Map;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;


public class JsNonBladeRunnerLib extends JsLib
{
	
	public JsNonBladeRunnerLib(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, parent, dir);
		init(rootNode, parent, dir);
	}
	
	public static NodeMap<JsNonBladeRunnerLib> createSdkNonBladeRunnerLibNodeSet()
	{
		return new NodeMap<>(JsNonBladeRunnerLib.class, "sdk/libs/javascript/thirdparty", null);
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
	}
	
}
