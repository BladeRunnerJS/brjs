package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.AmbiguousRequirePathException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.file.AliasesFile;

public abstract class AbstractBundlableNode extends AbstractSourceLocation implements BundlableNode {
	private AliasesFile aliasesFile;
	private CompositeFileSet<LinkedAssetFile> seedFileSet;
	
	public AbstractBundlableNode(RootNode rootNode, File dir) {
		super(rootNode, dir);
	}
	
	public abstract FileSet<LinkedAssetFile> getSeedFileSet();
	
	@Override
	public List<LinkedAssetFile> seedFiles() {
		if(seedFileSet == null) {
			seedFileSet = new CompositeFileSet<LinkedAssetFile>();
			
			seedFileSet.addFileSet(getSeedFileSet());
			
			for(BundlerPlugin bundlerPlugin : ((BRJS) rootNode).bundlerPlugins()) {
				seedFileSet.addFileSet(bundlerPlugin.getFileSetFactory().getLinkedResourceFileSet(this.getSeedResources()));
			}
		}
		
		return seedFileSet.getFiles();
	}
	
	private Resources getSeedResources() {
		return sourceLocationResources.getSeedResources();
	}
	
	@Override
	public AliasesFile aliases() {
		if(aliasesFile == null) {
			aliasesFile = new AliasesFile(dir(), "resources/aliases.xml");
		}
		
		return aliasesFile;
	}
	
	@Override
	public SourceFile getSourceFile(String requirePath) throws AmbiguousRequirePathException {
		// TODO: implement this method
		return null;
	}
	
	@Override
	public BundleSet getBundleSet() throws ModelOperationException {
		return BundleSetCreator.createBundleSet(this);
	}
}
