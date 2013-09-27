package com.caplin.cutlass.filter.versionfilter;

import java.util.Arrays;

public class VersionRedirector
{

	private static char FIRST_CHAR_OF_VERSION_STRING = 'v';
	private static char SECOND_CHAR_OF_VERSION_STRING = '_';

	public String getRedirectedUrl(String urlPath)
	{
		String version = extractVersionStringFromPath(urlPath);
		if (version == null)
		{
			return urlPath;
		}

		return urlPath.replaceFirst(version + "/", "");
	}

	private String extractVersionStringFromPath(String urlPath)
	{
		String[] urlPathSplit = urlPath.split("/");

		for (int i : Arrays.asList(0, 1))
		{
			if (urlPathSplit.length > i)
			{
				String possibleVersionString = urlPathSplit[i];
				if (isVersionString(possibleVersionString))
				{
					return possibleVersionString;
				}
			}
		}

		return null;
	}

	private boolean isVersionString(String theString)
	{
		if (theString.length() < 2)
		{
			return false;
		}
		for (int i = 0; i < theString.length(); i++)
		{
			char thisChar = theString.charAt(i);
			if (i == 0)
			{
				if (!isFirstCharOfVersionString(thisChar))
				{
					return false;
				}
			}
			else if (i == 1)
			{
				if (!isSecondCharOfVersionString(thisChar))
				{
					return false;
				}
			}
			else
			{
				if (!isValidVersionChar(thisChar))
				{
					return false;
				}
			}
		}
		return true;
	}

	private boolean isValidVersionChar(char theChar)
	{
		return Character.isDigit(theChar);
	}

	private boolean isFirstCharOfVersionString(char theChar)
	{
		return Character.toLowerCase(theChar) == FIRST_CHAR_OF_VERSION_STRING;
	}

	private boolean isSecondCharOfVersionString(char theChar)
	{
		return Character.toLowerCase(theChar) == SECOND_CHAR_OF_VERSION_STRING;
	}

}
