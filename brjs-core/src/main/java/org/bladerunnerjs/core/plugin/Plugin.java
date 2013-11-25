package org.bladerunnerjs.core.plugin;

import org.bladerunnerjs.model.BRJS;


public interface Plugin
{
	public void setBRJS(BRJS brjs);
	
	/**
	 * Used instead of Java's `instanceof` operate as plugins within BRJS are wrapped 
	 * within a <a href="https://en.wikipedia.org/wiki/Lazy_loading#Virtual_proxy">Virtual Proxy</a>, causing the `instanceof` operator to not behave as expected.
	 * <p>
	 * Standard plugins can ignore this method and simply throw a {@link RuntimeException} as this method should never be called on a {@link Plugin}. Abstract classes that implement
	 * this method are provided for each plugin type.
	 */
	boolean instanceOf(Class<? extends Plugin> otherPluginCLass);
}
