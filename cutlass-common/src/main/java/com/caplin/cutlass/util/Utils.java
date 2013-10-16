package com.caplin.cutlass.util;

import java.io.IOException;


public class Utils
{

	public static void ensureCharactersDontMatchMacLineEndings(char previousChar, char nextChar) throws IOException
	{
		if (previousChar == '\r' && nextChar != '\n')
		{
			throw new IOException("Mac line endings detected. This type of line ending is not supported.");
		}
	}
	
}
