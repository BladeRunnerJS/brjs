package org.bladerunnerjs.model;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.AbstractNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;


public abstract class AbstractBRJSNode extends AbstractNode implements BRJSNode {
	public AbstractBRJSNode(RootNode rootNode, Node parent, MemoizedFile dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	public BRJS root() {
		return (BRJS) super.root();
	}
	
	@Override
	public void populate(String templateGroup) throws InvalidNameException, ModelUpdateException {
		BRJSNodeHelper.populate(this, templateGroup);
	}
	
	@Override
	public String getTemplateName() {
		return BRJSNodeHelper.getTemplateName(this);
	}
}
