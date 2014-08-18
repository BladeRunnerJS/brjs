package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class SdkJsLib extends AbstractJsLib
{
	
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
	
}
