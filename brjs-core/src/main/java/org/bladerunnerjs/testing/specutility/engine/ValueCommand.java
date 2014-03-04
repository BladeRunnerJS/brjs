package org.bladerunnerjs.testing.specutility.engine;

public interface ValueCommand<T extends Object> {
	public T call() throws Exception;
}
