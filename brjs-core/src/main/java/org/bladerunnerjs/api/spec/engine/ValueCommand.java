package org.bladerunnerjs.api.spec.engine;

public interface ValueCommand<T extends Object> {
	public T call() throws Exception;
}
