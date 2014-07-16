package org.bladerunnerjs.model;

import java.io.File;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.model.engine.AbstractRootNode;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;


public abstract class AbstractBRJSRootNode extends AbstractRootNode implements BRJSNode {
	public AbstractBRJSRootNode(File dir, LoggerFactory loggerFactory) throws InvalidSdkDirectoryException {
		super(dir, loggerFactory);
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
	public String getTemplateName() {
		return BRJSNodeHelper.getTemplateName(this);
	}
}
