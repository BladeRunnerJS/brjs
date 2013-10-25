package org.bladerunnerjs.model;

public class FileSetSourceObserver implements FileSetObserver {
	private final SourceObserver sourceObserver;
	
	public FileSetSourceObserver(SourceObserver sourceObserver) {
		this.sourceObserver = sourceObserver;
	}
	
	@Override
	public void onFileSetChanged() {
		sourceObserver.onSourceFilesChanged();
	}
}
