package org.bladerunnerjs.model.app.build;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.utility.FileUtility;

public class AppBuilder extends AbstractAppBuilder {
	
	public void preBuild(App app, File targetDir) throws ModelOperationException {
		File appExportDir = getAppExportDir(app, targetDir);
		if(appExportDir.exists()) throw new ModelOperationException("'" + appExportDir.getPath() + "' already exists.");
	}
	
	public void postBuild(File exportDir, App app, File targetDir) throws ModelOperationException {
		try
		{
			FileUtility.copyDirectoryIfExists(exportDir, getAppExportDir(app, targetDir));
		}
		catch (IOException ex)
		{
			throw new ModelOperationException(ex);
		}
	}
	
	private File getAppExportDir(App app, File targetDir) {
		return new File(targetDir, app.getName());
	}
	
}
