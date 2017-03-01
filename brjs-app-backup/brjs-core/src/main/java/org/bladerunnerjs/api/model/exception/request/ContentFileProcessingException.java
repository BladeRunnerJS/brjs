package org.bladerunnerjs.api.model.exception.request;

import org.bladerunnerjs.api.memoization.MemoizedFile;

/**
 * Thrown when problems have been encountered while processing the specified file. 
*/

public class ContentFileProcessingException extends ContentProcessingException
{
	 
	private static final long serialVersionUID = 1L;
	
	private MemoizedFile sourceFile;
	private int lineNumber = -1;
	private int characterNumber = -1;
	
	public ContentFileProcessingException(Exception e, MemoizedFile sourceFile)
	{
		super(e);
		this.sourceFile = sourceFile;
	}
	
	public ContentFileProcessingException(MemoizedFile sourceFile, String message)
	{
		super(message);
		this.sourceFile = sourceFile;
	}
	
	public ContentFileProcessingException(MemoizedFile sourceFile, int lineNumber, int characterNumber, Exception cause)
	{
		super(cause);
		this.sourceFile = sourceFile;
		this.lineNumber = lineNumber;
		this.characterNumber = characterNumber;
	}
	
	public ContentFileProcessingException(MemoizedFile sourceFile, Exception cause) {
		super(cause);
		this.sourceFile = sourceFile;
	}
	
	@Override
	public String toString()
	{
		StringBuilder errorMessage = new StringBuilder();

		String message = getMessage();
		if(message != null)
		{
			errorMessage.append(message + "\n");
		}
		if(this.getCause() != null)
		{
			errorMessage.append("Caused by: " + getCause().getMessage() + "\n");
		}

		if(sourceFile != null)
		{
			errorMessage.append("Exception thrown whilst attempting to process file:\n '" + sourceFile.getAbsolutePath() + "'.\n");
		}	
		
		if(lineNumber >= 0)
		{
			errorMessage.append(" - line " + lineNumber + ", character " + characterNumber + ".\n");
		}
		
		if(message == null)
		{
			errorMessage.append("Stack trace:\n");
			StackTraceElement[] stackTrace = getStackTrace();
			for(StackTraceElement traceElement : stackTrace)
			{
				errorMessage.append("\t" + traceElement.toString() + "\n");
			}
		}
		
		return errorMessage.toString();
	}
	
}
