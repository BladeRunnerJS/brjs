package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.exception.AmbiguousRequirePathException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.file.AliasesFile;

public abstract class AbstractBundlableNode extends AbstractSourceLocation implements BundlableNode {
	private AliasesFile aliasesFile;
	
	public AbstractBundlableNode(File dir) {
		super(dir);
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
