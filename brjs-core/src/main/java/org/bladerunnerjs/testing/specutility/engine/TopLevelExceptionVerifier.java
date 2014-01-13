package org.bladerunnerjs.testing.specutility.engine;

import static org.junit.Assert.*;

import com.google.common.base.Joiner;

public class TopLevelExceptionVerifier {
	private final Throwable exception;
	private VerifierChainer verifierChainer;
	
	public TopLevelExceptionVerifier(SpecTest specTest, Throwable exception) {
		this.exception = exception;
		this.verifierChainer = new VerifierChainer(specTest);
	}
	
	public <T extends Throwable> VerifierChainer whereTopLevelExceptionIs(Class<T> exceptionClass, Object... args) {
		if(!exceptionClass.isInstance(exception)) {
			fail(exception.getClass().getName() + " is not an instance of " + exceptionClass.getName());
		}
		
		if(!ExceptionsVerifier.argsMatch(exception, args)) {
			fail("expected exception arguments [" + Joiner.on(", ").join(args) + "] doesn't match the given exception " + exception);
		}
		
		return this.verifierChainer;
	}
	
	public <T extends Throwable> VerifierChainer whereTopLevelExceptionContainsString(Class<T> exceptionClass, String expectedString) {
		if(!exceptionClass.isInstance(exception)) {
			fail(exception.getClass().getName() + " is not an instance of " + exceptionClass.getName());
		}
		
		if(!ExceptionsVerifier.containsString(exception, expectedString)) {
			fail("expected exception containing string '" + expectedString + "' for given exception " + exception.getMessage());
		}
		
		return this.verifierChainer;
	}
}
