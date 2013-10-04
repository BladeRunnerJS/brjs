package org.bladerunnerjs.specutil.engine;

public interface ValueCommand<T extends Object> {
	public T call() throws Exception;
}
