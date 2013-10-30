package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

public class CompositeFileSet<AF extends AssetFile> implements FileSet<AF> {
	private List<FileSet<AF>> fileSets = new ArrayList<FileSet<AF>>();
	private List<FileSetObserver> observers = new ArrayList<>();
	private FileSetObserver compositeObserver = new CompositeFileSetObserver();
	
	public void addFileSet(FileSet<AF> fileSet) {
		fileSet.addObserver(compositeObserver);
		fileSets.add(fileSet);
	}
	
	@Override
	public List<AF> getFiles() {
		List<AF> files = new ArrayList<AF>();
		
		for(FileSet<AF> fileSet : fileSets) {
			files.addAll(fileSet.getFiles());
		}
		
		return files;
	}
	
	@Override
	public void addObserver(FileSetObserver fileSetObserver) {
		observers.add(fileSetObserver);
	}
	
	private class CompositeFileSetObserver implements FileSetObserver {
		@Override
		public void onFileSetChanged() {
			for(FileSetObserver observer : observers) {
				observer.onFileSetChanged();
			}
		}
	}
}
