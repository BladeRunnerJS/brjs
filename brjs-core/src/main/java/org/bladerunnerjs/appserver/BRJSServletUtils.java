package org.bladerunnerjs.appserver;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;


public class BRJSServletUtils
{

	public static App localeAppForContext(BRJS brjs, ServletContext servletContext) throws ServletException
	{
		File servletContextFilePath = new File(servletContext.getRealPath("/"));
		MemoizedFile servletContextMemoizedFile = brjs.getMemoizedFile(servletContextFilePath);
		App app = brjs.locateAncestorNodeOfClass(servletContextMemoizedFile, App.class);
		if (app == null) {
			App brjsApp = brjs.app( servletContextFilePath.getName() );
			if (brjsApp.dirExists() && brjsApp.dir().equals(servletContextFilePath)) {
				app = brjsApp;
			}
		}
		
		if (app == null) {
			throw new ServletException("Unable to calculate app for the servlet context file path '" + servletContextFilePath + "'.");
		}
		
		return app;
	}
	
}
