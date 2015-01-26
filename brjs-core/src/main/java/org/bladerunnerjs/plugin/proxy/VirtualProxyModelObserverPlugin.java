package org.bladerunnerjs.plugin.proxy;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.plugin.ModelObserverPlugin;


public class VirtualProxyModelObserverPlugin extends VirtualProxyPlugin implements ModelObserverPlugin
{

	public VirtualProxyModelObserverPlugin(ModelObserverPlugin plugin)
	{
		super(plugin);
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		super.setBRJS(brjs);
		initializePlugin();
	}
	
}
