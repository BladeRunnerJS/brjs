package org.bladerunnerjs.model;

import java.io.File;
import java.util.concurrent.locks.ReentrantLock;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.logging.SLF4JLoggerFactory;
import org.bladerunnerjs.plugin.utility.BRJSPluginLocator;

/**
 * A utility class so Servlets and Filters can share a single BRJS model instance.
 * 
 * WARNING: Do not use this class. Any plugins that should have a reference to the BRJS instance will be provided it in the setBRJS() method. 
 *
 */
// Note: this should be the only static state within 'brjs-core'
public class ThreadSafeStaticBRJSAccessor {
	
	private static BRJS model;
	private static final ReentrantLock lock = new ReentrantLock();
	
	//TODO: remove this once we've removed all legacy code
	public static BRJS root;
	
	public static synchronized BRJS initializeModel(File brjsDir) throws InvalidSdkDirectoryException {
		if (model == null) {
			model = new BRJS(brjsDir, new BRJSPluginLocator(), new SLF4JLoggerFactory(), new TimestampAppVersionGenerator());
			root = model;
		}
		
		return model;
	}
	
	//TODO: this should be removed once all legacy code is removed
	public static synchronized BRJS initializeModel(BRJS brjs) throws InvalidSdkDirectoryException {
		if (model == null) {
			model = brjs;
			root = model;
		}
		return model;
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