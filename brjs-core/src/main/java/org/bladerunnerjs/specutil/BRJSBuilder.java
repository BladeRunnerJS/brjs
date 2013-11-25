package org.bladerunnerjs.specutil;

import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.core.plugin.PluginLoader;
import org.bladerunnerjs.core.plugin.VirtualProxyModelObserverPlugin;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.bundler.VirtualProxyBundlerPlugin;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.core.plugin.command.VirtualProxyCommandPlugin;
import org.bladerunnerjs.core.plugin.content.ContentPlugin;
import org.bladerunnerjs.core.plugin.content.VirtualProxyContentPlugin;
import org.bladerunnerjs.core.plugin.minifier.MinifierPlugin;
import org.bladerunnerjs.core.plugin.minifier.VirtualProxyMinifierPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.appserver.ServletModelAccessor;
import org.bladerunnerjs.specutil.engine.BuilderChainer;
import org.bladerunnerjs.specutil.engine.NodeBuilder;
import org.bladerunnerjs.specutil.engine.SpecTest;
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
