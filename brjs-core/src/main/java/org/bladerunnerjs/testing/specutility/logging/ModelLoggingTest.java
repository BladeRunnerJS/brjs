package org.bladerunnerjs.testing.specutility.logging;

import org.apache.commons.lang3.ArrayUtils;
import org.bladerunnerjs.testing.specutility.LoggerVerifier;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.utility.LogMessage;
import org.bladerunnerjs.testing.utility.LogMessageStore;
import org.junit.ComparisonFailure;
import org.junit.Rule;
import org.junit.Test;


/*
 * NOTE: these tests are expected to 'fail' - the @ExpectedFailure turns a jUnit failure in to a pass, and a pass into a fail.
 */
public class ModelLoggingTest extends SpecTest
{
	private static final String LOG_MESSAGE_ONE_PARAM = "some message %s";
	private static final String LOG_MESSAGE_TWO_PARAM = "some message %s %s";
	private static final String PARAM1 = "param1";
	private static final String PARAM2 = "param2";
	private static final String SINGLE_PARAM_ARRAY = ArrayUtils.toString( new String[] { PARAM1 } );
	private static final String DOUBLE_PARAM_ARRAY = ArrayUtils.toString( new String[] { PARAM1, PARAM2 } );
	
	ModelLoggingCommander logCommander = new ModelLoggingCommander(this);
	
	@Rule
	public ExpectedTestFailureWatcher expectedFailure = ExpectedTestFailureWatcher.instance();
	
	
	@Test /* 1A. we comment out a log.error() */
	@ExpectedFailure("Test should have failed since we didnt log a message")
	public void testFailsWhenWeMissAnErrorLogMessage() {
		expectFailureTypeAndMessage(AssertionError.class, LogMessageStore.NO_MESSAGES_RECIEVED, "error" );
		
		given(logging).enabled();
		logCommander.whenNoMessagesLogged();
		then(logging).errorMessageReceived(LOG_MESSAGE_ONE_PARAM, PARAM1);
	}
	
	@Test /* 1B. we comment out a log.info() */
	@ExpectedFailure("Test should have failed since we didnt log a message")
	public void testFailsWhenWeMissAnInfoLogMessage() {
		expectFailureTypeAndMessage(AssertionError.class, LogMessageStore.NO_MESSAGES_RECIEVED, "info" );
		
		given(logging).enabled();
		logCommander.whenNoMessagesLogged();
		then(logging).infoMessageReceived(LOG_MESSAGE_ONE_PARAM, PARAM1);
	}
	
	@Test /* 1C. we comment out a log.debug() */
	@ExpectedFailure("Test should have failed since we didnt log a message")
	public void testFailsWhenWeMissAnDebugLogMessage() {
		expectFailureTypeAndMessageStartsWith(AssertionError.class, LogMessageStore.NO_MESSAGE_MATCHING_RECIEVED, "debug", LOG_MESSAGE_ONE_PARAM, PARAM1 );
		
		given(logging).enabled();
		logCommander.whenNoMessagesLogged();
		then(logging).debugMessageReceived(LOG_MESSAGE_ONE_PARAM, PARAM1);
	}
	
	
	@Test /* 2A. we introduce an extra parameter in a log.error() */
	@ExpectedFailure("Test should have failed since we added a new param to the logged message")
	public void testFailsWhenWeIntroduceAnotherParameterInAnErrorLogMessage() {
		expectFailureTypeAndMessageStartsWith(ComparisonFailure.class, LogMessageStore.MESSAGE_NOT_LOGGED, "error", LOG_MESSAGE_TWO_PARAM, SINGLE_PARAM_ARRAY );
		
		given(logging).enabled();
		logCommander.whenErrorMessageLogged(LOG_MESSAGE_TWO_PARAM, PARAM1, PARAM2);
		then(logging).errorMessageReceived(LOG_MESSAGE_TWO_PARAM, PARAM1);
	}
	
	@Test /* 2B. we introduce an extra parameter in a log.info() */
	@ExpectedFailure("Test should have failed since we added a new param to the logged message")
	public void testFailsWhenWeIntroduceAnotherParameterInAnInfoLogMessage() {
		expectFailureTypeAndMessageStartsWith(ComparisonFailure.class, LogMessageStore.MESSAGE_NOT_LOGGED, "info", LOG_MESSAGE_TWO_PARAM, SINGLE_PARAM_ARRAY );
		
		given(logging).enabled();
		logCommander.whenInfoMessageLogged(LOG_MESSAGE_TWO_PARAM, PARAM1, PARAM2);
		then(logging).infoMessageReceived(LOG_MESSAGE_TWO_PARAM, PARAM1);
	}
	
	@Test /* 2C. we introduce an extra parameter in a log.debug() */
	@ExpectedFailure("Test should have failed since we added a new param to the logged message")
	public void testFailsWhenWeIntroduceAnotherParameterInADebugLogMessage() {
		expectFailureTypeAndMessageStartsWith(ComparisonFailure.class, LogMessageStore.MESSAGE_NOT_LOGGED, "debug", LOG_MESSAGE_TWO_PARAM, SINGLE_PARAM_ARRAY );
		
		given(logging).enabled();
		logCommander.whenDebugMessageLogged(LOG_MESSAGE_TWO_PARAM, PARAM1, PARAM2);
		then(logging).debugMessageReceived(LOG_MESSAGE_TWO_PARAM, PARAM1);
	}
	
	
	
	@Test /* 3A. we expect an extra parameter from a log.error() */
	@ExpectedFailure("Test should have failed since we expected an extra param in the message")
	public void testFailsWhenWeExpectAnExtraParameterInAssertedErrorMessage() {
		expectFailureTypeAndMessageStartsWith(ComparisonFailure.class, LogMessageStore.MESSAGE_NOT_LOGGED, "error", LOG_MESSAGE_TWO_PARAM, DOUBLE_PARAM_ARRAY );
		
		given(logging).enabled();
		logCommander.whenErrorMessageLogged(LOG_MESSAGE_TWO_PARAM, PARAM1);
		then(logging).errorMessageReceived(LOG_MESSAGE_TWO_PARAM, PARAM1, PARAM2);
	}
	
	@Test /* 3B. we expect an extra parameter from a log.info() */
	@ExpectedFailure("Test should have failed since we expected an extra param in the message")
	public void testFailsWhenWeExpectAnExtraParameterInAssertedInfoMessage() {
		expectFailureTypeAndMessageStartsWith(ComparisonFailure.class, LogMessageStore.MESSAGE_NOT_LOGGED, "info", LOG_MESSAGE_TWO_PARAM, DOUBLE_PARAM_ARRAY );
		
		given(logging).enabled();
		logCommander.whenInfoMessageLogged(LOG_MESSAGE_TWO_PARAM, PARAM1);
		then(logging).infoMessageReceived(LOG_MESSAGE_TWO_PARAM, PARAM1, PARAM2);
	}
	
	@Test /* 3C. we expect an extra parameter from a log.debug() */
	@ExpectedFailure("Test should have failed since we expected an extra param in the message")
	public void testFailsWhenWeExpectAnExtraParameterInAssertedDebugMessage() {
		expectFailureTypeAndMessageStartsWith(ComparisonFailure.class, LogMessageStore.MESSAGE_NOT_LOGGED, "debug", LOG_MESSAGE_TWO_PARAM, DOUBLE_PARAM_ARRAY );
		
		given(logging).enabled();
		logCommander.whenDebugMessageLogged(LOG_MESSAGE_TWO_PARAM, PARAM1);
		then(logging).debugMessageReceived(LOG_MESSAGE_TWO_PARAM, PARAM1, PARAM2);
	}

	
	
	@Test /* 4A. we don't enable logging for something that logs at error level */
	@ExpectedFailure("Test should have failed since an error was logged")
	public void testFailsWhenWeDontEnableLoggingButAnErrorLogHappens() {
		given(logging).disabled();
		logCommander.whenErrorMessageLogged(LOG_MESSAGE_ONE_PARAM, PARAM1);
	}
	
	@Test /* 4B. we don't enable logging for something that logs at info level */
	/* test should not fail since we can ignore messages when logging is disabled */
	public void testFailsWhenWeDontEnableLoggingButAnInfoLogHappens() {
		given(logging).disabled();
		logCommander.whenInfoMessageLogged(LOG_MESSAGE_ONE_PARAM, PARAM1);
	}
	
	@Test /* 4C. we don't enable logging for something that logs at debug level */
	/* test should not fail since we can ignore messages when logging is disabled */
	public void testFailsWhenWeDontEnableLoggingButADebugLogHappens() {
		given(logging).disabled();
		logCommander.whenDebugMessageLogged(LOG_MESSAGE_ONE_PARAM, PARAM1);
	}
	

	@Test /* 5. we enable logging for something that doesn't log, and forget to make any assertions */
	@ExpectedFailure("Test should have failed since there were no messages asserted")
	public void testFailsWhenWeEnableLoggingButDontMakeAnyErrorAssertions() {
		expectFailureTypeAndMessage(AssertionError.class, LoggerVerifier.LOGGING_ENABLED_BUT_NO_ASSERTIONS_MESSAGE);
		
		given(logging).enabled();
	}
	
	
	
	@Test /* 6A. we enable logging for something that logs at error level, and forget to make any assertions */
	@ExpectedFailure("Test should have failed since there were log messages that werent asserted")
	public void testFailsWhenWeEnabledLogingAndErrorMessageLoggedButNoAssertions() {
		expectFailureTypeAndMessage(AssertionError.class, LogMessageStore.UNEXPECTED_LOG_MESSAGES, "error", new LogMessage(LOG_MESSAGE_ONE_PARAM, PARAM1 ) );
		
		given(logging).enabled();
		logCommander.whenErrorMessageLogged(LOG_MESSAGE_ONE_PARAM, PARAM1);
	}
	
	@Test /* 6B. we enable logging for something that logs at info level, and forget to make any assertions */
	@ExpectedFailure("Test should have failed since there were log messages that werent asserted")
	public void testFailsWhenWeEnabledLogingAndInfoMessageLoggedButNoAssertions() {
		expectFailureTypeAndMessage(AssertionError.class, LogMessageStore.UNEXPECTED_LOG_MESSAGES, "info", new LogMessage(LOG_MESSAGE_ONE_PARAM, PARAM1 ) );
		
		given(logging).enabled();
		logCommander.whenInfoMessageLogged(LOG_MESSAGE_ONE_PARAM, PARAM1);
	}
	
	@Test /* 6C. we enable logging for something that logs at debug level, and forget to make any assertions */
	@ExpectedFailure("Test should have failed since we didn't assert anything")
	public void testFailsWhenWeEnabledLogingAndDebugMessageLoggedButNoAssertions() {
		expectFailureTypeAndMessage(AssertionError.class, LoggerVerifier.LOGGING_ENABLED_BUT_NO_ASSERTIONS_MESSAGE);
		
		expectedFailure.expectedExceptionTypeIs(AssertionError.class);
		given(logging).enabled();
		logCommander.whenDebugMessageLogged(LOG_MESSAGE_ONE_PARAM, PARAM1);
	}
	
	
	
	@Test /* 7A. we enable logging, and also invoke a method within our given that logs at error level */
	/* happy path - test should pass */
	public void testPassesWhenWeEnableLoggingAndMakeTheCorrectErrorAssertions() {
		expectedFailure.expectedExceptionTypeIs(AssertionError.class);
		
		given(logging).enabled();
		logCommander.whenErrorMessageLogged(LOG_MESSAGE_ONE_PARAM, PARAM1);
		then(logging).errorMessageReceived(LOG_MESSAGE_ONE_PARAM, PARAM1);
	}
	
	@Test /* 7B. we enable logging, and also invoke a method within our given that logs at info level */
	/* happy path - test should pass */
	public void testPassesWhenWeEnableLoggingAndMakeTheCorrectInfoAssertions() {
		expectedFailure.expectedExceptionTypeIs(AssertionError.class);
		
		given(logging).enabled();
		logCommander.whenInfoMessageLogged(LOG_MESSAGE_ONE_PARAM, PARAM1);
		then(logging).infoMessageReceived(LOG_MESSAGE_ONE_PARAM, PARAM1);
	}
	
	@Test /* 7C. we enable logging, and also invoke a method within our given that logs at debug level */
	/* happy path - test should pass */
	public void testPassesWhenWeEnableLoggingAndMakeTheCorrectDebugAssertions() {
		expectedFailure.expectedExceptionTypeIs(AssertionError.class);
		
		given(logging).enabled();
		logCommander.whenDebugMessageLogged(LOG_MESSAGE_ONE_PARAM, PARAM1);
		then(logging).debugMessageReceived(LOG_MESSAGE_ONE_PARAM, PARAM1);
	}
	
	
	@ExpectedFailure("Test should have failed since the param is different")
	public void testAddingQuotesAroundAParamFailsTheTestSinceTheParamIsDifferent() {
		expectFailureTypeAndMessageStartsWith(AssertionError.class, LogMessageStore.MESSAGE_NOT_LOGGED, "debug", LOG_MESSAGE_TWO_PARAM, DOUBLE_PARAM_ARRAY );
		
		given(logging).enabled();
		logCommander.whenDebugMessageLogged(LOG_MESSAGE_ONE_PARAM, PARAM1);
		then(logging).debugMessageReceived(LOG_MESSAGE_ONE_PARAM, "PARAM1");
	}
	
	private void expectFailureTypeAndMessage(Class<? extends Throwable> t, String message, Object... params)
	{
		expectedFailure.expectedExceptionTypeIs(t);
		expectedFailure.expectedExceptionMessageIs( String.format(message, params) );
	}
	
	private void expectFailureTypeAndMessageStartsWith(Class<? extends Throwable> t, String message, Object... params)
	{
		expectFailureTypeAndMessage(t, message, params);
		expectedFailure.onlyMatchStartOfMessage();
	}
}
