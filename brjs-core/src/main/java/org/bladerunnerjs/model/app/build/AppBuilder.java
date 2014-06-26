package org.bladerunnerjs.model.app.build;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.exception.ModelOperationException;

public class AppBuilder extends AbstractAppBuilder {
	
	public void preBuild(App app, File appBuildDir) throws ModelOperationException {
		if(appBuildDir.exists()) throw new ModelOperationException("'" + appBuildDir.getPath() + "' already exists.");
	}
	
	public void postBuild(File exportDir, App app, File appBuildDir) throws ModelOperationException {
		try
		{
			FileUtils.copyDirectory(exportDir, appBuildDir);
		}
		catch (IOException ex)
		{
			throw new ModelOperationException(ex);
		}
	}
	
}
