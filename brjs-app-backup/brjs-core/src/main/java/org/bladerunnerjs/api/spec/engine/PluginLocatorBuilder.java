package org.bladerunnerjs.api.spec.engine;

import org.bladerunnerjs.api.plugin.CommandPlugin;
import org.bladerunnerjs.api.spec.utility.MockPluginLocator;

public class PluginLocatorBuilder {
	private MockPluginLocator pluginLocator;
	private CommanderChainer commanderChainer;
	
	public PluginLocatorBuilder(SpecTest specTest, MockPluginLocator pluginLocator) {
		this.pluginLocator = pluginLocator;
		
		commanderChainer = new CommanderChainer(specTest);
	}
	
	public CommanderChainer hasCommand(CommandPlugin command) {
		pluginLocator.pluginCommands.add(command);
		
		return commanderChainer;
	}
}
