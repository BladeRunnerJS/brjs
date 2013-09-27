package org.bladerunnerjs.core.log;

import static org.mockito.Mockito.*;

import org.bladerunnerjs.core.log.SLF4JLogger;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class SLF4JLoggerTest
{

	private org.slf4j.Logger slf4jLogger;

	private SLF4JLogger logger;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setup()
	{
		slf4jLogger = mock(org.slf4j.Logger.class);
		logger = new SLF4JLogger(slf4jLogger, "TEST");
	}

	@Test
	public void testMessagesAreOnlyLoggedIfWeAreAtThatLogLevelOrHigher() throws Exception
	{
		when(slf4jLogger.isDebugEnabled()).thenReturn(true);
		logger.debug("%s", "some text");
		verify(slf4jLogger).isDebugEnabled();
		verify(slf4jLogger).debug("some text");
	}

	@Test
	public void testMessagesArentLoggedIfCurrentLogLevelisLower() throws Exception
	{
		when(slf4jLogger.isDebugEnabled()).thenReturn(false);
		logger.debug("%s", "some text");
		verify(slf4jLogger).isDebugEnabled();
		verifyNoMoreInteractions(slf4jLogger);
	}
	
	@Test
	public void testToStringMethodIsUsedOnObjects() throws Exception
	{
		Object o = new Object();
		when(slf4jLogger.isErrorEnabled()).thenReturn(true);
		logger.error("%s", o);
		verify(slf4jLogger).error(o.toString());
		
	}
	
	@Test
	public void testAnArrayOfMixedArgsCanBePassedInAndToStringIsUsed() throws Exception
	{
		Object o = new Object();
		when(slf4jLogger.isErrorEnabled()).thenReturn(true);
		logger.error("%s %s", o, this);
		verify(slf4jLogger).error(o.toString() + " " + this.toString());
		
	}

}
