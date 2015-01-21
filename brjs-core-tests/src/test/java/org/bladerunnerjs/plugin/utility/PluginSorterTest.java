package org.bladerunnerjs.plugin.utility;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Joiner;

public class PluginSorterTest {
	@Test
	public void pluginsWithNoDependenciesKeepTheirOriginalOrder() throws Exception {
		assertEquals("TestPluginB, TestPluginC, TestPluginA", sort(new TestPluginB(), new TestPluginC(), new TestPluginA()));
	}
	
	@Test
	public void pluginsWithDependenciesCauseTheOrderToChange() throws Exception {
		assertEquals("TestPlugin1, TestPlugin2, TestPlugin3", sort(new TestPlugin2(), new TestPlugin3(), new TestPlugin1()));
	}
	
	@Test
	public void pluginsWithInverseDependenciesCorrectlyCauseTheOrderToChange() throws Exception {
		assertEquals("TestPluginBefore1, TestPlugin1, TestPlugin2, TestPlugin3", sort(new TestPlugin1(), new TestPlugin3(), new TestPlugin2(), new TestPluginBefore1()));
	}
	
	@Test
	public void pluginsThatReferToNonExistentPluginsCauseAnException() throws Exception {
		try {
			sort(new TestPluginA(), new TestPlugin2());
			fail("Exception expected");
		}
		catch(NonExistentPluginException e) {
			assertEquals("The 'TestPlugin2' plug-in has a dependency on the 'org.bladerunnerjs.plugin.utility.PluginSorterTest.TestPlugin1' plug-in, which isn't available.", e.getMessage());
		}
	}
	
	@Test
	public void pluginsWithCircularDependenciesCauseAnException() throws Exception {
		try {
			sort(new TestPluginCircularB(), new TestPluginCircularC(), new TestPluginCircularA());
			fail("Exception expected");
		}
		catch(PluginOrderingException e) {
			assertEquals("Circular dependency involving the plug-ins 'TestPluginCircularB', 'TestPluginCircularC'.", e.getMessage());
		}
	}
	
	@Test
	public void circularDependenciesDontOccurIfOneSideIsAnInverseDependency() throws Exception {
		assertEquals("TestPluginX, TestPluginY", sort(new TestPluginY(), new TestPluginX()));
	}
	
	private class TestPluginA extends TestPlugin {}
	private class TestPluginB extends TestPlugin {}
	private class TestPluginC extends TestPlugin {}
	
	private class TestPlugin1 extends TestPlugin {}
	private class TestPlugin2 extends TestPlugin {{ pluginsThatMustAppearBeforeThisPlugin.add("org.bladerunnerjs.plugin.utility.PluginSorterTest.TestPlugin1"); }}
	private class TestPlugin3 extends TestPlugin {{ pluginsThatMustAppearBeforeThisPlugin.add("org.bladerunnerjs.plugin.utility.PluginSorterTest.TestPlugin2"); }}
	private class TestPluginBefore1 extends TestPlugin {{ pluginsThatMustAppearAfterThisPlugin.add("org.bladerunnerjs.plugin.utility.PluginSorterTest.TestPlugin1"); }}
	
	private class TestPluginCircularA extends TestPlugin {}
	private class TestPluginCircularB extends TestPlugin {{ pluginsThatMustAppearBeforeThisPlugin.add("org.bladerunnerjs.plugin.utility.PluginSorterTest.TestPluginCircularC"); }}
	private class TestPluginCircularC extends TestPlugin {{ pluginsThatMustAppearBeforeThisPlugin.add("org.bladerunnerjs.plugin.utility.PluginSorterTest.TestPluginCircularB"); }}
	
	private class TestPluginX extends TestPlugin {{ pluginsThatMustAppearAfterThisPlugin.add("org.bladerunnerjs.plugin.utility.PluginSorterTest.TestPluginY"); }}
	private class TestPluginY extends TestPlugin {{ pluginsThatMustAppearBeforeThisPlugin.add("org.bladerunnerjs.plugin.utility.PluginSorterTest.TestPluginX"); }}
	
	private String sort(TestPlugin... testPlugins) throws PluginOrderingException, NonExistentPluginException {
		return printTestPluginList(PluginSorter.sort(Arrays.asList(testPlugins)));
	}
	
	private String printTestPluginList(List<TestPlugin> plugins) {
		List<String> pluginNames = new ArrayList<>();
		
		for(TestPlugin plugin : plugins) {
			pluginNames.add(plugin.getClass().getSimpleName());
		}
		
		return Joiner.on(", ").join(pluginNames);
	}
}
