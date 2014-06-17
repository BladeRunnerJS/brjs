package org.bladerunnerjs.model.app.build;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.utility.FileUtility;



public class WarAppBuilder extends AppBuilder
{

	public void preBuild(App app, File targetDir) throws ModelOperationException {
		File warExportFile = getWarExportFile(app, targetDir);
		if(warExportFile.exists()) throw new ModelOperationException("'" + warExportFile.getPath() + "' already exists.");
	}
	
	public void postBuild(File exportDir, App app, File targetDir) throws ModelOperationException {
		try
		{
			FileUtility.zipFolder(exportDir, getWarExportFile(app, targetDir), true);
		}
		catch (IOException ex)
		{
			throw new ModelOperationException(ex);
		}
	}
	
	private File getWarExportFile(App app, File targetDir) {
		return new File(targetDir, app.getName()+".war");
	}
	
}
