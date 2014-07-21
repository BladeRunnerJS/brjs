package org.bladerunnerjs.model.exception.test;

import java.io.IOException;

import com.google.common.base.Joiner;

public class BrowserStartupException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public BrowserStartupException(IOException e, String[] args, String confPath) {
		super("Error while starting the browser using the arguments `" + Joiner.on(" ").join(args) + "`.\nPlease check your test config inside '" + confPath + "'", e);
	}
}
