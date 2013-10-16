package org.bladerunnerjs.model.exception.request;

import java.io.File;




public class BundlerFileProcessingException extends BundlerProcessingException
{
	 
	private static final long serialVersionUID = 5326055152554125320L;
	
	private File sourceFile;
	private int lineNumber = -1;
	private int characterNumber = -1;
	
	public BundlerFileProcessingException(BundlerProcessingException e, File sourceFile)
	{
		super(e);
		this.sourceFile = sourceFile;
	}
	
	public BundlerFileProcessingException(File sourceFile, String message)
	{
		super(message);
		this.sourceFile = sourceFile;
	}
	
	public BundlerFileProcessingException(File sourceFile, Throwable cause, String message)
	{
		super(cause, message);
		this.sourceFile = sourceFile;
	}
	
	public BundlerFileProcessingException(File sourceFile, int lineNumber, int characterNumber, String message)
	{
		super(message);
		this.sourceFile = sourceFile;
		this.lineNumber = lineNumber;
		this.characterNumber = characterNumber;
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
