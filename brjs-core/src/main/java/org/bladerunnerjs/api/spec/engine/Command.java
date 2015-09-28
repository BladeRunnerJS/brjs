package org.bladerunnerjs.api.spec.engine;

/**
 * Used to create a chain of Commands within the {@link CommanderChainer} for the commander utilities.
 */

public interface Command {
	public void call() throws Exception;
}
