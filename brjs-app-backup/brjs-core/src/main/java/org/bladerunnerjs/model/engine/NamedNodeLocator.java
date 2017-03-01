package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.List;

public interface NamedNodeLocator
{
	List<String> getLogicalNodeNames(File sourceDir);
	boolean couldSupportLogicalNodeName(String logicalNodeName);
	String getDirName(String logicalNodeName);
}
