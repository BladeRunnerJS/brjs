package org.bladerunnerjs.model.engine;

import java.io.File;
import java.util.List;

public interface NodeMapLocator
{
	List<String> getDirs(File sourceDir);
	
	boolean canHandleName(String childName);
	
	String getDirName(String childName);
}
