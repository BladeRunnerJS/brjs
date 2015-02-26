package org.bladerunnerjs.api.memoization;

public interface Getter<E extends Exception> {
	Object get() throws E;
}