package org.bladerunnerjs.core.plugin;

import org.bladerunnerjs.model.BRJS;


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
