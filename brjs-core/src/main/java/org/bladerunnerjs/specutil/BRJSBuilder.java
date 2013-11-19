package org.bladerunnerjs.specutil;

import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.core.plugin.PluginLoader;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.core.plugin.minifier.MinifierPlugin;
import org.bladerunnerjs.core.plugin.servlet.ServletPlugin;
import org.bladerunnerjs.model.BRJS;
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
	
	// look at brjs is null
	
	public BuilderChainer hasBeenPopulated() throws Exception {
		brjs.populate();
		
		return builderChainer;
	}

	public BuilderChainer hasCommands(CommandPlugin... commands)
	{
		for(CommandPlugin command : commands)
		{
			specTest.pluginLocator.pluginCommands.add(command);
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasModelObservers(ModelObserverPlugin... modelObservers)
	{
		for(ModelObserverPlugin modelObserver : modelObservers)
		{
			specTest.pluginLocator.modelObservers.add(modelObserver);
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasBundlers(BundlerPlugin... bundlerPlugins)
	{
		for(BundlerPlugin bundlerPlugin : bundlerPlugins)
		{
			specTest.pluginLocator.bundlers.add(bundlerPlugin);
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasServlets(ServletPlugin... servletPlugins)
	{
		for(ServletPlugin servletPlugin : servletPlugins)
		{
			specTest.pluginLocator.servlets.add(servletPlugin);
		}
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsCommands()
	{
		specTest.pluginLocator.bundlers.clear();
		specTest.pluginLocator.pluginCommands.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), CommandPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsModelObservers()
	{
		specTest.pluginLocator.bundlers.clear();
		specTest.pluginLocator.modelObservers.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), ModelObserverPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsBundlers()
	{
		specTest.pluginLocator.bundlers.clear();
		specTest.pluginLocator.bundlers.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), BundlerPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsMinifiers() {
		specTest.pluginLocator.minifiers.clear();
		specTest.pluginLocator.minifiers.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), MinifierPlugin.class) );
		
		return builderChainer;
	}
	
	public BuilderChainer automaticallyFindsServlets() {
		specTest.pluginLocator.minifiers.clear();
		specTest.pluginLocator.servlets.addAll( PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), ServletPlugin.class) );
		
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
	
}
