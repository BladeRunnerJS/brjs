package org.bladerunnerjs.memoization;

import java.io.File;
import java.util.ArrayList;
//import java.util.Date;
import java.util.List;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.FileAccessLimitScope;
import org.bladerunnerjs.utility.filemodification.FileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.InfoFileModifiedChecker;

public class MemoizedValue<T extends Object> {
	private final List<FileModifiedChecker> watchList = new ArrayList<>();
	private final File[] watchItems;
	private final String valueRecomputedLogMessage;
	private boolean exceptionThrownOnLastCompute;
	private final BRJS brjs;
	private T value;
	private final Logger logger;
	private final String valueIdentifier;
	
	public MemoizedValue(String valueIdentifier, BRJSNode node) {
		this(valueIdentifier, node.root(), node.scopeFiles());
	}
	
	public MemoizedValue(String valueIdentifier, BRJS brjs, File... watchItems) {
		this.valueIdentifier = valueIdentifier;
		this.brjs = brjs;
		this.watchItems = watchItems;
		logger = brjs.logger(LoggerType.UTIL, getClass());
		valueRecomputedLogMessage = "Recomputing '" + valueIdentifier + "'.";
		
		if(watchItems.length == 0) {
			throw new IllegalStateException("At least one directory or file must be provided within the watch list.");
		}
		
		for(File watchItem : watchItems) {
			watchList.add(new InfoFileModifiedChecker(brjs.getFileInfo(watchItem)));
		}
	}
	
	@SuppressWarnings("unchecked")
	public <E extends Exception> T value(Getter<E> getter) throws E {
		if(valueNeedsToBeRecomputed()) {
			logger.debug(valueRecomputedLogMessage);
			
			try(FileAccessLimitScope scope = brjs.io().limitAccessToWithin(valueIdentifier, watchItems)) {
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
