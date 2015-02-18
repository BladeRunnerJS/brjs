package org.bladerunnerjs.model;

import static org.junit.Assert.*;

import org.junit.Test;

import org.bladerunnerjs.model.exception.request.MalformedRequestException;

public class MalformedBundlerRequestExceptionTest {

	@Test
	public void testCreatingExceptionWithAllArgsProvided() throws Exception {
		try {
			MalformedRequestException ex = new MalformedRequestException("foo.bundle", "oops, there was a problem");
			ex.setCharacterNumber(4);
			throw ex;
		} catch (MalformedRequestException ex) {
			String[] exceptionLines = ex.toString().split("\n");
			assertEquals( "MalformedBundlerRequestException when processing the request 'foo.bundle'.", exceptionLines[0] );
			assertEquals( "Invalid character at position 4: foo.bundle", exceptionLines[1] );
			assertEquals( "                                    ^", exceptionLines[2] );
			assertEquals( "oops, there was a problem", exceptionLines[3] );
			assertEquals( "The stack trace for the exception is below.", exceptionLines[4] );
			assertTrue( exceptionLines[5].contains(getCurrentClassAndMethod()) );
		}
	}
	
	@Test
	public void testCreatingExceptionWithoutMessage() throws Exception {
		try {
			MalformedRequestException ex = new MalformedRequestException("foo.bundle", null);
			ex.setCharacterNumber(4);
			throw ex;
		} catch (MalformedRequestException ex) {
			String[] exceptionLines = ex.toString().split("\n");
			assertEquals( "MalformedBundlerRequestException when processing the request 'foo.bundle'.", exceptionLines[0] );
			assertEquals( "Invalid character at position 4: foo.bundle", exceptionLines[1] );
			assertEquals( "                                    ^", exceptionLines[2] );
			assertEquals( "The stack trace for the exception is below.", exceptionLines[3] );
			assertTrue( exceptionLines[4].contains(getCurrentClassAndMethod()) );
		}
	}
	
	@Test
	public void testCreatingExceptionWithoutCharNumber() throws Exception {
		try {
			throw new MalformedRequestException("foo.bundle", "oops, there was a problem");
		} catch (MalformedRequestException ex) {
			String[] exceptionLines = ex.toString().split("\n");
			assertEquals( "MalformedBundlerRequestException when processing the request 'foo.bundle'.", exceptionLines[0] );
			assertEquals( "oops, there was a problem", exceptionLines[1] );
			assertEquals( "The stack trace for the exception is below.", exceptionLines[2] );
			assertTrue( exceptionLines[3].contains(getCurrentClassAndMethod()) );
		}
	}
	
	@Test
	public void testCreatingExceptionWithoutUrl() throws Exception {
		try {
			MalformedRequestException ex = new MalformedRequestException(null, "oops, there was a problem");
			ex.setCharacterNumber(4);
			throw ex;
		} catch (MalformedRequestException ex) {
			String[] exceptionLines = ex.toString().split("\n");
			assertEquals( "MalformedBundlerRequestException.", exceptionLines[0] );
			assertEquals( "oops, there was a problem", exceptionLines[1] );
			assertEquals( "The stack trace for the exception is below.", exceptionLines[2] );
			assertTrue( exceptionLines[3].contains(getCurrentClassAndMethod()) );
		}
	}
	
	@Test
	public void testCreatingExceptionWithOnlyAMessage() throws Exception {
		try {
			throw new MalformedRequestException(null, "oops, there was a problem");
		} catch (MalformedRequestException ex) {
			String[] exceptionLines = ex.toString().split("\n");
			assertEquals( "MalformedBundlerRequestException.", exceptionLines[0] );
			assertEquals( "oops, there was a problem", exceptionLines[1] );
			assertEquals( "The stack trace for the exception is below.", exceptionLines[2] );
			assertTrue( exceptionLines[3].contains(getCurrentClassAndMethod()) );
		}
	}
	
	@Test 
	public void testCreatingExceptionWithoutUrlCharNumberOrMessage() throws Exception {
		try {
			throw new MalformedRequestException(null, null);
		} catch (MalformedRequestException ex) {
			String[] exceptionLines = ex.toString().split("\n");
			assertEquals( "MalformedBundlerRequestException.", exceptionLines[0] );
			assertEquals( "The stack trace for the exception is below.", exceptionLines[1] );
		}		
	}
	
	
	private String getCurrentClassAndMethod() {
		StackTraceElement stackTraceElements[] = (new Throwable()).getStackTrace();
		return stackTraceElements[1].getClassName() + "." + stackTraceElements[1].getMethodName();
	}
	
}