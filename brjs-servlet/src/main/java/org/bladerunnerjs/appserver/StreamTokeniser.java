package org.bladerunnerjs.appserver;

import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StreamTokeniser
{
	public static final char tokenStart = '@';
	public static final char tokenEnd = '@';
	public static final String APP_VERSION_TOKEN = "APP.VERSION";
	
	private static final String VERSION_TIMESTAMP_PREFIX = "v_";
	private static final String VERSION_TIMESTAMP_FORMAT = "yyyyMMddHHmmss";
	
	private static DateFormat dateFormat = new SimpleDateFormat(VERSION_TIMESTAMP_FORMAT);
	
	public StringBuffer replaceTokens(Reader input, JndiTokenFinder tokenFinder, String requestUri) throws IOException
	{
		StringBuffer output = new StringBuffer();
		boolean withinToken = false;
		boolean continueProcessing = true;
		StringBuffer tokenString = new StringBuffer();

		int charCode = -1;
		char c;

		do
		{
			try
			{
				charCode = input.read();
			}
			catch (IOException ex)
			{
				throw new IOException("Error getting next character from input");
			}
			c = (char) charCode;

			if ((continueProcessing = (charCode > -1)))
			{
				if (withinToken)
				{
					withinToken = processPossbileTokenCharacter(tokenFinder, output, withinToken, tokenString, c, requestUri);
				}
				else
				{
					if (c == tokenStart)
					{
						withinToken = true;
						tokenString = new StringBuffer();
						tokenString.append(c);
					}
					else
					{
						output.append(c);
					}
				}
			}
		}
		while (continueProcessing);

		if (withinToken)
		{
			output.append(tokenString.toString());
		}

		return output;
	}

	private boolean processPossbileTokenCharacter(JndiTokenFinder tokenFinder, StringBuffer output, boolean withinToken, StringBuffer tokenString, char c, String requestUri)
	{
		if (isValidTokenChar(c))
		{
			tokenString.append(c);
		}
		else if (c == tokenEnd)
		{
			withinToken = processEndOfToken(tokenFinder, output, tokenString, c, requestUri);
		}
		else
		{
			withinToken = false;
			output.append(tokenString);
			output.append(c);
		}
		return withinToken;
	}

	private boolean processEndOfToken(JndiTokenFinder tokenFinder, StringBuffer output, StringBuffer tokenString, char c, String requestUri)
	{
		boolean withinToken;
		withinToken = false;
		tokenString.append(c);
		if (tokenString.length() <= 2)
		{
			output.append(tokenString);
		}
		else
		{
			String tokenReplacement = findTokenReplacement(tokenString.toString(), tokenFinder, requestUri);
			if (tokenReplacement != null)
			{
				output.append(tokenReplacement);
			}
			else
			{
				throw new IllegalArgumentException("No replacement found for token " + tokenString);
			}
		}
		return withinToken;
	}

	private boolean isValidTokenChar(char c)
	{
		return Character.isUpperCase(c) || c == '.';
	}

	private String findTokenReplacement(String tokenName, JndiTokenFinder tokenFinder, String requestUri)
	{
		tokenName = tokenName.substring(1, tokenName.length() - 1);
		String tokenReplacement = tokenFinder.findTokenValue(tokenName);
		if (tokenName.toString().equals(APP_VERSION_TOKEN))
		{
			if (tokenReplacement == null)
			{
				tokenReplacement = VERSION_TIMESTAMP_PREFIX+getAppVersionTimestamp();
			}
			
			if (!tokenReplacement.endsWith("/"))
			{
				tokenReplacement += "/";
			}
			
			tokenReplacement = requestUri + "/" + tokenReplacement;
		}
		
		return tokenReplacement;
	}

	public static String getAppVersionTimestamp()
	{
		Date now = new Date();
		return dateFormat.format(now);
	}
	
}
