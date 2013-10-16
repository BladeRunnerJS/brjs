package com.caplin.cutlass.structure.model;

import java.io.File;
import java.lang.reflect.Constructor;


public class NodeFactory
{

	public static Node createNodeOfType(NodeType type, Node parent, File path)
	{
		try {
			Constructor<? extends Node> construct = type.nodeClass.getConstructor(new Class<?>[]{Node.class,File.class});		
			Node newNode = construct.newInstance(new Object[]{parent, path});
			SdkModel.registerNode(newNode);
			return newNode;
		}
		catch (Exception ex)
		{
			return null;
		}
	}
	
}
