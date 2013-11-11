package org.bladerunnerjs.testing.utility;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Joiner;


public class LogMessageStore
{
	public static final String NO_MESSAGES_RECIEVED = "no %s messages have been recieved (or logging has not been enabled for this test)";
	public static final String NO_MESSAGE_MATCHING_RECIEVED = "no %s message matching the message '%s'";
	public static final String MESSAGE_NOT_LOGGED = "%s message '%s' (params: %s) not logged";
	public static final String UNEXPECTED_LOG_MESSAGES = "Unexpected %s log messages: [%s]";
	
	private boolean storeLogs = false; /* enable capturing log messages for 'when' actions */
	private boolean echoLogs = false;
	private boolean loggingEnabled = false;
	private boolean assertionMade = false;
	
	private LinkedList<LogMessage> fatalMessages = new LinkedList<LogMessage>();
	private LinkedList<LogMessage> errorMessages = new LinkedList<LogMessage>();
	private LinkedList<LogMessage> warnMessages = new LinkedList<LogMessage>();
	private LinkedList<LogMessage> infoMessages = new LinkedList<LogMessage>();
	private LinkedList<LogMessage> debugMessages = new LinkedList<LogMessage>();

	public LogMessageStore()
	{
	}
	
	public LogMessageStore(boolean storeLogs)
	{
		this.storeLogs = storeLogs;
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
		verifyLogMessage("fatal", true, fatalMessages, message, params);
	}
	
	public void verifyErrorLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyLogMessage("error", true, errorMessages, message, params);
	}
	
	public void verifyWarnLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyLogMessage("warn", true, warnMessages, message, params);
	}
	
	public void verifyInfoLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyLogMessage("info", true, infoMessages, message, params);
	}
	
	public void verifyDebugLogMessage(String message, Object... params)
	{
		assertionMade = true;
		verifyLogMessage("debug", false, debugMessages, message, params);
	}
	
	public void addFatal(String loggerName, String message, Object... params)
	{
		registerLogMessage(fatalMessages, loggerName, message, params);
	}

	public void addError(String loggerName, String message, Object... params)
	{
		registerLogMessage(errorMessages, loggerName, message, params);
	}
	
	public void addWarn(String loggerName, String message, Object... params)
	{
		registerLogMessage(warnMessages, loggerName, message, params);
	}

	public void addInfo(String loggerName, String message, Object... params)
	{
		registerLogMessage(infoMessages, loggerName, message, params);
	}

	public void addDebug(String loggerName, String message, Object... params)
	{
		registerLogMessage(debugMessages, loggerName, message, params);
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

	private void registerLogMessage(LinkedList<LogMessage> messages, String loggerName, String message, Object[] params)
	{
		if(echoLogs ) {
			System.out.println(String.format(message, params));
		}
		
		if (storeLogs)
		{
			messages.add(new LogMessage(message, params));
		}
	}

	private void verifyLogMessage(String logLevel, boolean strictCheck, LinkedList<LogMessage> messages, String message, Object... params)
	{
		LogMessage foundMessage;
		String isNullFailMessage;
		if (strictCheck)
		{
			foundMessage = (!messages.isEmpty()) ? messages.removeFirst() : null;
			isNullFailMessage = NO_MESSAGES_RECIEVED;
		} else {
			foundMessage = findFirstMessageMatching(messages, message);
			isNullFailMessage = NO_MESSAGE_MATCHING_RECIEVED;
		}
		assertNotNull( String.format(isNullFailMessage, logLevel, message) , foundMessage );
		
		String failMessage = String.format(MESSAGE_NOT_LOGGED, logLevel, message, ArrayUtils.toString(params));
		String expectedMessage = new LogMessage(message, params).toString();
		if (!strictCheck)
		{
			failMessage += "Got message: " + concatenateMessages(messages);
		}
		assertEquals( failMessage, expectedMessage, foundMessage.toString() );
	}

	private String concatenateMessages(LinkedList<LogMessage> messages)
	{
		StringBuilder s = new StringBuilder();
		for (LogMessage m : messages)
		{
			s.append(m);
			s.append("\n");
		}
		return s.toString().trim();
	}

	private LogMessage findFirstMessageMatching(LinkedList<LogMessage> messages, String message)
	{
		for (LogMessage m : messages)
		{
			if (m.message.equals(message))
			{
				return m;
			}
		}
		return null;
	}

	private void verifyNoMoreMessageOnList(String logLevel, List<LogMessage> messages)
	{
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
	}

	public void stopStoringLogs() {
		disableStoringLogs();
	}
}
