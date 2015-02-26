package org.bladerunnerjs.api.memoization;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.model.FileAccessLimitScope;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class MemoizedValue<T extends Object> {
	
	public static final String RECALCULATING_VALUE_MSG = "Recalculating memoized value for '%s'";
	public static final String USING_MEMOIZED_VALUE_MSG = "Using memoized value for '%s' as no files have changed";
	
	private final List<FileModifiedChecker> watchList = new ArrayList<>();
	private final File[] watchItems;
	private boolean exceptionThrownOnLastCompute;
	private T value;
	private final RootNode rootNode;
	private final String valueIdentifier;
	private Logger logger;
	
	
	public MemoizedValue(String valueIdentifier, Node node) {
		this(valueIdentifier, node.root(), node.memoizedScopeFiles());
	}
	
	public MemoizedValue(String valueIdentifier, RootNode rootNode, File... watchItems) { // take an array of objects so callers can pass in a mix of MemoizedFile and File
		this.valueIdentifier = valueIdentifier;
		this.rootNode = rootNode;
		
		if(watchItems.length == 0) {
			throw new IllegalStateException("At least one directory or file must be provided within the watch list.");
		}
		
		FileModificationRegistry fileModificationRegistry = rootNode.getFileModificationRegistry();
		
		List<File> watchItemsList = new ArrayList<>();
		for(File file : watchItems) {
			watchList.add( new FileModifiedChecker(fileModificationRegistry, rootNode, file));				
			watchItemsList.add( file );
		}
		this.watchItems = watchItemsList.toArray(new File[0]);
		logger = rootNode.logger(this.getClass());
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Exception> T value(Getter<E> getter) throws E {
		if (valueNeedsToBeRecomputed()) {
			
			logger.debug(RECALCULATING_VALUE_MSG, valueIdentifier);
			
			try (FileAccessLimitScope scope = rootNode.io().limitAccessToWithin(valueIdentifier, watchItems)) {
				exceptionThrownOnLastCompute = false;
				value = (T) getter.get();
			}
			catch(Throwable e) {
				exceptionThrownOnLastCompute = true;
				throw e;
			}
		} else {
			logger.debug(USING_MEMOIZED_VALUE_MSG, valueIdentifier);
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
