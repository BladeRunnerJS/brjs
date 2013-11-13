package org.bladerunnerjs.model;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.RootNode;

public abstract class AbstractSourceLocation extends AbstractBRJSNode implements SourceLocation {
	private final NodeItem<DirNode> src = new NodeItem<>(DirNode.class, "src");
	private final NodeItem<DirNode> resources = new NodeItem<>(DirNode.class, "resources");
	protected final SourceLocationResources sourceLocationResources;
	
	public AbstractSourceLocation(RootNode rootNode, File dir) {
		init(rootNode, rootNode, dir);
		
		sourceLocationResources = new SourceLocationResources((BRJS) rootNode, src().dir(), resources().dir());
	}
	
	public DirNode src() {
		return item(src);
	}
	
	public DirNode resources()
	{
		return item(resources);
	}
	
	@Override
	public App getApp() {
		Node node = this.parentNode();
		
		while(!(node instanceof App)) {
			node = node.parentNode();
		}
		
		return (App) node;
	}
	
	@Override
	public List<SourceFile> sourceFiles() {
		List<SourceFile> sourceFiles = new LinkedList<SourceFile>();
			
		for(BundlerPlugin bundlerPlugin : ((BRJS) rootNode).bundlerPlugins()) {
			sourceFiles.addAll(bundlerPlugin.getAssetFileAccessor().getSourceFiles(this));
		}
		
		return sourceFiles;
	}
	
	@Override
	public SourceFile sourceFile(String requirePath) {
		for(SourceFile sourceFile : sourceFiles()) {
			if(sourceFile.getRequirePath().equals(requirePath)) {
				return sourceFile;
			}
		}
		
		return null;
	}
	
	@Override
	public List<Resources> getResources(File srcDir) {
		return sourceLocationResources.getResources(srcDir);
	}
	
	@Override
	public void addSourceObserver(SourceObserver sourceObserver) {
	}
}
