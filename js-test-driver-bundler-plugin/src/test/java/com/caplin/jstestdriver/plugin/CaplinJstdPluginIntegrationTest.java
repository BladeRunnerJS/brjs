package com.caplin.jstestdriver.plugin;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.Permission;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CaplinJstdPluginIntegrationTest {

	PrintStream oldSystemOut;
	PrintStream oldSystemErr;
	final ByteArrayOutputStream systemOut = new ByteArrayOutputStream();
	final ByteArrayOutputStream systemErr = new ByteArrayOutputStream();
	
	@Before
	public void setup() {
		oldSystemOut = System.out;
		oldSystemErr = System.err;
		System.setOut(new PrintStream(systemOut));
		System.setErr(new PrintStream(systemErr));
		
		// stops jstd exiting the JVM - see http://stackoverflow.com/questions/309396/java-how-to-test-methods-that-call-system-exit
		System.setSecurityManager(new NoExitSecurityManager());
	}
	
	@After
	public void tearDown() {
		System.setOut(oldSystemOut);
		System.setErr(oldSystemErr);
		
		System.out.println("#####################################");
		System.out.println("## Output from captured System.out ##");
		System.out.println("#####################################");
		System.out.println(systemOut.toString());
		System.out.println("#####################################");
		
		System.setSecurityManager(null);
	}
	
	@Ignore
	@Test
	public void testADummyPluginCanBeCalledByJSTestDriver() throws Exception {
		
		final String[] commandLineArgs = new String[]{"--tests", "all"};
		
		try {
			/* use for JSTD 1.3.4+ */
//			com.google.jstestdriver.Main.main(commandLineArgs);
			/* use for JSTD 1.3.3 */
			com.google.jstestdriver.JsTestDriver.main(commandLineArgs);
		} catch (ExitException ex) {
			
		} finally {
			String sysOutString = systemOut.toString();
			
			// check each method has been called 'Adding file:' appears twice afterwards 
			assertTrue(sysOutString.contains("DummyBundleInjector.processDependencies:"));
			assertTrue(sysOutString.contains("Adding file:")); sysOutString = sysOutString.replaceFirst("Adding file:", "");
			assertTrue(sysOutString.contains("Adding file:")); sysOutString = sysOutString.replaceFirst("Adding file:", "");
			assertTrue(sysOutString.contains("DummyBundleInjector.processTests:"));
			assertTrue(sysOutString.contains("Adding file:")); sysOutString = sysOutString.replaceFirst("Adding file:", "");
			assertTrue(sysOutString.contains("Adding file:")); sysOutString = sysOutString.replaceFirst("Adding file:", "");
		}

	}
	
	
	
	
	
	@SuppressWarnings("serial")
	protected static class ExitException extends SecurityException 
	{
		public final int status;
		public ExitException(int status) 
		{
				super("ExitException");
				this.status = status;
		}
	}

	private static class NoExitSecurityManager extends SecurityManager 
	{
		@Override
		public void checkPermission(Permission perm) 
		{
				// allow anything.
		}
		@Override
		public void checkPermission(Permission perm, Object context) 
		{
				// allow anything.
		}
		@Override
		public void checkExit(int status) 
		{
				super.checkExit(status);
				throw new ExitException(status);
		}
	}
	
}
