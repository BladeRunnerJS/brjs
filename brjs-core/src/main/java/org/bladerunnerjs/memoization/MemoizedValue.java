package org.bladerunnerjs.memoization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.FileAccessLimitScope;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class MemoizedValue<T extends Object> {
	private final List<FileModifiedChecker> watchList = new ArrayList<>();
	private final File[] watchItems;
	private boolean exceptionThrownOnLastCompute;
	private T value;
	private final RootNode rootNode;
	private final String valueIdentifier;
	
	
	public MemoizedValue(String valueIdentifier, Node node) {
		this(valueIdentifier, node.root(), (Object[])node.memoizedScopeFiles());
	}
	
	public MemoizedValue(String valueIdentifier, RootNode rootNode, Object... watchItems) { // take an array of objects so callers can pass in a mix of MemoizedFile and File
		this.valueIdentifier = valueIdentifier;
		this.rootNode = rootNode;
		
		if(watchItems.length == 0) {
			throw new IllegalStateException("At least one directory or file must be provided within the watch list.");
		}
		
		FileModificationRegistry fileModificationRegistry = rootNode.getFileModificationRegistry();
		
		List<File> watchItemsList = new ArrayList<>();
		for(Object o : watchItems) {
			File file;
			if (o instanceof MemoizedFile) {
				file = ((MemoizedFile) o).getUnderlyingFile();								
			} else if (o instanceof File) {
				file = (File) o;								
			} else {
				throw new RuntimeException("MemoizedValue watch items must be an instance of MemoizedFile or File");
			}
			watchList.add( new FileModifiedChecker(fileModificationRegistry, rootNode, file));				
			watchItemsList.add( file );
		}
		this.watchItems = watchItemsList.toArray(new File[0]);
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Exception> T value(Getter<E> getter) throws E {
		if (valueNeedsToBeRecomputed()) {
			
			try (FileAccessLimitScope scope = rootNode.io().limitAccessToWithin(valueIdentifier, watchItems)) {
				scope.getClass(); // reference scope to prevent compiler warnings
				exceptionThrownOnLastCompute = false;
				value = (T) getter.get();
			}
			catch(Throwable e) {
				exceptionThrownOnLastCompute = true;
				throw e;
			}
		}
		
		return value;
	}
	
	private boolean valueNeedsToBeRecomputed() {
		boolean valueNeedsToBeRecomputed = exceptionThrownOnLastCompute;
		
		for(FileModifiedChecker fileModifiedChecker : watchList) {
			if(fileModifiedChecker.hasChangedSinceLastCheck()) {
				valueNeedsToBeRecomputed = true;
			}
		}
		
		return valueNeedsToBeRecomputed;
	}
}
