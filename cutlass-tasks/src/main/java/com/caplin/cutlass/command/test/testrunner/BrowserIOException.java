package com.caplin.cutlass.command.test.testrunner;

import java.io.IOException;

import com.google.common.base.Joiner;

public class BrowserIOException extends Exception {
	private static final long serialVersionUID = 1L;
	private final String message;
	
	public BrowserIOException(String[] args, String confPath, IOException e) {
		super(e);
		message = "Error while running command defined within '" + confPath + "': " + Joiner.on(" ").join(args);
	}
	
	@Override
	public String getMessage() {
		return message;
	}
}
