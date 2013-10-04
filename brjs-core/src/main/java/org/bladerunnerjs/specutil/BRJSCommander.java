package org.bladerunnerjs.specutil;

import org.bladerunnerjs.core.plugin.Event;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.specutil.engine.Command;
import org.bladerunnerjs.specutil.engine.CommanderChainer;
import org.bladerunnerjs.specutil.engine.NodeCommander;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.bladerunnerjs.specutil.engine.ValueCommand;
import org.bladerunnerjs.specutil.logging.MockLogLevelAccessor;


public class BRJSCommander extends NodeCommander<BRJS> {
	private final BRJS brjs;
	
	public BRJSCommander(SpecTest modelTest, BRJS brjs) {
		super(modelTest, brjs);
		this.brjs = brjs;
	}
	
	public CommanderChainer populate() throws Exception {
		call(new Command() {
			public void call() throws Exception {
				brjs.populate();
			}
		});
		
		return commanderChainer;
	}
	
	public BRJSConfCommander bladerunnerConf() {
		BRJSConfCommander commander = call(new ValueCommand<BRJSConfCommander>() {
			public BRJSConfCommander call() throws Exception {
				return new BRJSConfCommander(brjs.bladerunnerConf());
			}
		});
		
		return commander;
	}
	
	public CommanderChainer runCommand(final String... args) throws Exception {
		call(new Command() {
			public void call() throws Exception {
				brjs.runCommand(args);
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer runUserCommand(final String... args) {
		call(new Command() {
			public void call() throws Exception {
				brjs.runUserCommand(new MockLogLevelAccessor(), args);
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer discoverApps()
	{
		call(new Command() {
			public void call() throws Exception {
				brjs.apps();
			}
		});
		
		return commanderChainer;
	}

	public CommanderChainer discoverAllChildren() {
		call(new Command() {
			public void call() throws Exception {
				brjs.discoverAllChildren();
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer hasBeenCreated() {
		call(new Command() {
			public void call() throws Exception {
				modelTest.brjs = modelTest.createModel();
			}
		});
		
		return commanderChainer;
	}

	public void eventFires(Event event, Node node)
	{
		node.notifyObservers(event, node);
	}
}
