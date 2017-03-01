package org.bladerunnerjs.api.memoization;

/**
 * A generic Getter entity for obtaining access to a Memoized Value.
 */

public interface Getter<E extends Exception> {
	Object get() throws E;
}