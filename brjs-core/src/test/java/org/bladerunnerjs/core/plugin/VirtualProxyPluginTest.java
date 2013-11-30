package org.bladerunnerjs.core.plugin;

import static org.junit.Assert.*;

import org.bladerunnerjs.model.BRJS;
import org.junit.Test;

public class VirtualProxyPluginTest {
	@Test
	public void instanceOfWorks() {
		Plugin plugin = new VirtualProxyPlugin(new MyTestPlugin());
		
		assertTrue("1", plugin.instanceOf(MyTestPlugin.class));
		assertTrue("2", plugin.instanceOf(TestPlugin.class));
	}
	
	@Test
	public void equalityWorks() {
		Plugin plugin = new MyTestPlugin();
		Plugin proxyPlugin = new VirtualProxyPlugin(plugin);
		
		assertTrue("1", plugin.equals(plugin));
		assertTrue("2", proxyPlugin.equals(proxyPlugin));
		assertTrue("3", proxyPlugin.equals(plugin));
	}
	
	private interface TestPlugin extends Plugin {
	}
	
	private class MyTestPlugin implements TestPlugin {
		@Override
		public void setBRJS(BRJS brjs) {
			// do nothing
		}
		
		@Override
		public boolean instanceOf(Class<? extends Plugin> otherPluginCLass) {
			throw new RuntimeException("This method should never be called");
		}
		
		@Override
		public Class<?> getPluginClass() {
			return this.getClass();
		}
	}
}
