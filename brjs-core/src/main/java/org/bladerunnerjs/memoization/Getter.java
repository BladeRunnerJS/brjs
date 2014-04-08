package org.bladerunnerjs.memoization;

public interface Getter<E extends Exception> {
	Object get() throws E;
}