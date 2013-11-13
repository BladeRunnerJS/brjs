package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

public interface SourceLocation extends BRJSNode {
	App getApp();
	String getRequirePrefix();
	List<SourceFile> sourceFiles();
	SourceFile sourceFile(String requirePath);
	List<Resources> getResources(File srcDir);
	//TODO: remove this
	void addSourceObserver(SourceObserver sourceObserver);
}
