package org.bladerunnerjs.plugin;

import static org.junit.Assert.*;
import static org.bladerunnerjs.plugin.utility.PluginLoader.Messages.*;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.utilClasses.AnotherTestInterface;
import org.bladerunnerjs.plugin.utilClasses.ClassImplementingNestedInterface;
import org.bladerunnerjs.plugin.utilClasses.MyTestInterface;
import org.bladerunnerjs.plugin.utilClasses.MyTestInterfaceImplementer;
import org.bladerunnerjs.plugin.utility.PluginLoader;
import org.bladerunnerjs.testing.utility.BRJSTestFactory;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.bladerunnerjs.testing.utility.TestLoggerFactory;
import org.junit.Before;
import org.junit.Test;



public class PluginLoaderTest
{

	private BRJS brjs;
	private LogMessageStore logStore;

	@Before
	public void setup()
	{
		logStore = new LogMessageStore(true);
		brjs = BRJSTestFactory.createBRJS(new File("src/test/resources/BRJSTest"), new TestLoggerFactory(logStore));
	}

	@Test
	public void testInstancesOfInterfaceAreReturned()
	{
		List<MyTestInterface> newObjectList = PluginLoader.createPluginsOfType(brjs, MyTestInterface.class, null);
		assertEquals("List should contain 1 item", 1, newObjectList.size());
		assertEquals("List item should be of type MyTestInterfaceImplementer", MyTestInterfaceImplementer.class, newObjectList.get(0).getClass());
	}
	
	@Test
	public void testErroMessageIsPrintedIfConstructorCantBeFound()
	{
		PluginLoader.createPluginsOfType(brjs, MyTestInterface.class, null);
		logStore.verifyErrorLogMessage(CANNOT_CREATE_INSTANCE_LOG_MSG, "org.bladerunnerjs.plugin.utilClasses.MyTestInterfaceImplementerBadConstructor");
	}
	
	@Test
	public void testInterfacesExtendingAnotherArentLoaded()
	{
		PluginLoader.createPluginsOfType(brjs, AnotherTestInterface.class, null); // make sure we haven't tried to instantiate the sub interface InterfaceExtendingMyTestInterface
		logStore.verifyNoMoreErrorMessages(); // make sure there weren't any errors logged when auto locating plugins
		logStore.verifyNoMoreWarnMessages();
	}
	
	@Test
	public void classesImplementingNestedInterfacesAreCreated()
	{
		PluginLoader.createPluginsOfType(brjs, AnotherTestInterface.class, null);
		List<AnotherTestInterface> newObjectList = PluginLoader.createPluginsOfType(brjs, AnotherTestInterface.class, null);
		assertEquals("List should contain 1 item", 1, newObjectList.size());
		assertEquals("List item should be of type ClassImplementingNestedInterface", ClassImplementingNestedInterface.class, newObjectList.get(0).getClass());
	}
	
}
