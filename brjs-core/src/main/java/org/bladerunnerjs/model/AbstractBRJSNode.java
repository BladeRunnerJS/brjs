package org.bladerunnerjs.model;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.AbstractNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.StringLengthComparator;
import org.bladerunnerjs.utility.filemodification.FileModificationInfo;


public abstract class AbstractBRJSNode extends AbstractNode implements BRJSNode {
	private FileModificationInfo fileModificationInfo;
	
	public AbstractBRJSNode(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	public BRJS root() {
		return (BRJS) super.root();
	}
	
	@Override
	public void populate() throws InvalidNameException, ModelUpdateException {
		// With the addition of the populate(Map) most implementations will call this
		// and overrides of populate(Map) will handled the domain specific functionality.
		Map<String, String> emptyTransformations = new TreeMap<>(new StringLengthComparator());
		populate(emptyTransformations);
	}
	
	@Override
	public void populate(Map<String, String> transformations) throws InvalidNameException, ModelUpdateException {
		BRJSNodeHelper.populate(this, transformations);
	}
	
	@Override
	public long lastModified() {
		if((fileModificationInfo == null) && dir.exists()) {
			fileModificationInfo = root().getModificationInfo(dir);
		}
		
		return (fileModificationInfo != null) ? fileModificationInfo.getLastModified() : 0;
	}
	
	@Override
	public String getTemplateName() {
		return BRJSNodeHelper.getTemplateName(this);
	}
	
}
