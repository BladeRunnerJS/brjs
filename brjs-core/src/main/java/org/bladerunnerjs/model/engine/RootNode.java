package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.memoization.FileModificationRegistry;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.IO;
import org.bladerunnerjs.model.exception.MultipleNodesForPathException;
import org.bladerunnerjs.model.exception.NodeAlreadyRegisteredException;


public interface RootNode extends Node {
	Logger logger(Class<?> classRef);	
	boolean isRootDir(File dir); // this cant be a memoizedFile since its used before the memoization accessor is initialised
	Node locateFirstAncestorNode(MemoizedFile file);
	Node locateFirstAncestorNode(MemoizedFile file, Class<? extends Node> nodeClass);
	<N extends Node> N locateAncestorNodeOfClass(File file, Class<N> nodeClass);
	<N extends Node> N locateAncestorNodeOfClass(MemoizedFile file, Class<N> nodeClass);
	<N extends Node> N locateAncestorNodeOfClass(Node node, Class<N> nodeClass);
	IO io();
	MemoizedFile getMemoizedFile(File file);
	MemoizedFile getMemoizedFile(File dir, String filePath);
	
	boolean isNodeRegistered(Node node);
	void registerNode(Node node) throws NodeAlreadyRegisteredException;
	void clearRegisteredNode(Node node);
	Node getRegisteredNode(MemoizedFile childPath) throws MultipleNodesForPathException;
	Node getRegisteredNode(MemoizedFile childPath, Class<? extends Node> nodeClass) throws MultipleNodesForPathException;
	List<Node> getRegisteredNodes(MemoizedFile childPath);
	FileModificationRegistry getFileModificationRegistry();
}
