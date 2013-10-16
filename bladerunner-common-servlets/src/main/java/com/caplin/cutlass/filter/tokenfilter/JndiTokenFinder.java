package com.caplin.cutlass.filter.tokenfilter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JndiTokenFinder
{
	private final Context appServerContext;

	public JndiTokenFinder() throws NamingException
	{		
		this.appServerContext = (Context) new InitialContext();
	}

	/* this constructor is only used for testing */
	protected JndiTokenFinder(Context appServerContext) {
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
			Object theToken = appServerContext.lookup("java:comp/env/" + tokenName);
			if (theToken != null)
			{
				return theToken.toString();
			}
		}
		catch (NamingException ex)
		{
			return null;
		}
		return null;
	}

}
