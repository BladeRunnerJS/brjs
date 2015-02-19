package org.bladerunnerjs.api.spec.model.app.building;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.utility.FileUtils;

public class StaticAppBuilder implements AppBuilder {
	
	public void build(App app, MemoizedFile appBuildDir) throws ModelOperationException {
		if (!appBuildDir.exists()) throw new ModelOperationException("'" + appBuildDir.getPath() + "' does not exist");
		
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
