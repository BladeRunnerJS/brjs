package org.bladerunnerjs.specutil.engine;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import com.google.common.base.Joiner;

public class ExceptionsVerifier {
	private final List<Throwable> exceptions;
	private SpecTest specTest;
	
	public ExceptionsVerifier(SpecTest specTest, List<Throwable> exceptions) {
		this.exceptions = exceptions;
		this.specTest = specTest;
	}
	
	public <T extends Throwable> TopLevelExceptionVerifier verifyFormattedException(Class<T> exceptionClass, String message, Object... args) {
		return verifyException(exceptionClass,  new StringBuilder(String.format(message,args)) );
	}
		
	public <T extends Throwable> TopLevelExceptionVerifier verifyException(Class<T> exceptionClass, Object... args) {
		for (Throwable exception : exceptions) {
			Throwable rootCause = getRootCause(exception);
			
			if(rootCause.getClass() == exceptionClass) {
				if(argsMatch(rootCause, args)) {
					exceptions.remove(exception);
					return new TopLevelExceptionVerifier(this.specTest, exception);
				}
			}
		}
		
		fail("could not find exception '" + exceptionClass + "' with arguments ['" +
			Joiner.on("', '").join(args) + "'] within " + exceptions + "");
		
		return null;
	}
	
	public void verifyNoOutstandingExceptions() {
		if (!exceptions.isEmpty())
		{
			System.err.println("\n\n");
			System.err.println("#### Uncaught Exceptions ####");
			System.err.println(ExceptionPrinter.printExceptions(exceptions));
			System.err.println("");
		}
		assertTrue("Unexpected exceptions: " + ExceptionPrinter.printExceptions(exceptions), exceptions.isEmpty());	
	}
	
	private Throwable getRootCause(Throwable exception) {
		Throwable rootCause = ExceptionUtils.getRootCause(exception);
		
		return (rootCause != null) ? rootCause : exception;
	}
	
	static boolean argsMatch(Throwable exception, Object... args) {
		String causeMessage = exception.getMessage();
		boolean argsMatch = true;
		
		for(Object arg : args) {
			String argValue = (arg instanceof String) ? "'" + arg + "'" : arg.toString();
			
			if(!causeMessage.contains(argValue)) {
				argsMatch = false;
				break;
			}
		}
		
		return argsMatch;
	}
	
	static boolean containsString(Throwable exception, String expectedString) {
		return exception.getMessage().contains(expectedString);
	}
}
