package org.bladerunnerjs.utility.filemodification;

import java.io.File;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AssetContainer;
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
		if(node instanceof App || node instanceof AssetContainer) {
			File resetLastModifiedForFile = node.parentNode().dir();
			FileModificationInfo fileModificationInfo = fileModificationService.getFileModificationInfo(resetLastModifiedForFile);
			fileModificationInfo.resetLastModified();
		}
	}
}

