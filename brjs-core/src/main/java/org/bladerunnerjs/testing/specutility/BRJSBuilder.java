package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.appserver.ServletModelAccessor;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.ModelObserverPlugin;
import org.bladerunnerjs.plugin.PluginLoader;
import org.bladerunnerjs.plugin.VirtualProxyModelObserverPlugin;
import org.bladerunnerjs.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.plugin.bundler.VirtualProxyBundlerPlugin;
import org.bladerunnerjs.plugin.command.CommandPlugin;
import org.bladerunnerjs.plugin.command.VirtualProxyCommandPlugin;
import org.bladerunnerjs.plugin.content.ContentPlugin;
import org.bladerunnerjs.plugin.content.VirtualProxyContentPlugin;
import org.bladerunnerjs.plugin.minifier.MinifierPlugin;
import org.bladerunnerjs.plugin.minifier.VirtualProxyMinifierPlugin;
import org.bladerunnerjs.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.plugin.taghandler.VirtualProxyTagHandlerPlugin;
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
		for(CommandPlugin command : commands)
		{
			specTest.pluginLocator.pluginCommands.add( new VirtualProxyCommandPlugin(command) );
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasModelObservers(ModelObserverPlugin... modelObservers)
	{
		verifyBrjsIsSet();
		for(ModelObserverPlugin modelObserver : modelObservers)
		{
			specTest.pluginLocator.modelObservers.add( new VirtualProxyModelObserverPlugin(modelObserver) );
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasBundlers(BundlerPlugin... bundlerPlugins)
	{
		verifyBrjsIsSet();
		for(BundlerPlugin bundlerPlugin : bundlerPlugins)
		{
			specTest.pluginLocator.bundlers.add( new VirtualProxyBundlerPlugin(bundlerPlugin) );
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
	
	public BuilderChainer hasMinifiers(MinifierPlugin... minifyPlugins)
	{
		verifyBrjsIsSet();
		for(MinifierPlugin minifierPlugin : minifyPlugins)
		{
			specTest.pluginLocator.minifiers.add( new VirtualProxyMinifierPlugin(minifierPlugin) );
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasTagPlugins(TagHandlerPlugin... tagHandlers)
	{
		verifyBrjsIsSet();
		for(TagHandlerPlugin tagHandler : tagHandlers)
		{
			specTest.pluginLocator.tagHandlers.add( new VirtualProxyTagHandlerPlugin(tagHandler) );
		}
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsCommands()
	{
		verifyBrjsIsSet();
		specTest.pluginLocator.bundlers.clear();
		specTest.pluginLocator.pluginCommands.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), CommandPlugin.class, VirtualProxyCommandPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsModelObservers()
	{
		verifyBrjsIsSet();
		specTest.pluginLocator.bundlers.clear();
		specTest.pluginLocator.modelObservers.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), ModelObserverPlugin.class, VirtualProxyModelObserverPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsBundlers()
	{
		verifyBrjsIsSet();
		specTest.pluginLocator.bundlers.clear();
		specTest.pluginLocator.bundlers.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), BundlerPlugin.class, VirtualProxyBundlerPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsMinifiers() 
	{
		verifyBrjsIsSet();
		specTest.pluginLocator.minifiers.clear();
		specTest.pluginLocator.minifiers.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), MinifierPlugin.class, VirtualProxyMinifierPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsContentPlugins() 
	{
		verifyBrjsIsSet();
		specTest.pluginLocator.contentPlugins.clear();
		specTest.pluginLocator.contentPlugins.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), ContentPlugin.class, VirtualProxyContentPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsTagHandlers() 
	{
		verifyBrjsIsSet();
		specTest.pluginLocator.tagHandlers.clear();
		specTest.pluginLocator.tagHandlers.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), TagHandlerPlugin.class, VirtualProxyTagHandlerPlugin.class) );
		
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
		ServletModelAccessor.reset();
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
	
}
