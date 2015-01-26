package org.bladerunnerjs.appserver;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;


public class BRJSServletUtils
{

	public static App localeAppForContext(BRJS brjs, ServletContext servletContext) throws ServletException
	{
		File servletContextFilePath = new File(servletContext.getRealPath("/"));
		MemoizedFile servletContextMemoizedFile = brjs.getMemoizedFile(servletContextFilePath);
		App app = brjs.locateAncestorNodeOfClass(servletContextMemoizedFile, App.class);
		if (app != null) {
			return app;
		}
		
		App brjsApp = brjs.app( servletContextFilePath.getName() );
		try {
			if (brjsApp.dirExists() && (
					brjsApp.dir().getAbsolutePath().equals(servletContextFilePath.getAbsolutePath()) || brjsApp.dir().getCanonicalPath().equals(servletContextFilePath.getCanonicalPath()) )) {
				return brjsApp;
			}
		} catch (Exception ex) {
			
		}
		
		throw new ServletException("Unable to calculate app for the servlet context file path '" + servletContextFilePath + "'.");
	}
	
}
