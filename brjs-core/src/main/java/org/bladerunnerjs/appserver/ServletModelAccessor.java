package org.bladerunnerjs.appserver;

import java.io.File;

import javax.servlet.ServletContext;

import org.bladerunnerjs.logging.NullLogConfigurator;
import org.bladerunnerjs.model.BRJS;

/**
 * A utility class so Servlets and Filters can share a single BRJS model instance.
 * 
 * WARNING: Do not use this class. Any plugins that should have a reference to the BRJS instance will be provided it in the setBRJS() method. 
 *
 */
public class ServletModelAccessor {
	private static BRJS model;
	
	public static synchronized BRJS initializeModel(ServletContext servletContext) {
		return initializeModel( new File(servletContext.getRealPath("/")) );
	}
	
	public static synchronized BRJS initializeModel(File path) {
		return initializeModel( new BRJS(path, new NullLogConfigurator()) );
	}
	
	public static synchronized BRJS initializeModel(BRJS brjs) {
		if(model == null) {
			model = brjs;
		}
		
		return model;
	}

	public static void reset()
	{
		model = null;		
	}
}