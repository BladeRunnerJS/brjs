package org.bladerunnerjs.plugin.proxy;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.ModelObserverPlugin;


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
