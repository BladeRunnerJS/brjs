package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.engine.NodeItem;

public abstract class AbstractSourceLocation extends AbstractBRJSNode implements SourceLocation {
	private final NodeItem<DirNode> src = new NodeItem<>(DirNode.class, "src");
	private final FileSet<SourceFile> sourceFiles;
	private final Resources resources;
	
	public AbstractSourceLocation(File dir) {
		sourceFiles = FileSetBuilder.createSourceFileSetForDir(this)
			.includingPaths("src/**.js")
			.build();
		resources = new DeepResources(dir);
	}
	
	public DirNode src() {
		return item(src);
	}
	
	@Override
	public List<SourceFile> sourceFiles() {
		return sourceFiles.getFiles();
	}
	
	@Override
	public SourceFile sourceFile(String requirePath) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Resources getResources(String srcPath) {
		return resources;
	}
	
	@Override
	public void addSourceObserver(SourceObserver sourceObserver) {
		sourceFiles.addObserver(new FileSetSourceObserver(sourceObserver));
	}
}
