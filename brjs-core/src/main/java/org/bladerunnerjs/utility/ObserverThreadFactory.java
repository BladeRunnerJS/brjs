package org.bladerunnerjs.utility;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bladerunnerjs.memoization.PollingFileModificationObserverThread;
import org.bladerunnerjs.memoization.WatchKeyServiceFactory;
import org.bladerunnerjs.memoization.WatchingFileModificationObserverThread;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.ConfigException;


public class ObserverThreadFactory
{
	private static final Pattern POLLING_PATTERN = Pattern.compile("polling(:([0-9]+))?");
	private static final Pattern WATCHING_PATTERN = Pattern.compile("watching");
	private BRJS brjs;
	
	public ObserverThreadFactory(BRJS brjs)
	{
		this.brjs = brjs;
	}

	public Thread getObserverThread() throws ConfigException, IOException
	{
		String observerThreadOption = brjs.bladerunnerConf().getFileObserverValue();
	
		Matcher pollingMatcher = POLLING_PATTERN.matcher(observerThreadOption);
		Matcher watchingMatcher = WATCHING_PATTERN.matcher(observerThreadOption);
		
		if (pollingMatcher.matches()) {
			String pollingInterval = pollingMatcher.group(2);
			if (pollingInterval == null || pollingInterval.length() < 1) {
				pollingInterval = "1000";
			}
			return new PollingFileModificationObserverThread(brjs, Integer.parseInt(pollingInterval));
		}
		if (watchingMatcher.matches() || observerThreadOption.equals("")) {
			return new WatchingFileModificationObserverThread( brjs, new WatchKeyServiceFactory() );
		}
		throw new ConfigException(
			String.format("'%s' is not a valid observer thread config option. Possible values are '%s' or '%s'.", 
				observerThreadOption, POLLING_PATTERN.pattern(), WATCHING_PATTERN.pattern())
		);
	}

}
