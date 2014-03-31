package org.bladerunnerjs.plugin.utility;

public class NonExistentPluginException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public NonExistentPluginException(String sourcePluginName, String dependentPluginName) {
		super("The '" + sourcePluginName + "' plug-in has a dependency on the '" + dependentPluginName + "' plug-in, which isn't available.");
	}
}
