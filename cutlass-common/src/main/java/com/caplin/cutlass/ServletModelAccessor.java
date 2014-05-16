package com.caplin.cutlass;

import java.io.File;

import javax.servlet.ServletContext;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;

public class ServletModelAccessor extends org.bladerunnerjs.appserver.BRJSThreadSafeModelAccessor {
	public static BRJS initializeAndGetModel(ServletContext servletContext) throws InvalidSdkDirectoryException {
		initializeModel(servletContext);
		return BRJSAccessor.initialize(model);
	}
	
	public static BRJS initializeAndGetModel(File path) throws InvalidSdkDirectoryException {
		initializeModel(path);
		return BRJSAccessor.initialize(model);
	}
}