package com.caplin.cutlass.structure.model;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;


public abstract class Node
{
	private final Node parentNode;
	private final File dir;
	protected final Map<String, Node> nodes = new LinkedHashMap<String, Node>();
	
	public Node(Node parentNode, File path) 
	{
		this.parentNode = parentNode;
		this.dir = path;
	}
	
	public abstract NodeType getNodeType();
	public abstract void calculateChildNodes();
	
	public File getDir()
	{
		return dir;
	}
	
	public Node getParentNode()
	{
		return parentNode;
	}
	
	public Node getAncestorOfType(NodeType type)
	{
		Node nextNode = this;
		
		do
		{
			if (nextNode.getNodeType() == type)
			{
				return nextNode;
			}
			nextNode = nextNode.getParentNode();
		} while (nextNode != null);
		
		return null;
	}
}
