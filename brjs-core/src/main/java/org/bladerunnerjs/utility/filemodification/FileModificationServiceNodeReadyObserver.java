package org.bladerunnerjs.utility.filemodification;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.plugin.EventObserver;

// TODO: find a better catch-all solution to knowing when the model has been changed programmatically
public class FileModificationServiceNodeReadyObserver implements EventObserver {
	private FileModificationService fileModificationService;
	
	public FileModificationServiceNodeReadyObserver(FileModificationService fileModificationService) {
		this.fileModificationService = fileModificationService;
	}
	
	public void setFileModificationService(FileModificationService fileModificationService) {
		this.fileModificationService = fileModificationService;
	}
	
	@Override
	public void onEventEmitted(Event event, Node node) {
		fileModificationService.getFileModificationInfo(node.dir()).resetLastModified();
	}
}

