package org.bladerunnerjs.core.plugin;

import org.bladerunnerjs.model.BRJS;


public interface Plugin
{
	public void setBRJS(BRJS brjs);
	// TODO: add an instanceOf() method since VirtualProxy classes will cause the instanceof operator to return the wrong result
//	public <P extends Plugin> boolean instanceOf(Class<P> pluginClass);
}
