package org.bladerunnerjs.model.exception.request;

public class MalformedRequestException extends RequestHandlingException
{

	private static final long serialVersionUID = 1L;
	private String request = "";
	private int characterNumber = -1;

	public MalformedRequestException(String request, String message)
	{
		super(message);
		this.request = request;
	}

	public int getCharacterNumber()
	{
		return characterNumber;
	}

	public void setCharacterNumber(int characterNumber)
	{
		this.characterNumber = characterNumber;
	}

	public String getRequest()
	{
		return request;
	}

	public String toString()
	{
		StringBuilder ret = new StringBuilder();

		if (request != null)
		{
			ret.append("MalformedBundlerRequestException when processing the request '" + request + "'.\n");
			if (getCharacterNumber() >= 0)
			{
				addExceptionMessage(ret, request, getCharacterNumber());
			}
		}
		else
		{
			ret.append("MalformedBundlerRequestException.\n");
		}

		String message = getMessage();
		if (message != null && message.length() > 0)
		{
			ret.append(message + "\n");
		}

		ret.append("The stack trace for the exception is below.\n");
		StackTraceElement[] stackTrace = getStackTrace();
		for (StackTraceElement traceElement : stackTrace)
		{
			ret.append("\t" + traceElement.toString() + "\n");
		}

		return ret.toString();
	}

	private void addExceptionMessage(StringBuilder sb, String request, int characterNumber)
	{
		String invalidCharacterInfoString = String.format("Invalid character at position %d: ", characterNumber);
		int lengthOfSubstringBeforeRequest = invalidCharacterInfoString.length();
		sb.append(invalidCharacterInfoString).append(request).append("\n");
		sb.append(padLeft("^", lengthOfSubstringBeforeRequest + characterNumber)).append("\n");
	}

	private String padLeft(String s, int n)
	{
		return String.format("%1$" + n + "s", s);
	}

}
