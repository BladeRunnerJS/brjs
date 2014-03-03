package com.caplin.cutlass;

import java.io.File;

import javax.servlet.ServletContext;

import org.bladerunnerjs.model.BRJS;

public class ServletModelAccessor extends org.bladerunnerjs.appserver.BRJSThreadSafeModelAccessor {
	public static BRJS initializeAndGetModel(ServletContext servletContext) {
		initializeModel(servletContext);
		return BRJSAccessor.initialize(model);
	}
	
	public static BRJS initializeAndGetModel(File path) {
		initializeModel(path);
		return BRJSAccessor.initialize(model);
	}
}