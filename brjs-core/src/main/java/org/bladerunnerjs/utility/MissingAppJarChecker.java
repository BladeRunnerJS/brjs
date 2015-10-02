package org.bladerunnerjs.utility;

import java.io.File;

import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.memoization.MemoizedFile;


public class MissingAppJarChecker
{

	public static boolean hasCorrectApplicationLibVersions(App app)
	{
		File webinfLib = app.file("WEB-INF/lib");
		MemoizedFile appJarsDir = app.root().appJars().dir();
		if (!webinfLib.exists() || !appJarsDir.exists()) {
			return true;
		}
		
		boolean containsValidJars = true;
		
		for (File appJar : FileUtils.listFiles(webinfLib, new PrefixFileFilter("brjs-"), null)) {
			File sdkJar = app.root().appJars().file(appJar.getName());
			if (!sdkJar.exists()) {
				containsValidJars = false;
			}
		}
		
		for (File sdkJar : FileUtils.listFiles(appJarsDir, new PrefixFileFilter("brjs-"), null)) {
			File appJar = new File(webinfLib, sdkJar.getName());
			if (!appJar.exists()) {
				containsValidJars = false;
			}
		}
		
		return containsValidJars;
	}
	
}
