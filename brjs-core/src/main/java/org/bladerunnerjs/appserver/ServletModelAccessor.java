package org.bladerunnerjs.appserver;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

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
	private static ReentrantLock lock = new ReentrantLock();
	
	public static synchronized void initializeModel(ServletContext servletContext) {
		initializeModel( new File(servletContext.getRealPath("/")) );
	}
	
	public static synchronized void initializeModel(File path) {
		if(model == null) {
			model = new BRJS(path, new NullLogConfigurator());
		}
	}
	
	public static synchronized void initializeModel(BRJS brjs) {
		if(model == null) {
			model = brjs;
		}
	}
	
	public static synchronized void destroy() {
		if(model != null) {
			try {
				model.close();
			}
			finally {
				model = null;
			}
		}
	}
	
	public static BRJS aquireModel() {
		lock.lock();
		return model;
	}
	
	public static void releaseModel() {
		lock.unlock();
	}
}