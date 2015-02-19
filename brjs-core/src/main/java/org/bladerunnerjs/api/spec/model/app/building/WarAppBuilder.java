package org.bladerunnerjs.api.spec.model.app.building;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.utility.ZipUtility;



public class WarAppBuilder implements AppBuilder
{

	public void build(App app, MemoizedFile appWarFile) throws ModelOperationException {
		if (!appWarFile.getParentFile().exists()) throw new ModelOperationException("'" + appWarFile.getParentFile().getPath() + "' does not exist");
		if (appWarFile.exists()) throw new ModelOperationException("'" + appWarFile.getParentFile().getPath() + "' already exists");
		
		File exportDir = AppBuilderUtilis.getTemporaryExportDir(app);
		AppBuilderUtilis.build(app, exportDir);
		
		try
		{
			ZipUtility.zipFolder(exportDir, appWarFile, true);
			appWarFile.incrementFileVersion();
			org.apache.commons.io.FileUtils.deleteQuietly(exportDir);
		}
		catch (IOException ex)
		{
			throw new ModelOperationException(ex);
		}
	}
	
}
