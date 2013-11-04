package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.core.plugin.bundlesource.BundleSourcePlugin;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.RootNode;

public abstract class AbstractSourceLocation extends AbstractBRJSNode implements SourceLocation {
	private final NodeItem<DirNode> src = new NodeItem<>(DirNode.class, "src");
	private CompositeFileSet<SourceFile> sourceFileSet ;
	private final Resources resources;
	
	public AbstractSourceLocation(RootNode rootNode, File dir) {
		resources = new DeepResources(dir);
	}
	
	public DirNode src() {
		return item(src);
	}
	
	@Override
	public List<SourceFile> sourceFiles() {
		if(sourceFileSet == null) {
			sourceFileSet = new CompositeFileSet<SourceFile>();
			
			for(BundleSourcePlugin bundleSourcePlugin : ((BRJS) rootNode).bundleSourcePlugins()) {
				sourceFileSet.addFileSet(bundleSourcePlugin.getFileSetFactory().getSourceFileSet(this));
			}
		}
		
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
