package org.bladerunnerjs.model;

import java.io.File;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.AbstractNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;


public abstract class AbstractBRJSNode extends AbstractNode implements BRJSNode {
	public AbstractBRJSNode(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	public BRJS root() {
		return (BRJS) super.root();
	}
	
	@Override
	public void populate() throws InvalidNameException, ModelUpdateException {
		BRJSNodeHelper.populate(this);
	}
	
	@Override
	public long lastModified() {
		return (dir.exists()) ? root().getFileIterator(dir).getLastModified() : 0;
	}
	
	@Override
	public String getTemplateName() {
		return BRJSNodeHelper.getTemplateName(this);
	}
}
