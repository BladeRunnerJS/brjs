package org.bladerunnerjs.model.engine;

import java.io.File;

import org.bladerunnerjs.core.console.ConsoleWriter;
import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;


public interface RootNode extends Node {
	Logger logger(LoggerType type, Class<?> classRef);
	ConsoleWriter getConsoleWriter();
	void setConsoleWriter(ConsoleWriter consoleWriter);
	boolean isRootDir(File dir);
	Node locateFirstAncestorNode(File file);
	<N extends Node> N locateAncestorNodeOfClass(File file, Class<N> nodeClass);
	
	// these two methods, implemented by AbstractRootNode, are used by AbstractNode
	void registerNode(Node node);
	Node getRegisteredNode(File childPath);
}
