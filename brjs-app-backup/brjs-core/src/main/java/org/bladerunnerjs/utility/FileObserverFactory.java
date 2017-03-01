package org.bladerunnerjs.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.FileObserver;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.memoization.PollingFileModificationObserver;
import org.bladerunnerjs.memoization.WatchingFileModificationObserver;


public class FileObserverFactory
{
	private static final Pattern POLLING_PATTERN = Pattern.compile("polling(:([0-9]+))?");
	private static final Pattern WATCHING_PATTERN = Pattern.compile("watching");

	public static FileObserver getObserver(BRJS brjs) throws ConfigException, IOException
	{
		String observerThreadOption = brjs.bladerunnerConf().getFileObserverValue();
	
		Matcher pollingMatcher = POLLING_PATTERN.matcher(observerThreadOption);
		Matcher watchingMatcher = WATCHING_PATTERN.matcher(observerThreadOption);
		
		if (pollingMatcher.matches()) {
			String pollingInterval = pollingMatcher.group(2);
			if (pollingInterval == null || pollingInterval.length() < 1) {
				pollingInterval = "1000";
			}
			return new PollingFileModificationObserver(brjs, Integer.parseInt(pollingInterval));
		}
		if (watchingMatcher.matches() || observerThreadOption.equals("")) {
			return new WatchingFileModificationObserver(brjs);
		}
		throw new ConfigException(
			String.format("'%s' is not a valid observer thread config option. Possible values are '%s' or '%s'.", 
				observerThreadOption, POLLING_PATTERN.pattern(), WATCHING_PATTERN.pattern())
		);
	}

	public static List<File> getBrjsRootDirs(BRJS brjs)
	{
		List<File> rootDirs = new ArrayList<>();
		rootDirs.add(brjs.dir().getUnderlyingFile());
		if (!brjs.appsFolder().getAbsolutePath().startsWith(brjs.dir().getAbsolutePath())) {
			rootDirs.add(brjs.appsFolder().getUnderlyingFile());
		}
		return rootDirs;
	}

}
