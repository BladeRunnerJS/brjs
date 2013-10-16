package com.caplin.cutlass.structure.model;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.utility.FileUtility;


public class NodeUtility
{

	public static List<Node> updateNodesFromDirListing(Node parentNode, Map<String,Node> nodes, NodeType newNodeType, NodeNameCalculator nameCalculator, File dirToList, String acceptSuffix)
	{
		if (!dirToList.exists())
		{
			return new ArrayList<Node>();
		}
		List<File> nodeFiles = new LinkedList<File>();
		for (File child : FileUtility.sortFileArray(dirToList.listFiles()))
		{
			if (child.isDirectory() && child.getName().endsWith(acceptSuffix)){
				nodeFiles.add(child);
			}
		}
		NodeUtility.updateNodes(parentNode, nodes, newNodeType, nameCalculator, nodeFiles);
		return getNodesOfType(nodes, newNodeType);
	}
	
	public static List<Node> updateNodes(Node parentNode, Map<String,Node> nodes, NodeType newNodeType, NodeNameCalculator nameCalculator, List<File> nodeFiles)
	{
		for (File nodeFile : nodeFiles)
		{
			NodeUtility.updateNodes(parentNode, nodes, newNodeType, nameCalculator, nodeFile);
		}
		return getNodesOfType(nodes, newNodeType);
	}
	
	/* must be concurrent so multiple threads do not both try to add nodes to this nodes known child nodes */
	public synchronized static Node updateNodes(Node parentNode, Map<String,Node> nodes, NodeType newNodeType, NodeNameCalculator nameCalculator, File nodeFile)
	{
		// remove dead nodes first so we can replace them
		// store nodes to remove in a list and remove later so we dont get a concurrent modification exception
		ArrayList<String> nodesToRemove = new ArrayList<String>();
		for (Node node : nodes.values())
		{
			if (!node.getDir().exists())
			{
				String nodeName = nameCalculator.calculateNodeName(node.getDir());
				nodesToRemove.add(nodeName);
				SdkModel.unregisterNode(node);
			}
		}
		for (String nodeToRemove : nodesToRemove)
		{
			nodes.remove(nodeToRemove);
		}
		
		String nodeName = nameCalculator.calculateNodeName(nodeFile);
		if (!nodes.containsKey(nodeName))
		{
			Node newNode = NodeFactory.createNodeOfType(newNodeType, parentNode, nodeFile);
			nodes.put(nodeName, newNode);
			return newNode;
		}
		else
		{
			return nodes.get(nodeName);
		}
	}
	
	public static List<Node> getNodesOfType(Map<String,Node> nodes, NodeType nodeType)
	{
		List<Node> nodesOfType = new LinkedList<Node>();
		for (Node node : nodes.values())
		{
			if (node.getNodeType() == nodeType)
			{
				nodesOfType.add( node);
			}
		}
		return nodesOfType;
	}
	
}
