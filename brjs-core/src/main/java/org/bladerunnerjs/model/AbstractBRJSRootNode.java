package org.bladerunnerjs.model;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.logging.LoggerFactory;
import org.bladerunnerjs.model.engine.AbstractRootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.StringLengthComparator;


public abstract class AbstractBRJSRootNode extends AbstractRootNode implements BRJSNode {
	public AbstractBRJSRootNode(File dir, LoggerFactory loggerFactory, ConsoleWriter consoleWriter) {
		super(dir, loggerFactory, consoleWriter);
	}
	
	@Override
	public BRJS root() {
		return (BRJS) super.root();
	}
	
	@Override
	public void populate() throws InvalidNameException, ModelUpdateException {
		Map<String, String> emptyTransformations = new TreeMap<>(new StringLengthComparator());
		populate(emptyTransformations);
	}
	
	@Override
	public void populate(Map<String, String> transformations) throws InvalidNameException, ModelUpdateException {
		BRJSNodeHelper.populate(this, transformations);
	}
	
	@Override
	public long lastModified() {
		return (dir.exists()) ? root().getModificationInfo(dir).getLastModified() : 0;
	}
	
	@Override
	public String getTemplateName() {
		return BRJSNodeHelper.getTemplateName(this);
	}
}
