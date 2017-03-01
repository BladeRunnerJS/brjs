package org.bladerunnerjs.api.spec.model.app.building;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.utility.FileUtils;
import org.bladerunnerjs.utility.MissingAppJarChecker;
import org.bladerunnerjs.utility.MissingAppJarsException;

public class StaticAppBuilder implements AppBuilder {
	
	public void build(App app, MemoizedFile appBuildDir) throws ModelOperationException {
		if (!appBuildDir.exists()) throw new ModelOperationException("'" + appBuildDir.getPath() + "' does not exist");

		if (app.file("WEB-INF").isDirectory() && !MissingAppJarChecker.hasCorrectApplicationLibVersions(app)) {
			throw new MissingAppJarsException(app);
		}

		File exportDir = AppBuilderUtilis.getTemporaryExportDir(app);
		AppBuilderUtilis.build(app, exportDir);
		
		try
		{
			FileUtils.moveDirectoryContents(app.root(), exportDir, appBuildDir);
		}
		catch (IOException ex)
		{
			throw new ModelOperationException(ex);
		}
		finally 
		{
			org.apache.commons.io.FileUtils.deleteQuietly(exportDir);			
		}
	}
	
}
