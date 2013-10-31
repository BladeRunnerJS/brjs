package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.core.plugin.bundlesource.BundleSourcePlugin;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.RootNode;

public abstract class AbstractSourceLocation extends AbstractBRJSNode implements SourceLocation {
	private final NodeItem<DirNode> src = new NodeItem<>(DirNode.class, "src");
	private final CompositeFileSet<SourceFile> sourceFileSet = new CompositeFileSet<SourceFile>();
	private final Resources resources;
	
	public AbstractSourceLocation(RootNode rootNode, File dir) {
		resources = new DeepResources(dir);
		
		for(BundleSourcePlugin bundleSourcePlugin : ((BRJS) rootNode).bundleSources()) {
			sourceFileSet.addFileSet(bundleSourcePlugin.getFileSetFactory().getSourceFileSet(this));
		}
	}
	
	public DirNode src() {
		return item(src);
	}
	
	@Override
	public List<SourceFile> sourceFiles() {
		return sourceFileSet.getFiles();
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
		sourceFileSet.addObserver(new FileSetSourceObserver(sourceObserver));
	}
}
