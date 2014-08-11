package org.bladerunnerjs.model.app.building;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.utility.FileUtility;



public class WarAppBuilder implements AppBuilder
{

	public void build(App app, File appWarFile) throws ModelOperationException {
		if(appWarFile.exists()) throw new ModelOperationException("'" + appWarFile.getPath() + "' already exists.");
		
		File exportDir = AppBuilderUtilis.getTemporaryExportDir(app);
		AppBuilderUtilis.build(app, exportDir);
		
		try
		{
			FileUtility.zipFolder(exportDir, appWarFile, true);
			FileUtils.deleteQuietly(exportDir);
		}
		catch (IOException ex)
		{
			throw new ModelOperationException(ex);
		}
	}
	
}
