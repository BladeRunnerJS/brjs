package org.bladerunnerjs.api.spec.utility;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.FileModificationWatcherThread;
import org.bladerunnerjs.api.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.plugin.AssetLocationPlugin;
import org.bladerunnerjs.api.plugin.AssetPlugin;
import org.bladerunnerjs.api.plugin.CommandPlugin;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.MinifierPlugin;
import org.bladerunnerjs.api.plugin.ModelObserverPlugin;
import org.bladerunnerjs.api.plugin.Plugin;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.TagHandlerPlugin;
import org.bladerunnerjs.api.spec.engine.BuilderChainer;
import org.bladerunnerjs.api.spec.engine.NodeBuilder;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.spec.logging.MockLogLevelAccessor;
import org.bladerunnerjs.memoization.WatchKeyServiceFactory;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;
import org.bladerunnerjs.plugin.proxy.VirtualProxyAssetLocationPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyAssetPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyCommandPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyContentPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyMinifierPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyModelObserverPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyRequirePlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyTagHandlerPlugin;
import org.bladerunnerjs.plugin.utility.PluginLoader;
import org.bladerunnerjs.utility.FileUtils;
import org.mockito.Mockito;


public class BRJSBuilder extends NodeBuilder<BRJS> {
	private BRJS brjs;
	
	public BRJSBuilder(SpecTest modelTest, BRJS brjs) {
		super(modelTest, brjs);
		this.brjs = brjs;
	}
	
	//TODO: look at brjs is null - commands must be added before BRJS is created
	
	public BuilderChainer hasBeenPopulated() throws Exception {
		brjs.populate("default");
		
		return builderChainer;
	}

	public BuilderChainer hasCommandPlugins(CommandPlugin... commands)
	{
		verifyBrjsIsNotSet();
		
		for(CommandPlugin command : commands)
		{
			specTest.pluginLocator.pluginCommands.add( new VirtualProxyCommandPlugin(command) );
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasModelObserverPlugins(ModelObserverPlugin... modelObservers)
	{
		verifyBrjsIsNotSet();
		
		for(ModelObserverPlugin modelObserver : modelObservers)
		{
			specTest.pluginLocator.modelObservers.add( new VirtualProxyModelObserverPlugin(modelObserver) );
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasContentPlugins(ContentPlugin... contentPlugins)
	{
		verifyBrjsIsNotSet();
		
		for(ContentPlugin contentPlugin : contentPlugins)
		{
			specTest.pluginLocator.contentPlugins.add( new VirtualProxyContentPlugin(contentPlugin) );
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasConfigurationFileWithContent(String filename, String content) throws Exception 
	{
		FileUtils.write(brjs.conf().file(filename), content);	
		return builderChainer;
	}
	
	public BuilderChainer hasAssetPlugins(AssetPlugin... assetPlugins)
	{
		verifyBrjsIsNotSet();
		
		for(AssetPlugin assetPlugin : assetPlugins)
		{
			specTest.pluginLocator.assetPlugins.add( new VirtualProxyAssetPlugin(assetPlugin) );
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasMinifierPlugins(MinifierPlugin... minifyPlugins)
	{
		verifyBrjsIsNotSet();
		
		for(MinifierPlugin minifierPlugin : minifyPlugins)
		{
			specTest.pluginLocator.minifiers.add( new VirtualProxyMinifierPlugin(minifierPlugin) );
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasTagHandlerPlugins(TagHandlerPlugin... tagHandlers)
	{
		verifyBrjsIsNotSet();
		
		for(TagHandlerPlugin tagHandler : tagHandlers)
		{
			specTest.pluginLocator.tagHandlers.add( new VirtualProxyTagHandlerPlugin(tagHandler) );
		}
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsCommandPlugins()
	{
		verifyBrjsIsNotSet();
		verifyPluginsUnitialized(specTest.pluginLocator.pluginCommands);
		
		specTest.pluginLocator.pluginCommands.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), CommandPlugin.class, VirtualProxyCommandPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsModelObservers()
	{
		verifyBrjsIsNotSet();
		verifyPluginsUnitialized(specTest.pluginLocator.modelObservers);
		
		specTest.pluginLocator.modelObservers.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), ModelObserverPlugin.class, VirtualProxyModelObserverPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsContentPlugins() 
	{
		verifyBrjsIsNotSet();
		verifyPluginsUnitialized(specTest.pluginLocator.contentPlugins);
		
		specTest.pluginLocator.contentPlugins.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), ContentPlugin.class, VirtualProxyContentPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsTagHandlerPlugins() 
	{
		verifyBrjsIsNotSet();
		verifyPluginsUnitialized(specTest.pluginLocator.tagHandlers);
		
		specTest.pluginLocator.tagHandlers.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), TagHandlerPlugin.class, VirtualProxyTagHandlerPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsAssetPlugins() {
		verifyBrjsIsNotSet();
		verifyPluginsUnitialized(specTest.pluginLocator.assetPlugins);
		
		specTest.pluginLocator.assetPlugins.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), AssetPlugin.class, VirtualProxyAssetPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsAssetLocationPlugins() {
		verifyBrjsIsNotSet();
		verifyPluginsUnitialized(specTest.pluginLocator.assetLocationPlugins);
		
		specTest.pluginLocator.assetLocationPlugins.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), AssetLocationPlugin.class, VirtualProxyAssetLocationPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsBundlerPlugins()
	{
		automaticallyFindsContentPlugins();
		automaticallyFindsTagHandlerPlugins();
		automaticallyFindsAssetPlugins();
		automaticallyFindsAssetLocationPlugins();
		automaticallyFindsRequirePlugins();
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsMinifierPlugins() 
	{
		verifyBrjsIsNotSet();
		verifyPluginsUnitialized(specTest.pluginLocator.minifiers);
		
		specTest.pluginLocator.minifiers.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), MinifierPlugin.class, VirtualProxyMinifierPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsRequirePlugins() 
	{
		verifyBrjsIsNotSet();
		verifyPluginsUnitialized(specTest.pluginLocator.requirePlugins);
		
		specTest.pluginLocator.requirePlugins.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), RequirePlugin.class, VirtualProxyRequirePlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsAllPlugins() {
		automaticallyFindsContentPlugins();
		automaticallyFindsTagHandlerPlugins();
		automaticallyFindsAssetPlugins();
		automaticallyFindsAssetLocationPlugins();
		automaticallyFindsCommandPlugins();
		automaticallyFindsModelObservers();
		automaticallyFindsRequirePlugins();
		
		return builderChainer;
	}
	
	public BuilderChainer hasNotYetBeenCreated() throws Exception
	{
		if (brjs != null) {
			brjs.close();
		}
		brjs = null;
		specTest.brjs = null;
		specTest.resetTestObjects();
		return builderChainer;
	}
	
	@Override
	public BuilderChainer hasBeenCreated() throws Exception
	{
		brjs = specTest.createModel();
		brjs.io().installFileAccessChecker();
		specTest.brjs = brjs;
		this.node = brjs;
		
		super.hasBeenCreated();
		
		return builderChainer;
	}
	
	public BuilderChainer hasBeenAuthenticallyCreated() throws Exception
	{
		brjs = specTest.createNonTestModel();
		brjs.io().installFileAccessChecker();
		specTest.brjs = brjs;
		this.node = brjs;
		
		return builderChainer;
	}
	
	public BuilderChainer hasBeenAuthenticallyCreatedWithFileWatcherThread() throws Exception
	{
		hasBeenAuthenticallyCreated();
		specTest.fileWatcherThread = new FileModificationWatcherThread(brjs, new WatchKeyServiceFactory());
		specTest.fileWatcherThread.start();
		
		return builderChainer;
	}
	
	public BuilderChainer hasBeenAuthenticallyReCreated() throws Exception
	{
		if (brjs != null) {
			brjs.close();
		}
		return hasBeenAuthenticallyCreated();
	}

	public BuilderChainer usedForServletModel() throws InvalidSdkDirectoryException
	{
		ThreadSafeStaticBRJSAccessor.destroy();
		ThreadSafeStaticBRJSAccessor.initializeModel(brjs);
		return builderChainer;
	}
	
	private void verifyBrjsIsSet()
	{
		if (specTest.brjs == null)
		{
			throw new RuntimeException("BRJS must exist before this command can be used.");
		}
	}
	
	private void verifyBrjsIsNotSet()
	{
		if (specTest.brjs != null)
		{
			throw new RuntimeException("Plugins must be added to BRJS before it is created.");
		}
	}
	
	private <P extends Plugin> void verifyPluginsUnitialized(List<P> pluginList) {
		if(pluginList.size() > 0) {
			throw new RuntimeException("automaticallyFindsXXX() invoked after plug-ins have already been added.");
		}
	}
	
	public BuilderChainer commandHasBeenRun(String... args) throws Exception {
		brjs.runCommand(args);
		
		brjs.incrementFileVersion();
		
		return builderChainer;
	}
	
	public BuilderChainer userCommandHasBeenRun(String... args) throws Exception {
		brjs.runUserCommand(new MockLogLevelAccessor(), args);
		
		return builderChainer;
	}
	
	public BuilderChainer usesProductionTemplates() throws IOException {
		verifyBrjsIsSet();
		
		return usesProductionTemplates( locateBrjsSdk() );
	}
	
	public BuilderChainer usesProductionTemplates(File brjsSdkDir) throws IOException {
		verifyBrjsIsSet();
		
		File templateDir = new File(brjsSdkDir, "sdk/templates");
		FileUtils.copyDirectory(brjs, templateDir, brjs.sdkTemplateGroup("default").dir().getParentFile());
		
		File j2eeify = new File(brjsSdkDir, "sdk/j2eeify-app"); 
		FileUtils.copyDirectory(brjs, j2eeify, brjs.file("sdk/j2eeify-app"));
		
		return builderChainer;
	}

	public BuilderChainer usesJsDocResources() throws IOException {
		verifyBrjsIsSet();
		
		return usesJsDocResources( locateBrjsSdk() );
	}
	
	public BuilderChainer usesJsDocResources(File brjsSdkDir) throws IOException {
		verifyBrjsIsSet();
		
		File jsdocResourcesDir = new File(brjsSdkDir, "sdk/jsdoc-toolkit-resources");
		File jsdocResourcesDest = brjs.sdkRoot().file("jsdoc-toolkit-resources");
		
		FileUtils.copyDirectory(brjs, jsdocResourcesDir, jsdocResourcesDest);
		new File(jsdocResourcesDest, "jsdoc-toolkit/jsdoc").setExecutable(true);
		
		return builderChainer;
	}
	
	public BuilderChainer hasProdVersion(String version)
	{
		specTest.appVersionGenerator.setProdVersion(version);
		
		return builderChainer;
	}
	
	public BuilderChainer hasDevVersion(String version)
	{
		specTest.appVersionGenerator.setDevVersion(version);
		
		return builderChainer;
	}

	public BuilderChainer localeForwarderHasContents(String string) throws IOException, InvalidNameException, ModelUpdateException
	{
		SdkJsLib localeForwarderLib = brjs.sdkLib("br-locale-utility");
		FileUtils.write(localeForwarderLib.file("LocaleUtility.js"), string);
		
		return builderChainer;
	}

	public BuilderChainer appsHaveBeeniterated() {
		brjs.apps();
		
		return builderChainer;
	}

	public void hasBeenInactiveForOneMillisecond() {
		long currentTime = (new Date()).getTime();
		
		try {
			do {
				Thread.sleep(1);
			} while(currentTime == (new Date()).getTime());
		}
		catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	private File locateBrjsSdk()
	{
		File thisDir = new File(".").getAbsoluteFile();
		File brjsSdk;
		
		do {
			brjsSdk = new File(thisDir, "brjs-sdk");
			thisDir = thisDir.getParentFile();
		} while (!brjsSdk.isDirectory() && thisDir != null);
		
		if (!brjsSdk.exists() || brjsSdk == null) {
			throw new RuntimeException("Unable to find parent brjs-sdk directory");
		}
		return brjsSdk;
	}
	
}
