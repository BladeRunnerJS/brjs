package org.bladerunnerjs.spec.aliasing;

import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.spec.engine.Command;
import org.bladerunnerjs.api.spec.engine.CommanderChainer;
import org.bladerunnerjs.api.spec.engine.ModelCommander;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.spec.engine.SpecTestCommander;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility;


public class AliasesCommander extends ModelCommander implements SpecTestCommander
{

	private Aspect aspect;
	private CommanderChainer commanderChainer;

	public AliasesCommander(SpecTest specTest, Aspect aspect)
	{
		super(specTest);
		this.aspect = aspect;
		this.commanderChainer = new CommanderChainer(specTest);
	}

	public CommanderChainer retrievesAlias(String aliasName)
	{
		call(new Command() {
			public void call() throws Exception {
				AliasingUtility.resolveAlias(aliasName, aspect);
			}
		});
		
		return commanderChainer;
	}

}
