package org.bladerunnerjs.specutil.engine;

import static org.junit.Assert.*;

import com.google.common.base.Joiner;

public class TopLevelExceptionVerifier {
	private final Throwable exception;
	
	public TopLevelExceptionVerifier(Throwable exception) {
		this.exception = exception;
	}
	
	public <T extends Throwable> void whereTopLevelExceptionIs(Class<T> exceptionClass, Object... args) {
		if(!exceptionClass.isInstance(exception)) {
			fail(exception.getClass().getName() + " is not an instance of " + exceptionClass.getName());
		}
		
		if(!ExceptionsVerifier.argsMatch(exception, args)) {
			fail("expected exception arguments [" + Joiner.on(", ").join(args) + "] don't match the given exception " + exception);
		}
	}
}
