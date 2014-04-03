package org.bladerunnerjs.memoization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.FileAccessLimitScope;
import org.bladerunnerjs.utility.filemodification.FileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.InfoFileModifiedChecker;

public class MemoizedValue<T extends Object> {
	private final List<FileModifiedChecker> watchList = new ArrayList<>();
	private final File[] watchItems;
	private final BRJS brjs;
	private T value;
	
	public MemoizedValue(BRJS brjs, File... watchItems) {
		this.brjs = brjs;
		this.watchItems = watchItems;
		
		if(watchItems.length == 0) {
			throw new IllegalStateException("At least one directory or file must be provided within the watch list.");
		}
		
		for(File watchItem : watchItems) {
			watchList.add(new InfoFileModifiedChecker(brjs.getModificationInfo(watchItem)));
		}
	}
	
	@SuppressWarnings("unchecked")
	public T value(Getter getter) {
		if(valueNeedsToBeRecomputed()) {
			try(FileAccessLimitScope scope = brjs.io().limitAccessToWithin(watchItems)) {
				scope.preventCompilerWarning();
				value = (T) getter.get();
			}
		}
		
		return value;
	}
	
	private boolean valueNeedsToBeRecomputed() {
		boolean valueNeedsToBeRecomputed = false;
		
		for(FileModifiedChecker fileModifiedChecker : watchList) {
			if(fileModifiedChecker.hasChangedSinceLastCheck()) {
				valueNeedsToBeRecomputed = true;
			}
		}
		
		return valueNeedsToBeRecomputed;
	}
}
