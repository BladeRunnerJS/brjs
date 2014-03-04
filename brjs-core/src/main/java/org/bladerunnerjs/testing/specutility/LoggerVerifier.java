package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.specutility.engine.VerifierChainer;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.junit.Assert;



public class LoggerVerifier
{
	public static final String LOGGING_ENABLED_BUT_NO_ASSERTIONS_MESSAGE = "Logging was enabled, but 0 log messages were asserted. Either expect some log messages or disable logging for this test.";
	private final LogMessageStore logStore;
	private final VerifierChainer verifierChainer;
	
	public LoggerVerifier(SpecTest modelTest, LogMessageStore logStore)
	{
		this.logStore = logStore;
		verifierChainer = new VerifierChainer(modelTest);
	}

	public VerifierChainer enableLogging()
	{
		logStore.enableLogging();
		
		return verifierChainer;
	}

	public VerifierChainer disableLogging()
	{
		logStore.disableLogging();
		
		return verifierChainer;
	}

	public VerifierChainer enableStoringLogs()
	{
		if (logStore.isLoggingEnabled())
		{
			logStore.enableStoringLogs();
		}
		
		return verifierChainer;
	}
	
	public VerifierChainer disableStoringLogs()
	{
		logStore.disableStoringLogs();
		
		return verifierChainer;
	}
	
	public VerifierChainer verifyLogsRecievedIfCaptureEnabled()
	{
		if (logStore.isLoggingEnabled())
		{
			Assert.assertTrue(LOGGING_ENABLED_BUT_NO_ASSERTIONS_MESSAGE, logStore.isAssertionMade());
		}
		
		return verifierChainer;
	}
	
	public VerifierChainer noMessagesLogged()	
	{
		logStore.noMessagesLogged();
		
		return verifierChainer;
	}
	
	public VerifierChainer errorMessageReceived(String message, Object... params)
	{
		logStore.verifyErrorLogMessage(message, params);
		
		return verifierChainer;
	}

	public VerifierChainer warnMessageReceived(String message, Object... params)
	{
		logStore.verifyWarnLogMessage(message, params);
		
		return verifierChainer;
	}

	public VerifierChainer infoMessageReceived(String message, Object... params)
	{
		logStore.verifyInfoLogMessage(message, params);
		
		return verifierChainer;
	}

	public VerifierChainer debugMessageReceived(String message, Object... params)
	{
		logStore.verifyDebugLogMessage(message, params);
		
		return verifierChainer;
	}

	public VerifierChainer verifyNoUnhandledMessages() {
		logStore.verifyNoUnhandledMessage();
		
		return verifierChainer;
	}
}
