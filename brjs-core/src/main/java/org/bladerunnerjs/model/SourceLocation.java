package org.bladerunnerjs.model;

import java.util.List;

public interface SourceLocation extends BRJSNode {
	String getRequirePrefix();
	List<SourceFile> sourceFiles();
	SourceFile sourceFile(String requirePath);
	Resources getResources(String srcPath);
	void addSourceObserver(SourceObserver sourceObserver);
}
