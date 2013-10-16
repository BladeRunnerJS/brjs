package com.caplin.cutlass.command.testIntegration;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class CutlassIntegrationTestRunListener extends RunListener
{

	private static final int MAX_LINE_CHARS = 70;
	private static final Character FAIL_CHAR = 'F';
	private static final Character PASS_CHAR = '.';
	private static final Character IGNORED_CHAR = 'i';
	
	private boolean currentTestFailed = false;
	private boolean currentTestIgnored = false;
	private int lineCharCount = 0;
	
	@Override
	public void testAssumptionFailure(Failure failure)  
	{
	}

	@Override
	public void	testFailure(Failure failure)
	{
		currentTestFailed = true;
		printChar(FAIL_CHAR);
	}

	@Override
	public void	testFinished(Description description)
	{
		if (!currentTestFailed && !currentTestIgnored)
		{
			printChar(PASS_CHAR);			
		}
	}

	@Override
	public void	testIgnored(Description description)
	{
		currentTestIgnored = true;
		printChar(IGNORED_CHAR);		
	}

	@Override
	public void	testRunFinished(Result result)
	{
		if (lineCharCount > 0)
		{
			System.out.println("");
		}
	}

	@Override
	public void	testRunStarted(Description description) 
	{
	}

	@Override
	public void	testStarted(Description description)
	{
		currentTestFailed = false;
		currentTestIgnored = false;
	}
	
	private void printChar(Character c)
	{
		System.out.print(c);
		lineCharCount++;
		if (lineCharCount >= MAX_LINE_CHARS)
		{
			System.out.println("");
			lineCharCount = 0;
		}
	}
	
}
