package org.bladerunnerjs.model.engine;

import java.io.File;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.exception.NodeAlreadyRegisteredException;
import org.bladerunnerjs.utility.DirectoryIterator;
import org.bladerunnerjs.utility.FileIterator;


public interface RootNode extends Node {
	Logger logger(LoggerType type, Class<?> classRef);	
	ConsoleWriter getConsoleWriter();
	void setConsoleWriter(ConsoleWriter consoleWriter);
	boolean isRootDir(File dir);
	Node locateFirstAncestorNode(File file);
	<N extends Node> N locateAncestorNodeOfClass(File file, Class<N> nodeClass);
	DirectoryIterator getDirectoryIterator(File dir);
	FileIterator createFileIterator(File dir, IOFileFilter fileFilter);
	
	// these two methods, implemented by AbstractRootNode, are used by AbstractNode
	void registerNode(Node node) throws NodeAlreadyRegisteredException;
	Node getRegisteredNode(File childPath);
}
