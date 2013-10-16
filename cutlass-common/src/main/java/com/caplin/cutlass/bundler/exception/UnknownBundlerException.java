package com.caplin.cutlass.bundler.exception;

public class UnknownBundlerException extends RuntimeException {

	private static final long serialVersionUID = -8855303440924373407L;
	
	public UnknownBundlerException(Exception e) {
		super(e);
	}

}
