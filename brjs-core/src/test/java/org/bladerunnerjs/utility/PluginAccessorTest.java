package org.bladerunnerjs.utility;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.plugin.AssetRegistry;
import org.bladerunnerjs.api.plugin.AssetPlugin;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.api.spec.utility.LogMessageStore;
import org.bladerunnerjs.api.spec.utility.MockPluginLocator;
import org.bladerunnerjs.api.spec.utility.TestLoggerFactory;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BRJSTestModelFactory;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.plugin.utility.PluginAccessor;
import org.bladerunnerjs.plugin.utility.PluginLocatorUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


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
	private ContentPlugin contentPlugin4;
	private AssetPlugin assetPlugin1;
	private AssetPlugin assetPlugin2;

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
		contentPlugin4 = new ContentPlugin4();
		assetPlugin1 = new AssetPlugin1();
		assetPlugin2 = new AssetPlugin2();
	}
	
	@After
	public void cleanup() throws Exception {
		org.apache.commons.io.FileUtils.deleteQuietly(tempBrjsDir);
	}

	@Test
	public void ifPluginsInBrjsConfAreEmptyAllPluginsAreLoaded() throws Exception {
		pluginLocator.contentPlugins.add( contentPlugin1 );
		brjs.bladerunnerConf().setOrderedPlugins(activePlugins);
		
		PluginAccessor pluginAccessor = new PluginAccessor(brjs, pluginLocator);
		assertEquals( 1, pluginAccessor.contentPlugins().size() );
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
		brjs.bladerunnerConf().setOrderedPlugins(activePlugins);
		
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
		brjs.bladerunnerConf().setOrderedPlugins(activePlugins);
		
		PluginAccessor pluginAccessor = new PluginAccessor(brjs, pluginLocator);
		assertEquals( 3, pluginAccessor.contentPlugins().size() );
		assertEquals( contentPlugin2, pluginAccessor.contentPlugins().get(0) );
		assertEquals( contentPlugin3, pluginAccessor.contentPlugins().get(1) );
		assertEquals( contentPlugin1, pluginAccessor.contentPlugins().get(2) );
	}
	
	@Test
	public void pluginsMissedFromTheConfigAreLoadedLast() throws Exception {
		activePlugins.put(ContentPlugin.class.getSimpleName(), Arrays.asList(
				"ContentPlugin2",
				"ContentPlugin3"
				));
		pluginLocator.contentPlugins.add( contentPlugin1 );
		pluginLocator.contentPlugins.add( contentPlugin2 );
		pluginLocator.contentPlugins.add( contentPlugin3 );
		brjs.bladerunnerConf().setOrderedPlugins(activePlugins);
		
		PluginAccessor pluginAccessor = new PluginAccessor(brjs, pluginLocator);
		assertEquals( 3, pluginAccessor.contentPlugins().size() );
		assertEquals( contentPlugin2, pluginAccessor.contentPlugins().get(0) );
		assertEquals( contentPlugin3, pluginAccessor.contentPlugins().get(1) );
		assertEquals( contentPlugin1, pluginAccessor.contentPlugins().get(2) );
	}
	
	@Test
	public void allOrderedPluginTypesUseTheFilteringMechanism() throws Exception {
		activePlugins.put(ContentPlugin.class.getSimpleName(), Arrays.asList(
				contentPlugin2.getClass().getSimpleName(),
				contentPlugin1.getClass().getSimpleName()
		));
		activePlugins.put(AssetPlugin.class.getSimpleName(), Arrays.asList(
			assetPlugin2.getClass().getSimpleName(),
			assetPlugin1.getClass().getSimpleName()
		));
		
		pluginLocator.contentPlugins.add( contentPlugin1 );
		pluginLocator.contentPlugins.add( contentPlugin2 );
		pluginLocator.assetPlugins.add( assetPlugin1 );
		pluginLocator.assetPlugins.add( assetPlugin2 );
		
		brjs.bladerunnerConf().setOrderedPlugins(activePlugins);
				
		PluginAccessor pluginAccessor = new PluginAccessor(brjs, pluginLocator);
		assertEquals( contentPlugin2, pluginAccessor.contentPlugins().get(0) );
		assertEquals( contentPlugin1, pluginAccessor.contentPlugins().get(1) );
		assertEquals( assetPlugin2, pluginAccessor.assetPlugins().get(0) );
		assertEquals( assetPlugin1, pluginAccessor.assetPlugins().get(1) );
	}
	
	@Test
	public void warningIsLoggedIfNoPluginMatcheActivePluginConfig() throws Exception {
		activePlugins.put(ContentPlugin.class.getSimpleName(), Arrays.asList("foo"));
		pluginLocator.contentPlugins.add( contentPlugin1 );
		brjs.bladerunnerConf().setOrderedPlugins(activePlugins);
		
		new PluginAccessor(brjs, pluginLocator);
		logStore.verifyWarnLogMessage(PluginLocatorUtils.Messages.NO_MATCHING_PLUGIN, ContentPlugin.class.getSimpleName(), "foo");
	}
	
	@Test
	public void wildcardCanBeUsedLastToMatchAllOtherPlugins() throws Exception {
		activePlugins.put(ContentPlugin.class.getSimpleName(), Arrays.asList(
				"ContentPlugin2",
				"*"
			));
		pluginLocator.contentPlugins.add( contentPlugin1 );
		pluginLocator.contentPlugins.add( contentPlugin2 );
		pluginLocator.contentPlugins.add( contentPlugin3 );
		brjs.bladerunnerConf().setOrderedPlugins(activePlugins);
		
		PluginAccessor pluginAccessor = new PluginAccessor(brjs, pluginLocator);
		assertEquals( 3, pluginAccessor.contentPlugins().size() );
		assertEquals( contentPlugin2, pluginAccessor.contentPlugins().get(0) );
		assertEquals( contentPlugin1, pluginAccessor.contentPlugins().get(1) );
		assertEquals( contentPlugin3, pluginAccessor.contentPlugins().get(2) );
	}
	
	@Test
	public void wildcardCanBeUsedFirstToMatchAllPluginsNotIncludedAfter() throws Exception {
		activePlugins.put(ContentPlugin.class.getSimpleName(), Arrays.asList(
				"*",
				"ContentPlugin2"
			));
		pluginLocator.contentPlugins.add( contentPlugin1 );
		pluginLocator.contentPlugins.add( contentPlugin2 );
		pluginLocator.contentPlugins.add( contentPlugin3 );
		brjs.bladerunnerConf().setOrderedPlugins(activePlugins);
		
		PluginAccessor pluginAccessor = new PluginAccessor(brjs, pluginLocator);
		assertEquals( 3, pluginAccessor.contentPlugins().size() );
		assertEquals( contentPlugin1, pluginAccessor.contentPlugins().get(0) );
		assertEquals( contentPlugin3, pluginAccessor.contentPlugins().get(1) );
		assertEquals( contentPlugin2, pluginAccessor.contentPlugins().get(2) );
	}
	
	@Test
	public void wildcardCanBeUsedInTheMiddleOfTheListToMatchAllOtherPlugins() throws Exception {
		activePlugins.put(ContentPlugin.class.getSimpleName(), Arrays.asList(
				"ContentPlugin2",
				"*",
				"ContentPlugin1"
			));
		pluginLocator.contentPlugins.add( contentPlugin1 );
		pluginLocator.contentPlugins.add( contentPlugin2 );
		pluginLocator.contentPlugins.add( contentPlugin3 );
		pluginLocator.contentPlugins.add( contentPlugin4 );
		brjs.bladerunnerConf().setOrderedPlugins(activePlugins);
		
		PluginAccessor pluginAccessor = new PluginAccessor(brjs, pluginLocator);
		assertEquals( 4, pluginAccessor.contentPlugins().size() );
		assertEquals( contentPlugin2, pluginAccessor.contentPlugins().get(0) );
		assertEquals( contentPlugin3, pluginAccessor.contentPlugins().get(1) );
		assertEquals( contentPlugin4, pluginAccessor.contentPlugins().get(2) );
		assertEquals( contentPlugin1, pluginAccessor.contentPlugins().get(3) );
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
	class ContentPlugin4 extends EmptyAbstractContentPlugin {}
	
	class EmptyAbstractAssetPlugin extends AbstractAssetPlugin {
		public List<Asset> discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetRegistry assetDiscoveryInitiator) { return null; }
		public void setBRJS(BRJS brjs) { }
	}
	class AssetPlugin1 extends EmptyAbstractAssetPlugin {}
	class AssetPlugin2 extends EmptyAbstractAssetPlugin {}
	class AssetPlugin3 extends EmptyAbstractAssetPlugin {}
	
}
