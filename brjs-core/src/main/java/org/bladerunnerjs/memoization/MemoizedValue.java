package org.bladerunnerjs.memoization;

import java.io.File;
import java.util.ArrayList;
//import java.util.Date;
import java.util.List;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.FileAccessLimitScope;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.filemodification.FileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.InfoFileModifiedChecker;

public class MemoizedValue<T extends Object> {
	private final List<FileModifiedChecker> watchList = new ArrayList<>();
	private final File[] watchItems;
	private boolean exceptionThrownOnLastCompute;
	private final RootNode rootNode;
	private T value;
	private final Logger logger;
	private final String valueIdentifier;
	
	private static final String RECOMPUTING_LOG_MSG = "Recomputing '%s'.";
	
	public MemoizedValue(String valueIdentifier, Node node) {
		this(valueIdentifier, node.root(), node.memoizedScopeFiles());
	}
	
	public MemoizedValue(String valueIdentifier, RootNode rootNode, File... watchItems) {
		this.valueIdentifier = valueIdentifier;
		this.rootNode = rootNode;
		this.watchItems = watchItems;
		logger = rootNode.logger(getClass());
		
		if(watchItems.length == 0) {
			throw new IllegalStateException("At least one directory or file must be provided within the watch list.");
		}
		
		File primaryFile = watchItems[0];
		for(File file : watchItems) {
			watchList.add(new InfoFileModifiedChecker(rootNode.getFileSetInfo(file, primaryFile)));
		}
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Exception> T value(Getter<E> getter) throws E {
		if(valueNeedsToBeRecomputed()) {
			logger.debug( RECOMPUTING_LOG_MSG, valueIdentifier );
			
			try(FileAccessLimitScope scope = rootNode.io().limitAccessToWithin(valueIdentifier, watchItems)) {
				scope.preventCompilerWarning();
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
