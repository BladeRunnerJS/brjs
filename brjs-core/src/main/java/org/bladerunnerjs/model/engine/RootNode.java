package org.bladerunnerjs.model.engine;

import java.io.File;

import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.exception.NodeAlreadyRegisteredException;


public interface RootNode extends Node {
	Logger logger(LoggerType type, Class<?> classRef);	
	ConsoleWriter getConsoleWriter();
	void setConsoleWriter(ConsoleWriter consoleWriter);
	boolean isRootDir(File dir);
	Node locateFirstAncestorNode(File file);
	<N extends Node> N locateAncestorNodeOfClass(File file, Class<N> nodeClass);
	
	// these two methods, implemented by AbstractRootNode, are used by AbstractNode
	void registerNode(Node node) throws NodeAlreadyRegisteredException;
	Node getRegisteredNode(File childPath);
}
