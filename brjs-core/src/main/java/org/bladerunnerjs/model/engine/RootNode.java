package org.bladerunnerjs.model.engine;

import java.io.File;

import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.IO;
import org.bladerunnerjs.model.FileInfo;
import org.bladerunnerjs.model.exception.NodeAlreadyRegisteredException;


public interface RootNode extends Node {
	Logger logger(LoggerType type, Class<?> classRef);	
	ConsoleWriter getConsoleWriter();
	void setConsoleWriter(ConsoleWriter consoleWriter);
	boolean isRootDir(File dir);
	Node locateFirstAncestorNode(File file);
	<N extends Node> N locateAncestorNodeOfClass(File file, Class<N> nodeClass);
	<N extends Node> N locateAncestorNodeOfClass(Node node, Class<N> nodeClass);
	FileInfo getFileInfo(File dir);
	IO io();
	
	// these two methods, implemented by AbstractRootNode, are used by AbstractNode
	void registerNode(Node node, boolean makeUnique) throws NodeAlreadyRegisteredException;
	void registerNode(Node node) throws NodeAlreadyRegisteredException;
	void clearRegisteredNode(Node node);
	Node getRegisteredNode(File childPath);
}
