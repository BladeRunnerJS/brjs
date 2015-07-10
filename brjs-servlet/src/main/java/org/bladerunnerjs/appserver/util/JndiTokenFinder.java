package org.bladerunnerjs.appserver.util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JndiTokenFinder implements TokenFinder
{
	private final Context appServerContext;

	public JndiTokenFinder() throws NamingException
	{
		this.appServerContext = (Context) new InitialContext();
	}

	/* this constructor is only used for testing */
	public JndiTokenFinder(Context appServerContext) {
		this.appServerContext = appServerContext;
	}

	public String findTokenValue(String tokenName)
	{
		if (tokenName == null || tokenName.length() < 1)
		{
			return null;
		}

		try
		{
			Object tokenValue = appServerContext.lookup("java:comp/env/" + tokenName);
			if (tokenValue != null)
			{
				return tokenValue.toString();
			}
			return "";
		}
		catch (NamingException ex)
		{
			return null;
		}
	}
}
