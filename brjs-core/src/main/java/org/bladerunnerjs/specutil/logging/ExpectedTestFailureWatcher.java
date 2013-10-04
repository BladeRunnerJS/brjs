package org.bladerunnerjs.specutil.logging;

import static org.junit.Assert.*;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static com.google.common.base.Strings.isNullOrEmpty;


/* adapted from https://github.com/mike-ensor/expected-failure */
public class ExpectedTestFailureWatcher implements TestRule
{

	private Class<? extends Throwable> expectedFailureExceptionType;
	private String expectedFailureMessage;
	private boolean onlyMatchStartOfMessage = false;
	
	/**
	 * Static factory to an instance of this watcher
	 * 
	 * @return New instance of this watcher
	 */
	public static ExpectedTestFailureWatcher instance()
	{
		return new ExpectedTestFailureWatcher();
	}

	@Override
	public Statement apply(final Statement base, final Description description)
	{
		return new Statement()
		{

			@Override
			public void evaluate() throws Throwable
			{
				boolean expectedToFail = description.getAnnotation(ExpectedFailure.class) != null;
				boolean failed = false;
				try
				{
					// allow test case to execute
					base.evaluate();
				}
				catch (Throwable exception)
				{
					failed = true;
					if (!expectedToFail)
					{
						throw exception; // did not expect to fail and failed...fail
					}
					else
					{
						if (expectedFailureExceptionType != null)
						{
							assertEquals("Test failed as expected, but expected exception type didnt match", expectedFailureExceptionType, exception.getClass());
						}
						if (expectedFailureMessage != null)
						{
							if (onlyMatchStartOfMessage)
							{
								String failMessage = String.format("Test failed as expected, but expected message didnt start with \"%s\". Actual message was \"%s\"", expectedFailureMessage, exception.getMessage());
								assertTrue(failMessage, exception.getMessage().startsWith(expectedFailureMessage) );
							}
							else
							{
								assertEquals("Test failed as expected, but expected message didnt match", expectedFailureMessage, exception.getMessage());
							}
						}
					}
				}
				// placed outside of catch
				if (expectedToFail && !failed)
				{
					throw new ExpectedTestFailureException(getUnFulfilledFailedMessage(description));
				}
			}

			/**
			 * Extracts detailed message about why test failed
			 * 
			 * @param description
			 * @return
			 */
			private String getUnFulfilledFailedMessage(Description description)
			{
				String reason = null;
				if (description.getAnnotation(ExpectedFailure.class) != null)
				{
					reason = description.getAnnotation(ExpectedFailure.class).value();
				}
				if (isNullOrEmpty(reason))
				{
					reason = "Should have failed but didn't";
				}
				return reason;
			}
		};
	}

	public void expectedExceptionTypeIs(Class<? extends Throwable> t)
	{
		expectedFailureExceptionType = t;
	}

	public void expectedExceptionMessageIs(String message)
	{
		expectedFailureMessage = message;
	}

	public void onlyMatchStartOfMessage()
	{
		onlyMatchStartOfMessage = true;
	}

}
