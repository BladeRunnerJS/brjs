package org.bladerunnerjs.model.app.building;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.utility.FileUtility;

public class StaticAppBuilder implements AppBuilder {
	
	public void build(App app, MemoizedFile appBuildDir) throws ModelOperationException {
		if (!appBuildDir.exists()) throw new ModelOperationException("'" + appBuildDir.getPath() + "' does not exist");
		
		File exportDir = AppBuilderUtilis.getTemporaryExportDir(app);
		AppBuilderUtilis.build(app, exportDir);
		
		try
		{
			FileUtility.moveDirectoryContents(exportDir, appBuildDir);
			FileUtils.deleteQuietly(exportDir);
		}
		catch (IOException ex)
		{
			throw new ModelOperationException(ex);
		}
	}
	
}
