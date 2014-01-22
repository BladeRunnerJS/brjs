package org.bladerunnerjs.utility.filemodification;

import org.bladerunnerjs.model.BRJSNode;

public class NodeFileModifiedChecker implements FileModifiedChecker {
	private BRJSNode brjsNode;
	private long lastModifiedTime = 0;
	
	public NodeFileModifiedChecker(BRJSNode brjsNode) {
		this.brjsNode = brjsNode;
	}
	
	@Override
	public boolean hasChangedSinceLastCheck() {
		boolean hasChangedSinceLastCheck = (brjsNode.lastModified() > lastModifiedTime);
		lastModifiedTime = brjsNode.lastModified();
		
		return hasChangedSinceLastCheck;
	}
}
