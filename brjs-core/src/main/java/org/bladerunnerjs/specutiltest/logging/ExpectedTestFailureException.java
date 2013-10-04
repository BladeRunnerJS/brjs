package org.bladerunnerjs.specutiltest.logging;

/* adapted from https://github.com/mike-ensor/expected-failure */
public class ExpectedTestFailureException extends Throwable
{

	private static final long serialVersionUID = -890674033786829732L;

	public ExpectedTestFailureException(String message)
	{
		super(message);
	}
}
