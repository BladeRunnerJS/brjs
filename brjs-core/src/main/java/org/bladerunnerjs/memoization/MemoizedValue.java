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
		this(valueIdentifier, node.root(), node.memoizedScopeFiles());
	}
	
	public MemoizedValue(String valueIdentifier, RootNode rootNode, File... watchItems) {
		this.valueIdentifier = valueIdentifier;
		this.watchItems = watchItems;
		this.rootNode = rootNode;
		
		if(watchItems.length == 0) {
			throw new IllegalStateException("At least one directory or file must be provided within the watch list.");
		}
		
		FileModificationRegistry fileModificationRegistry = rootNode.getFileModificationRegistry();
		
		for(File file : watchItems) {
			watchList.add( new FileModifiedChecker(fileModificationRegistry, rootNode, file));
		}
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
