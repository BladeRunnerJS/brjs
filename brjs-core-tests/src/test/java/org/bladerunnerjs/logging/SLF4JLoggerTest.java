package org.bladerunnerjs.logging;

import static org.mockito.Mockito.*;

import java.util.UnknownFormatConversionException;

import org.bladerunnerjs.logging.LoggerTimeAccessor;
import org.bladerunnerjs.logging.SLF4JLogger;
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

	private LoggerTimeAccessor timeAccessor;

	@Before
	public void setup()
	{
		slf4jLogger = mock(org.slf4j.Logger.class);
		timeAccessor = mock(LoggerTimeAccessor.class);
		logger = new SLF4JLogger(slf4jLogger, "TEST", timeAccessor);
	}

	@Test
	public void testMessagesAreOnlyLoggedIfWeAreAtThatLogLevelOrHigher() throws Exception
	{
		when(slf4jLogger.isDebugEnabled()).thenReturn(true);
		logger.debug("%s", "some text");
		verify(slf4jLogger, atLeastOnce()).isDebugEnabled();
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
	
	@Test(expected=UnknownFormatConversionException.class)
	public void anInvalidMessageFormatStringCausesAnExceptionWhenThereAreLogArguments() throws Exception
	{
		when(slf4jLogger.isDebugEnabled()).thenReturn(true);
		logger.debug("%", "some-argument");
	}
	
	@Test
	public void percentageSymbolsCanBeLoggedWhenThereAreNoLogArguments() throws Exception
	{
		when(slf4jLogger.isDebugEnabled()).thenReturn(true);
		logger.debug("%");
		verify(slf4jLogger).debug("%");
	}
	
	@Test
	public void allLogLevelsContainTheTimestampIfDebugIsActive() throws Exception
	{
		when(slf4jLogger.isDebugEnabled()).thenReturn(true);
		when(slf4jLogger.isInfoEnabled()).thenReturn(true);
		when(slf4jLogger.isWarnEnabled()).thenReturn(true);
		when(slf4jLogger.isErrorEnabled()).thenReturn(true);
		when(timeAccessor.getTimestamp()).thenReturn("1234");
		logger.debug("debug log");
		logger.info("info log");
		logger.warn("warn log");
		logger.error("error log");
		verify(slf4jLogger).debug("1234 - debug log");
		verify(slf4jLogger).info("1234 - info log");
		verify(slf4jLogger).warn("1234 - warn log");
		verify(slf4jLogger).error("1234 - error log");
	}
}
