package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class NodeList<N extends Node> {
	private final Node node;
	private final NodeMap<N> nodeMap;
	
	public NodeList(Node node, Class<N> nodeClass, String subDirPath, String dirNameFilter) {
		this.node = node;
		nodeMap = new NodeMap<N>(node, nodeClass, subDirPath, dirNameFilter);
	}
	
	public void addAlternateLocation(String subDirPath, String dirNameFilter)
	{
		nodeMap.addAlternateLocation(subDirPath, dirNameFilter);
	}
	
	public void addAdditionalNamedLocation(String itemName, String subDirPath)
	{
		nodeMap.addAdditionalNamedLocation(itemName, subDirPath);
	}
	
	public N item(String childName) {
		return child(nodeMap, childName);
	}
	
	public List<N> list() {
		return children(nodeMap);
	}
	
	@SuppressWarnings("unchecked")
	private N child(NodeMap<N> children, String childName) {
		if (!children.nodes.containsKey(childName)) {
			File childPath = children.getDir(childName);
			N child = (N) node.root().getRegisteredNode(childPath);
			
			if (child == null) {
				child = (N) NodeCreator.createNode(node.root(), node, childPath, childName, children.nodeClass);
			}
			
			children.nodes.put(childName, child);
		}
		
		return children.nodes.get(childName);
	}
	
	private List<N> children(NodeMap<N> children) {
		List<N> childList = new ArrayList<>();
		List<String> locatorNames = children.getLocatorNames();
		
		for (String locatorName : locatorNames) {
			childList.add(child(children, locatorName));
		}
		
		return childList;
	}
}
