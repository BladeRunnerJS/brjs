package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.IO;
import org.bladerunnerjs.model.FileInfo;
import org.bladerunnerjs.model.exception.MultipleNodesForPathException;
import org.bladerunnerjs.model.exception.NodeAlreadyRegisteredException;


public interface RootNode extends Node {
	Logger logger(Class<?> classRef);	
	boolean isRootDir(File dir);
	Node locateFirstAncestorNode(File file);
	<N extends Node> N locateAncestorNodeOfClass(File file, Class<N> nodeClass);
	<N extends Node> N locateAncestorNodeOfClass(Node node, Class<N> nodeClass);
	FileInfo getFileInfo(File dir);
	IO io();
	
	void registerNode(Node node) throws NodeAlreadyRegisteredException;
	void clearRegisteredNode(Node node);
	Node getRegisteredNode(File childPath) throws MultipleNodesForPathException;
	List<Node> getRegisteredNodes(File childPath);
}
