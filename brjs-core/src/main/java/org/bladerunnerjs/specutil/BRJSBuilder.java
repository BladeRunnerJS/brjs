package org.bladerunnerjs.specutil;

import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.specutil.engine.BuilderChainer;
import org.bladerunnerjs.specutil.engine.NodeBuilder;
import org.bladerunnerjs.specutil.engine.SpecTest;


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
