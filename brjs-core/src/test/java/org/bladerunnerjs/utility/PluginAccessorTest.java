package org.bladerunnerjs.utility;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.plugin.AssetPlugin;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.MinifierPlugin;
import org.bladerunnerjs.api.plugin.ModelObserverPlugin;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.TagHandlerPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.api.spec.utility.LogMessageStore;
import org.bladerunnerjs.api.spec.utility.MockPluginLocator;
import org.bladerunnerjs.api.spec.utility.TestLoggerFactory;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.plugin.utility.PluginAccessor;
import org.bladerunnerjs.plugin.utility.PluginLocatorUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class PluginAccessorTest
{

	private BRJS brjs;
	private LogMessageStore logStore;
	private File tempBrjsDir;
	private Map<String,List<String>> activePlugins;
	private MockPluginLocator pluginLocator;
	private ContentPlugin contentPlugin1;
	private ContentPlugin contentPlugin2;
	private ContentPlugin contentPlugin3;

	@Before
	public void setup() throws Exception
	{
		tempBrjsDir = FileUtils.createTemporaryDirectory(this.getClass(), "BRJS_ROOT");
		logStore = new LogMessageStore(true);
		brjs = BRJSTestModelFactory.createModel(tempBrjsDir, new TestLoggerFactory(logStore));
		activePlugins = new HashMap<>();
		pluginLocator = new MockPluginLocator();
		contentPlugin1 = new ContentPlugin1();
		contentPlugin2 = new ContentPlugin2();
		contentPlugin3 = new ContentPlugin3();
	}
	
	@After
	public void cleanup() throws Exception {
		org.apache.commons.io.FileUtils.deleteQuietly(tempBrjsDir);
	}

	@Test
	public void ifPluginsInBrjsConfAreEmptyNoPluginsAreLoaded() throws Exception {
		pluginLocator.contentPlugins.add( contentPlugin1 );
		brjs.bladerunnerConf().setActivePlugins(activePlugins);
		
		PluginAccessor pluginAccessor = new PluginAccessor(brjs, pluginLocator);
		assertTrue( pluginAccessor.contentPlugins().isEmpty() );
	}
	
	@Test
	public void wildcardCanBeUsedToLoadAllFoundPlugins() throws Exception {
		activePlugins.put(ContentPlugin.class.getSimpleName(), Arrays.asList("*"));
		pluginLocator.contentPlugins.add( contentPlugin1 );
		pluginLocator.contentPlugins.add( contentPlugin2 );
		brjs.bladerunnerConf().setActivePlugins(activePlugins);
		
		PluginAccessor pluginAccessor = new PluginAccessor(brjs, pluginLocator);
		assertEquals( 2, pluginAccessor.contentPlugins().size() );
	}
	
	@Test
	public void configCanBeUsedToOrderPlugins() throws Exception {
		activePlugins.put(ContentPlugin.class.getSimpleName(), Arrays.asList(
				"ContentPlugin2",
				"ContentPlugin3",
				"ContentPlugin1"
			));
		pluginLocator.contentPlugins.add( contentPlugin1 );
		pluginLocator.contentPlugins.add( contentPlugin2 );
		pluginLocator.contentPlugins.add( contentPlugin3 );
		brjs.bladerunnerConf().setActivePlugins(activePlugins);
		
		PluginAccessor pluginAccessor = new PluginAccessor(brjs, pluginLocator);
		assertEquals( 3, pluginAccessor.contentPlugins().size() );
		assertEquals( contentPlugin2, pluginAccessor.contentPlugins().get(0) );
		assertEquals( contentPlugin3, pluginAccessor.contentPlugins().get(1) );
		assertEquals( contentPlugin1, pluginAccessor.contentPlugins().get(2) );
	}
	
	@Test
	public void fullClassNamesCanAlsoBeUsed() throws Exception {
		activePlugins.put(ContentPlugin.class.getSimpleName(), Arrays.asList(
				"org.bladerunnerjs.utility.PluginAccessorTest$ContentPlugin2",
				"org.bladerunnerjs.utility.PluginAccessorTest$ContentPlugin3",
				"org.bladerunnerjs.utility.PluginAccessorTest$ContentPlugin1"
			));
		pluginLocator.contentPlugins.add( contentPlugin1 );
		pluginLocator.contentPlugins.add( contentPlugin2 );
		pluginLocator.contentPlugins.add( contentPlugin3 );
		brjs.bladerunnerConf().setActivePlugins(activePlugins);
		
		PluginAccessor pluginAccessor = new PluginAccessor(brjs, pluginLocator);
		assertEquals( 3, pluginAccessor.contentPlugins().size() );
		assertEquals( contentPlugin2, pluginAccessor.contentPlugins().get(0) );
		assertEquals( contentPlugin3, pluginAccessor.contentPlugins().get(1) );
		assertEquals( contentPlugin1, pluginAccessor.contentPlugins().get(2) );
	}
	
	@Test
	public void pluginsCanBeDisabledByNotIncludingThemInTheList() throws Exception {
		activePlugins.put(ContentPlugin.class.getSimpleName(), Arrays.asList(
				"ContentPlugin2",
				"ContentPlugin3"
				));
		pluginLocator.contentPlugins.add( contentPlugin1 );
		pluginLocator.contentPlugins.add( contentPlugin2 );
		pluginLocator.contentPlugins.add( contentPlugin3 );
		brjs.bladerunnerConf().setActivePlugins(activePlugins);
		
		PluginAccessor pluginAccessor = new PluginAccessor(brjs, pluginLocator);
		assertEquals( 2, pluginAccessor.contentPlugins().size() );
		assertEquals( contentPlugin2, pluginAccessor.contentPlugins().get(0) );
		assertEquals( contentPlugin3, pluginAccessor.contentPlugins().get(1) );
	}
	
	// TODO: long name can be used too
	
	@Test
	public void allPluginTypesUseTheFilteringMechanism() throws Exception {
		activePlugins.put( ContentPlugin.class.getSimpleName(), Arrays.asList() );
		pluginLocator.contentPlugins.add( Mockito.mock(ContentPlugin.class) );
		
		activePlugins.put( TagHandlerPlugin.class.getSimpleName(), Arrays.asList() );
		pluginLocator.tagHandlers.add( Mockito.mock(TagHandlerPlugin.class) );
		
		activePlugins.put( MinifierPlugin.class.getSimpleName(), Arrays.asList() );
		pluginLocator.minifiers.add( Mockito.mock(MinifierPlugin.class) );
		
		activePlugins.put( ModelObserverPlugin.class.getSimpleName(), Arrays.asList() );
		pluginLocator.modelObservers.add( Mockito.mock(ModelObserverPlugin.class) );
		
		activePlugins.put( RequirePlugin.class.getSimpleName(), Arrays.asList() );
		pluginLocator.requirePlugins.add( Mockito.mock(RequirePlugin.class) );
		
		activePlugins.put( AssetPlugin.class.getSimpleName(), Arrays.asList() );
		pluginLocator.assetPlugins.add( Mockito.mock(AssetPlugin.class) );
		
		brjs.bladerunnerConf().setActivePlugins(activePlugins);
		
		PluginAccessor pluginAccessor = new PluginAccessor(brjs, pluginLocator);
		assertEquals( 0, pluginAccessor.contentPlugins().size() );
		assertEquals( 0, pluginAccessor.tagHandlerPlugins().size() );
		assertEquals( 0, pluginAccessor.minifierPlugins().size() );
		assertEquals( 0, pluginAccessor.modelObserverPlugins().size() );
		assertEquals( 0, pluginAccessor.requirePlugins().size() );
		assertEquals( 0, pluginAccessor.assetPlugins().size() );
	}
	
	@Test
	public void warningIsLoggedIfNoPluginMatcheActivePluginConfig() throws Exception {
		activePlugins.put(ContentPlugin.class.getSimpleName(), Arrays.asList("foo"));
		pluginLocator.contentPlugins.add( contentPlugin1 );
		brjs.bladerunnerConf().setActivePlugins(activePlugins);
		
		new PluginAccessor(brjs, pluginLocator);
		logStore.verifyWarnLogMessage(PluginLocatorUtils.Messages.NO_MATCHING_PLUGIN, ContentPlugin.class.getSimpleName(), "foo");
	}
	
	@Test
	public void debugLogsListWildcardEnabledPlugins() throws Exception {
		activePlugins.put(ContentPlugin.class.getSimpleName(), Arrays.asList("*"));
		pluginLocator.contentPlugins.add( contentPlugin1 );
		brjs.bladerunnerConf().setActivePlugins(activePlugins);
		
		new PluginAccessor(brjs, pluginLocator);
		logStore.verifyDebugLogMessage(PluginLocatorUtils.Messages.PLUGIN_ENABLED_MESSAGE, contentPlugin1.getClass().getName(), ContentPlugin.class.getSimpleName(), "*");
	}
	
	@Test
	public void debugLogsListEnabledPlugins() throws Exception {
		activePlugins.put(ContentPlugin.class.getSimpleName(), Arrays.asList(contentPlugin1.getClass().getSimpleName()));
		pluginLocator.contentPlugins.add( contentPlugin1 );
		brjs.bladerunnerConf().setActivePlugins(activePlugins);
		
		new PluginAccessor(brjs, pluginLocator);
		logStore.verifyDebugLogMessage(PluginLocatorUtils.Messages.PLUGIN_ENABLED_MESSAGE, contentPlugin1.getClass().getName(), ContentPlugin.class.getSimpleName(), contentPlugin1.getClass().getSimpleName());
	}
	
	@Test
	public void debugLogsListWildcardDisabledPlugins() throws Exception {
		activePlugins.put(ContentPlugin.class.getSimpleName(), Arrays.asList());
		pluginLocator.contentPlugins.add( contentPlugin1 );
		brjs.bladerunnerConf().setActivePlugins(activePlugins);
		
		new PluginAccessor(brjs, pluginLocator);
		logStore.verifyDebugLogMessage(PluginLocatorUtils.Messages.PLUGIN_DISABLED_EMPTY_ACTIVE_PLUGINS_MESSAGE, contentPlugin1.getClass().getName(), ContentPlugin.class.getSimpleName());
	}
	
	@Test
	public void debugLogsListDisabledPlugins() throws Exception {
		activePlugins.put( ContentPlugin.class.getSimpleName(), Arrays.asList(contentPlugin1.getClass().getSimpleName(), contentPlugin2.getClass().getSimpleName()) );
		pluginLocator.contentPlugins.add( contentPlugin1 );
		pluginLocator.contentPlugins.add( contentPlugin2 );
		pluginLocator.contentPlugins.add( contentPlugin3 );
		brjs.bladerunnerConf().setActivePlugins(activePlugins);
		
		new PluginAccessor(brjs, pluginLocator);
		logStore.verifyDebugLogMessage(PluginLocatorUtils.Messages.PLUGIN_DISABLED_MESSAGE, contentPlugin3.getClass().getName(), ContentPlugin.class.getSimpleName(), contentPlugin1.getClass().getSimpleName()+", "+contentPlugin2.getClass().getSimpleName());
	}
	
	
	class EmptyAbstractContentPlugin extends AbstractContentPlugin {
		public String getRequestPrefix() { return null;	}
		public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ContentProcessingException { return null; }
		public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException { return null; }
		public void setBRJS(BRJS brjs) { }
	}
	class ContentPlugin1 extends EmptyAbstractContentPlugin {}
	class ContentPlugin2 extends EmptyAbstractContentPlugin {}
	class ContentPlugin3 extends EmptyAbstractContentPlugin {}
	
}
