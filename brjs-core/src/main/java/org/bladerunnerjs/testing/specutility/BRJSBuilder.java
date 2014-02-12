package org.bladerunnerjs.testing.specutility;

import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.appserver.ServletModelAccessor;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.AssetLocationPlugin;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.MinifierPlugin;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.Plugin;
import org.bladerunnerjs.plugin.TagHandlerPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyAssetLocationPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyAssetPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyCommandPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyContentPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyMinifierPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyModelObserverPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyTagHandlerPlugin;
import org.bladerunnerjs.plugin.utility.PluginLoader;
import org.bladerunnerjs.testing.specutility.engine.BuilderChainer;
import org.bladerunnerjs.testing.specutility.engine.NodeBuilder;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.mockito.Mockito;


public class BRJSBuilder extends NodeBuilder<BRJS> {
	private BRJS brjs;
	
	public BRJSBuilder(SpecTest modelTest, BRJS brjs) {
		super(modelTest, brjs);
		this.brjs = brjs;
	}
	
	//TODO: look at brjs is null - commands must be added before BRJS is created
	
	public BuilderChainer hasBeenPopulated() throws Exception {
		brjs.populate();
		
		return builderChainer;
	}

	public BuilderChainer hasCommands(CommandPlugin... commands)
	{
		verifyBrjsIsSet();
		verifyPluginsUnitialized(specTest.pluginLocator.pluginCommands);
		
		for(CommandPlugin command : commands)
		{
			specTest.pluginLocator.pluginCommands.add( new VirtualProxyCommandPlugin(command) );
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasModelObservers(ModelObserverPlugin... modelObservers)
	{
		verifyBrjsIsSet();
		verifyPluginsUnitialized(specTest.pluginLocator.modelObservers);
		
		for(ModelObserverPlugin modelObserver : modelObservers)
		{
			specTest.pluginLocator.modelObservers.add( new VirtualProxyModelObserverPlugin(modelObserver) );
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasContentPlugins(ContentPlugin... contentPlugins)
	{
		verifyBrjsIsSet();
		
		for(ContentPlugin contentPlugin : contentPlugins)
		{
			specTest.pluginLocator.contentPlugins.add( new VirtualProxyContentPlugin(contentPlugin) );
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasConfigurationFileWithContent(String filename, String content) throws Exception 
	{
		FileUtils.write(brjs.configurations().file(filename), content);	
		return builderChainer;
	}
	
	public BuilderChainer hasAssetPlugins(AssetPlugin... assetPlugins)
	{
		verifyBrjsIsSet();
		
		for(AssetPlugin assetPlugin : assetPlugins)
		{
			specTest.pluginLocator.assetPlugins.add( new VirtualProxyAssetPlugin(assetPlugin) );
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasMinifiers(MinifierPlugin... minifyPlugins)
	{
		verifyBrjsIsSet();
		verifyPluginsUnitialized(specTest.pluginLocator.minifiers);
		
		for(MinifierPlugin minifierPlugin : minifyPlugins)
		{
			specTest.pluginLocator.minifiers.add( new VirtualProxyMinifierPlugin(minifierPlugin) );
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasTagPlugins(TagHandlerPlugin... tagHandlers)
	{
		verifyBrjsIsSet();
		verifyPluginsUnitialized(specTest.pluginLocator.tagHandlers);
		
		for(TagHandlerPlugin tagHandler : tagHandlers)
		{
			specTest.pluginLocator.tagHandlers.add( new VirtualProxyTagHandlerPlugin(tagHandler) );
		}
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsCommands()
	{
		verifyBrjsIsSet();
		verifyPluginsUnitialized(specTest.pluginLocator.pluginCommands);
		
		specTest.pluginLocator.pluginCommands.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), CommandPlugin.class, VirtualProxyCommandPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsModelObservers()
	{
		verifyBrjsIsSet();
		verifyPluginsUnitialized(specTest.pluginLocator.modelObservers);
		
		specTest.pluginLocator.modelObservers.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), ModelObserverPlugin.class, VirtualProxyModelObserverPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsContentPlugins() 
	{
		verifyBrjsIsSet();
		verifyPluginsUnitialized(specTest.pluginLocator.contentPlugins);
		
		specTest.pluginLocator.contentPlugins.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), ContentPlugin.class, VirtualProxyContentPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsTagHandlers() 
	{
		verifyBrjsIsSet();
		verifyPluginsUnitialized(specTest.pluginLocator.tagHandlers);
		
		specTest.pluginLocator.tagHandlers.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), TagHandlerPlugin.class, VirtualProxyTagHandlerPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsAssetProducers() {
		verifyBrjsIsSet();
		verifyPluginsUnitialized(specTest.pluginLocator.assetPlugins);
		
		specTest.pluginLocator.assetPlugins.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), AssetPlugin.class, VirtualProxyAssetPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsAssetLocationProducers() {
		verifyBrjsIsSet();
		verifyPluginsUnitialized(specTest.pluginLocator.assetLocationPlugins);
		
		specTest.pluginLocator.assetLocationPlugins.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), AssetLocationPlugin.class, VirtualProxyAssetLocationPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsBundlers()
	{
		automaticallyFindsContentPlugins();
		automaticallyFindsTagHandlers();
		automaticallyFindsAssetProducers();
		automaticallyFindsAssetLocationProducers();
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsMinifiers() 
	{
		verifyBrjsIsSet();
		verifyPluginsUnitialized(specTest.pluginLocator.minifiers);
		
		specTest.pluginLocator.minifiers.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), MinifierPlugin.class, VirtualProxyMinifierPlugin.class) );
		
		return builderChainer;
	}
	
	@Override
	public BuilderChainer hasBeenCreated() throws Exception
	{
		brjs = specTest.createModel();
		specTest.brjs = brjs;
		this.node = brjs;
		
		super.hasBeenCreated();
		
		return builderChainer;
	}
	
	public BuilderChainer hasBeenAuthenticallyCreated() throws Exception
	{
		brjs = specTest.createNonTestModel();
		specTest.brjs = brjs;
		this.node = brjs;
		
		return builderChainer;
	}

	public BuilderChainer usedForServletModel()
	{
		ServletModelAccessor.destroy();
		ServletModelAccessor.initializeModel(brjs);
		return builderChainer;
	}
	
	private void verifyBrjsIsSet()
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
}
