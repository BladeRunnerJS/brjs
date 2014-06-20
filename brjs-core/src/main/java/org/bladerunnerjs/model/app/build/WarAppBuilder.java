package org.bladerunnerjs.model.app.build;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.utility.FileUtility;



public class WarAppBuilder extends AppBuilder
{

	public void preBuild(App app, File appWarFile) throws ModelOperationException {
		if(appWarFile.exists()) throw new ModelOperationException("'" + appWarFile.getPath() + "' already exists.");
	}
	
	public void postBuild(File exportDir, App app, File appWarFile) throws ModelOperationException {
		try
		{
			FileUtility.zipFolder(exportDir, appWarFile, true);
		}
		catch (IOException ex)
		{
			throw new ModelOperationException(ex);
		}
	}
	
}
