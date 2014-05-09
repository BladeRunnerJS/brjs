package org.bladerunnerjs.plugin.plugins.commands.standard;


public class InvalidBundlableNodeException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public InvalidBundlableNodeException(String path ) {
		super("The directory '" + path + "' is not itself a bundlable directory, nor resides within a bundlable directory.");
	}
}
