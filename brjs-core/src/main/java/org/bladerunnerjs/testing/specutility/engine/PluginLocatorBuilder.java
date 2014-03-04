package org.bladerunnerjs.testing.specutility.engine;

import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.testing.utility.MockPluginLocator;

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
