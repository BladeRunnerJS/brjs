package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.AmbiguousRequirePathException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.file.AliasesFile;

public abstract class AbstractBundlableNode extends AbstractSourceLocation implements BundlableNode {
	private AliasesFile aliasesFile;
	
	public AbstractBundlableNode(RootNode rootNode, File dir) {
		super(rootNode, dir);
	}
	
	public abstract List<LinkedAssetFile> getSeedFiles();
	
	@Override
	public List<LinkedAssetFile> seedFiles() {
		List<LinkedAssetFile> seedFiles = new ArrayList<>();
		
		seedFiles.addAll(getSeedFiles());
		seedFiles.addAll(this.getSeedResources().seedResources());
		
		return seedFiles;
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
		SourceFile sourceFile = null;
		
		for(SourceLocation sourceLocation : getSourceLocations()) {
			SourceFile locationSourceFile = sourceLocation.sourceFile(requirePath);
			
			if(locationSourceFile != null) {
				if(sourceFile == null) {
					sourceFile = locationSourceFile;
				}
				else {
					throw new AmbiguousRequirePathException("'" + sourceFile.getUnderlyingFile().getPath() + "' and '" +
						locationSourceFile.getUnderlyingFile().getPath() + "' source files both available via require path '" +
						sourceFile.getRequirePath() + "'.");
				}
			}
		}
		
		return sourceFile;
	}
	
	@Override
	public BundleSet getBundleSet() throws ModelOperationException {
		return BundleSetCreator.createBundleSet(this);
	}
}
