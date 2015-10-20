package org.bladerunnerjs.api.spec.utility;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Joiner;


public class LogMessageStore
{
	public static final String NO_MESSAGES_RECEIVED = "no %s messages have been received (or logging has not been enabled for this test)";
	public static final String NO_MESSAGE_MATCHING_RECEIVED = "no %s message matching the message '%s'";
	public static final String MESSAGE_NOT_LOGGED = "%s message '%s' (params: %s) not logged";
	public static final String UNEXPECTED_LOG_MESSAGES = "Unexpected %s log messages: [%s]";
	
	private boolean storeLogs = false; /* enable capturing log messages for 'when' actions */
	private boolean storeConsoleLogs = false; /* enable capturing console log messages for 'when' actions */
	private boolean echoLogs = false;
	private boolean loggingEnabled = false;
	private boolean assertionMade = false;
	
	private List<LogMessage> fatalMessages = Collections.synchronizedList(new LinkedList<LogMessage>());
	private List<LogMessage> errorMessages = Collections.synchronizedList(new LinkedList<LogMessage>());
	private List<LogMessage> warnMessages = Collections.synchronizedList(new LinkedList<LogMessage>());
	private List<LogMessage> consoleMessages = Collections.synchronizedList(new LinkedList<LogMessage>());
	private List<LogMessage> infoMessages = Collections.synchronizedList(new LinkedList<LogMessage>());
	private List<LogMessage> debugMessages = Collections.synchronizedList(new LinkedList<LogMessage>());

	private enum LogMatchType {
		MATCH_FIRST_EXACTLY,
		MATCH_ANY_EXACTLY,
		MATCH_ANY_MESSAGE_IGNORE_ARGS
	}
	
	public LogMessageStore()
	{
		clearLogs();
	}
	
	public void clearLogs()
	{
		fatalMessages.clear();
		errorMessages.clear();
		warnMessages.clear();
		consoleMessages.clear();
		infoMessages.clear();
		debugMessages.clear();
	}
	
	public LogMessageStore(boolean storeLogs)
	{
		this.storeLogs = storeLogs;
		this.storeConsoleLogs = storeLogs;
	}
	
	public void enableEchoingLogs()
	{
		System.out.println("");
		System.out.println("Echoing logs for test:");
		
		echoLogs = true;
	}
	
	public void enableStoringLogs()
	{
		storeLogs = true;
	}
	
	public void enableStoringConsoleLogs()
	{
		storeConsoleLogs = true;
	}

	public void disableStoringLogs()
	{
		storeLogs = false;
	}
	
	public void enableLogging() {
		loggingEnabled = true;
	}
	
	public void disableLogging() {
		loggingEnabled = false;
	}
	
	public boolean isLoggingEnabled() {
		return loggingEnabled;
	}
	
	public boolean isAssertionMade() {
		return assertionMade;
	}
	
	public void verifyFatalLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyLogMessage("fatal", LogMatchType.MATCH_FIRST_EXACTLY, fatalMessages, new LogMessage(message, params));
	}
	
	public void verifyErrorLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyLogMessage("error", LogMatchType.MATCH_FIRST_EXACTLY, errorMessages, new LogMessage(message, params));
	}
	
	public void verifyWarnLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyLogMessage("warn", LogMatchType.MATCH_FIRST_EXACTLY, warnMessages, new LogMessage(message, params));
	}
	
	public void verifyInfoLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyLogMessage("info", LogMatchType.MATCH_FIRST_EXACTLY, infoMessages, new LogMessage(message, params));
	}
	
	public void verifyDebugLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyLogMessage("debug", LogMatchType.MATCH_ANY_MESSAGE_IGNORE_ARGS, debugMessages, new LogMessage(message, params));
	}
	
	public void verifyUnorderedErrorLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyLogMessage("error", LogMatchType.MATCH_ANY_EXACTLY, errorMessages, new LogMessage(message, params));
	}
	
	public void verifyUnorderedWarnLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyLogMessage("warn", LogMatchType.MATCH_ANY_EXACTLY, warnMessages, new LogMessage(message, params));
	}
	
	public void verifyUnorderedInfoLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyLogMessage("info", LogMatchType.MATCH_ANY_EXACTLY, infoMessages, new LogMessage(message, params));
	}
	
	public void verifyFormattedConsoleLogMessage(String message, Object... params)
	{
		verifyConsoleLogMessages( new ConsoleLogMessage(message, params).toString() );
	}
	
	public void verifyConsoleLogMessages(String... messages)
	{
		String acutalConsoleMessages = StringUtils.join(consoleMessages, "\n");
		String expectedMessages = StringUtils.join(messages, "\n");
		if ( !acutalConsoleMessages.contains(expectedMessages) ) {
			assertEquals("console messages didnt contain expected text", expectedMessages, acutalConsoleMessages);
		}
	}
	
	public void verifyConsoleLogDoesNotContain(String... messages)
	{
		String acutalConsoleMessages = StringUtils.join(consoleMessages, "\n");
		for (String message : messages) {
			if ( acutalConsoleMessages.contains(message) ) {
				assertEquals("console messages contain unexpected text", message, acutalConsoleMessages);
			}			
		}
	}
	
	public void verifyNoConsoleLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyNoLogMessage("console", consoleMessages, new ConsoleLogMessage(message, params));
	}
	
	public void verifyNoErrorLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyNoLogMessage("error", errorMessages, new LogMessage(message, params));
	}
	
	public void verifyNoWarnLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyNoLogMessage("warn", warnMessages, new LogMessage(message, params));
	}
	
	public void verifyNoInfoLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyNoLogMessage("info", infoMessages, new LogMessage(message, params));
	}
	
	public void verifyNoDebugLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyNoLogMessage("debug", debugMessages, new LogMessage(message, params));
	}
	
	public void addFatal(String loggerName, String message, Object... params)
	{
		registerLogMessage(fatalMessages, loggerName, new LogMessage(message, params));
	}

	public void addError(String loggerName, String message, Object... params)
	{
		registerLogMessage(errorMessages, loggerName, new LogMessage(message, params));
	}
	
	public void addWarn(String loggerName, String message, Object... params)
	{
		registerLogMessage(warnMessages, loggerName, new LogMessage(message, params));
	}

	public void addConsole(String loggerName, String message, Object[] params)
	{
		registerLogMessage(consoleMessages, loggerName, new ConsoleLogMessage(message, params));
	}
	
	public void addInfo(String loggerName, String message, Object... params)
	{
		registerLogMessage(infoMessages, loggerName, new LogMessage(message, params));
	}

	public void addDebug(String loggerName, String message, Object... params)
	{
		registerLogMessage(debugMessages, loggerName, new LogMessage(message, params));
	}

	public void verifyNoMoreFatalMessages()
	{
		verifyNoMoreMessageOnList("fatal", fatalMessages);
	}
	
	public void verifyNoMoreErrorMessages()
	{
		verifyNoMoreMessageOnList("error", errorMessages);
	}

	public void verifyNoMoreWarnMessages()
	{
		verifyNoMoreMessageOnList("warn", warnMessages);
	}
	
	public void verifyNoMoreInfoMessages()
	{
		verifyNoMoreMessageOnList("info", infoMessages);
	}

	private void registerLogMessage(List<LogMessage> messages, String loggerName, LogMessage logMessage)
	{
		if (echoLogs ) {
			System.out.println(logMessage.getFormattedMessage());
		}
		
		if (messages == fatalMessages || messages == errorMessages) {
			messages.add(logMessage);
		}
		else if (messages == consoleMessages) {
			if (storeConsoleLogs) {
				messages.add(logMessage);
			}
		}
		else if (storeLogs)
		{
			messages.add(logMessage);
		} else if (echoLogs) {
			System.err.println( String.format("Log message '%s' was made but the log store has not been configured to store it.", logMessage) );
		}
	}

	private void verifyLogMessage(String logLevel, LogMatchType logMatchType, List<LogMessage> messages, LogMessage expectedMessage)
	{
		assertTrue("log message can't be empty", expectedMessage.message.length() > 0);
		
		LogMessage foundMessage = null;
		String isNullFailMessage = null;
		switch (logMatchType) {
			case MATCH_FIRST_EXACTLY:
				foundMessage = (!messages.isEmpty()) ? messages.remove(0) : null;
				isNullFailMessage = NO_MESSAGES_RECEIVED;
				break;
			case MATCH_ANY_EXACTLY:
				foundMessage = findFirstMessageMatching(messages, expectedMessage.message, expectedMessage.params);
				isNullFailMessage = NO_MESSAGE_MATCHING_RECEIVED;
				break;
			case MATCH_ANY_MESSAGE_IGNORE_ARGS:
				foundMessage = findFirstMessageMatching(messages, expectedMessage.message, null);
				isNullFailMessage = NO_MESSAGE_MATCHING_RECEIVED;
				break;
			
		}
		
		assertNotNull( String.format(isNullFailMessage, logLevel, expectedMessage.message) , foundMessage );
		
		String failMessage = String.format(MESSAGE_NOT_LOGGED, logLevel, expectedMessage.message, ArrayUtils.toString(expectedMessage.params));
		if (logMatchType == LogMatchType.MATCH_ANY_EXACTLY || logMatchType == LogMatchType.MATCH_FIRST_EXACTLY)
		{
			failMessage += "Got message: " + concatenateMessages(messages);
		}
		assertEquals( failMessage, expectedMessage.toString(), foundMessage.toString() );
	}
	
	private void verifyNoLogMessage(String logLevel, List<LogMessage> messages, LogMessage logMessage)
	{
		LogMessage foundMessage = findFirstMessageMatching(messages, logMessage.message, null);
		assertNull( String.format("found log message, expected not to", logLevel, logMessage.message) , foundMessage );
	}

	private String concatenateMessages(List<LogMessage> messages)
	{
		StringBuilder s = new StringBuilder();
		for (LogMessage m : messages)
		{
			s.append(m);
			s.append("\n");
		}
		return s.toString().trim();
	}

	private LogMessage findFirstMessageMatching(List<LogMessage> messages, String message, Object[] matchArgs)
	{
		LogMessage foundMessage = null;
		for (LogMessage m : messages)
		{
			if (m.message.equals(message))
			{
				if (matchArgs == null) {
					foundMessage = m;
					break;
				} else if (matchArgs != null && StringUtils.join(m.params).equals(StringUtils.join(matchArgs))) {
					foundMessage = m;
					break;
				}
			}
		}
		messages.remove(foundMessage);
		return foundMessage;
	}

	private void verifyNoMoreMessageOnList(String logLevel, List<LogMessage> messages)
	{
		assertionMade = true;
		assertTrue( String.format(UNEXPECTED_LOG_MESSAGES, logLevel, Joiner.on(",\n\t\t").join(messages)), messages.isEmpty() );
	}

	public void verifyNoUnhandledMessage()
	{
		verifyNoMoreFatalMessages();
		verifyNoMoreErrorMessages();
		verifyNoMoreWarnMessages();
		verifyNoMoreInfoMessages();
	}
	
	public void noMessagesLogged()
	{
		assertionMade = true;
		verifyNoUnhandledMessage();
	}

	public void storeLogsIfEnabled() {
		if (isLoggingEnabled())
		{
			enableStoringLogs();
		}
		enableStoringConsoleLogs();
	}

	public void stopStoringLogs() {
		disableStoringLogs();
	}

	public void emptyLogStore()
	{
		clearLogs();
	}
	
}
